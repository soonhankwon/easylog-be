package dev.easylogbe.oauth.domain;

import static dev.easylogbe.oauth.util.OauthUserInfoRequestConst.EMAIL;
import static dev.easylogbe.oauth.util.OauthUserInfoRequestConst.GOOGLE_NICKNAME;
import static dev.easylogbe.oauth.util.OauthUserInfoRequestConst.GOOGLE_PROFILE_IMG_URL;
import static dev.easylogbe.oauth.util.OauthUserInfoRequestConst.ID;
import static dev.easylogbe.oauth.util.OauthUserInfoRequestConst.KAKAO_ACCOUNT;
import static dev.easylogbe.oauth.util.OauthUserInfoRequestConst.KAKAO_NICKNAME;
import static dev.easylogbe.oauth.util.OauthUserInfoRequestConst.KAKAO_PROFILE_IMG_URL;
import static dev.easylogbe.oauth.util.OauthUserInfoRequestConst.PROFILE;

import dev.easylogbe.oauth.enumtype.OauthProvider;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OauthUserInfoImpl implements OauthUserInfo{

    private final Map<String, Object> attributes;
    private final String provider;

    @Override
    public String getProviderId() {
        return String.valueOf(attributes.get(ID));
    }

    @Override
    public String getProvider() {
        String kakao = OauthProvider.KAKAO.getValue();
        if (this.provider.equals(kakao)) {
            return kakao;
        }
        return OauthProvider.GOOGLE.getValue();
    }

    @Override
    public String getEmail() {
        String kakao = OauthProvider.KAKAO.getValue();
        if (this.provider.equals(kakao)) {
            return (String) getKakaoAccount().get(EMAIL);
        }
        return (String) attributes.get(EMAIL);
    }

    @Override
    public String getNickname() {
        if (this.provider.equals(OauthProvider.KAKAO.getValue())) {
            return (String) getProfile().get(KAKAO_NICKNAME);
        }
        return (String) attributes.get(GOOGLE_NICKNAME);
    }

    @Override
    public String getProfileImgUrl() {
        if (this.provider.equals(OauthProvider.KAKAO.getValue())) {
            return (String) getProfile().get(KAKAO_PROFILE_IMG_URL);
        }
        return (String) attributes.get(GOOGLE_PROFILE_IMG_URL);
    }

    public Map<String, Object> getKakaoAccount() {
        return (Map<String, Object>) attributes.get(KAKAO_ACCOUNT);
    }

    public Map<String, Object> getProfile() {
        return (Map<String, Object>) getKakaoAccount().get(PROFILE);
    }
}
