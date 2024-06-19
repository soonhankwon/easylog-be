package dev.easylogbe.oauth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.easylogbe.oauth.domain.OauthUserInfoImpl;

public record OauthLoginResponse(
        String email,
        String nickname,
        @JsonProperty(value = "profile_img_url")
        String profileImgUrl,
        @JsonProperty(value = "refresh_token")
        String refreshToken,
        @JsonProperty(value = "is_registered")
        boolean isRegistered
) {
    public static OauthLoginResponse of(OauthUserInfoImpl oauthUserInfo, String refreshToken, boolean isRegistered) {
        return new OauthLoginResponse(
                oauthUserInfo.getEmail(),
                oauthUserInfo.getNickname(),
                oauthUserInfo.getProfileImgUrl(),
                refreshToken,
                isRegistered
        );
    }
}
