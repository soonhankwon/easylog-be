package dev.easylogbe.oauth.enumtype;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OauthProvider {
    KAKAO("kakao"),
    GOOGLE("google");

    private final String value;
}
