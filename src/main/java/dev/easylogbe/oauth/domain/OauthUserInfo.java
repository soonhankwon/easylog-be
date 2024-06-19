package dev.easylogbe.oauth.domain;

public interface OauthUserInfo {
    String getProviderId();

    String getProvider();

    String getEmail();

    String getNickname();

    String getProfileImgUrl();
}
