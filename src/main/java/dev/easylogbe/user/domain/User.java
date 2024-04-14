package dev.easylogbe.user.domain;

import dev.easylogbe.common.domain.BaseTimeEntity;
import dev.easylogbe.oauth.enumtype.OauthProvider;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "`user`")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    // Email 로그인을 지원한다면 OauthProvider 에 NONE 추가 필요
    @Enumerated(value = EnumType.STRING)
    @Column(name = "oauth_login_type", nullable = false)
    private OauthProvider oauthProvider;

    @Column(name = "profile_image")
    private String profileImage;

    private User(String email, OauthProvider oauthProvider, String profileImage) {
        this.email = email;
        this.oauthProvider = oauthProvider;
        this.profileImage = profileImage;
    }
}
