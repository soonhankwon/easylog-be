package dev.easylogbe.oauth.service;

import dev.easylogbe.common.util.JwtProvider;
import dev.easylogbe.oauth.domain.OauthUserInfoImpl;
import dev.easylogbe.oauth.dto.OauthTokenDTO;
import dev.easylogbe.oauth.dto.response.OauthLoginResponse;
import dev.easylogbe.user.domain.User;
import dev.easylogbe.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class OauthService {

    private final InMemoryClientRegistrationRepository clientRegistrationRepository;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    public OauthLoginResponse login(String provider, String code, HttpServletResponse httpServletResponse) {
        //TODO request dto validation 적용시 수정 필요
        if(provider == null || provider.isEmpty()) {
            throw new IllegalArgumentException("provider can't null or empty");
        }
        if(code == null || code.isEmpty()) {
            throw new IllegalArgumentException("code can't null or empty");
        }

        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(provider);
        OauthTokenDTO oauthToken = getOauthToken(code, clientRegistration);
        OauthUserInfoImpl oauthUserInfo = getUserInfoFromOauth(provider, oauthToken, clientRegistration);

        String email = oauthUserInfo.getEmail();
        Optional<User> optionalUser =
                userRepository.findByEmail(email);

        /*
         * 서비스에 등록된 유저인 경우 Access, Refresh 토큰을 발급해줌
         * Oauth 에 등록된 유저정보와 서비스에 가입된 유저인지 여부를 응답(API 스펙)
         */
        if(optionalUser.isPresent()) {
            User registerdUser = optionalUser.get();
            Long userId = registerdUser.getId();

            String accessToken = jwtProvider.createJwt(userId, true);
            httpServletResponse.setHeader(HttpHeaders.AUTHORIZATION, accessToken);

            String refreshToken = jwtProvider.createJwt(userId, false);
            return OauthLoginResponse.of(oauthUserInfo, refreshToken, true);
        }
        // 등록되지 않은 유저라면 토큰을 발급하지 않고, Oauth2 유저정보와 가입되지 않았다는 flag 만 리턴
        return OauthLoginResponse.of(oauthUserInfo, null, false);
    }

    /**
     * @param code oauth2 url 로 부터 발급받은 code
     * @param clientRegistration YML 에 등록된 provider(kakao, google) Oauth2 메타데이터 레지스트리
     * @return 발급된 code 로 oauth2 api 에 요청을 보내 oauthToken 을 리턴
     */
    private OauthTokenDTO getOauthToken(String code, ClientRegistration clientRegistration) {
        return WebClient.create()
                .post()
                .uri(clientRegistration.getProviderDetails().getTokenUri())
                .headers(header -> {
                    header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                    header.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));
                })
                .bodyValue(createOauthRequestBody(code, clientRegistration))
                .retrieve()
                .bodyToMono(OauthTokenDTO.class)
                .block();
    }

    /**
     * @param code oauth2 url 로 부터 발급받은 code
     * @param clientRegistration YML 에 등록된 provider(kakao, google) Oauth2 메타데이터 레지스트리
     * @return oauth2 api 요청 바디를 리턴
     */
    private MultiValueMap<String, String> createOauthRequestBody(String code, ClientRegistration clientRegistration) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("code", code);
        formData.add("grant_type", "authorization_code");
        formData.add("redirect_uri", clientRegistration.getRedirectUri());
        formData.add("client_secret", clientRegistration.getClientSecret());
        formData.add("client_id", clientRegistration.getClientId());
        return formData;
    }

    private OauthUserInfoImpl getUserInfoFromOauth(String provider, OauthTokenDTO oauthToken,
                                                   ClientRegistration clientRegistration) {
        Map<String, Object> userAttributes = getUserAttribute(clientRegistration, oauthToken);
        return new OauthUserInfoImpl(userAttributes, provider);
    }

    /**
     * @param clientRegistration YML 에 등록된 provider(kakao, google) Oauth2 메타데이터 레지스트리
     * @param oauthToken 외부 API 에서 발급받은 oauthToken
     * @return 발급받은 oauthToken 으로 등록된 oauth 유저 정보를 리턴
     */
    private Map<String, Object> getUserAttribute(ClientRegistration clientRegistration, OauthTokenDTO oauthToken) {
        return WebClient.create()
                .get()
                .uri(clientRegistration.getProviderDetails().getUserInfoEndpoint().getUri())
                .headers(header -> header.setBearerAuth(oauthToken.accessToken()))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .block();
    }
}
