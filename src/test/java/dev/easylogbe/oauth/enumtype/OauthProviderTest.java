package dev.easylogbe.oauth.enumtype;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName(value = "OauthProvider Enum 유닛테스트")
class OauthProviderTest {

    @Test
    @DisplayName(value = "getter 메서드 테스트")
    void getValue() {
        OauthProvider google = OauthProvider.GOOGLE;
        assertThat(google.getValue()).isEqualTo("google");
    }

    @Test
    @DisplayName(value = "values 메서드 테스트: 현재 사이즈 2")
    void values() {
        OauthProvider[] values = OauthProvider.values();
        assertThat(values.length).isEqualTo(2);
    }

    @Test
    @DisplayName(value = "valueOf 메서드 테스트: 대문자로 호출해야 함")
    void valueOf() {
        assertThatThrownBy(() -> OauthProvider.valueOf("google"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}