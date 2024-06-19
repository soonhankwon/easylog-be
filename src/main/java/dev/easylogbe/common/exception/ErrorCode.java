package dev.easylogbe.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    INTERNAL_SEVER_ERROR(5000, "서버 내부 예외입니다."),
    JWT_INVALID_SIGNATURE(4000, "잘못된 JWT 서명입니다."),
    JWT_EXPIRED(4001, "만료된 JWT 입니다.");

    private final int code;
    private final String message;
}
