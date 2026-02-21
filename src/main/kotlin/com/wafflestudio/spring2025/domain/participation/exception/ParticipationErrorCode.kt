package com.wafflestudio.spring2025.domain.participation.exception

import com.wafflestudio.spring2025.common.exception.DomainErrorCode
import org.springframework.http.HttpStatus

enum class ParticipationErrorCode(
    override val httpStatusCode: HttpStatus,
    override val title: String,
    override val message: String,
) : DomainErrorCode {
    // 400 Bad Request
    WRONG_GUEST_NAME(
        httpStatusCode = HttpStatus.BAD_REQUEST,
        title = "이름이 올바르지 않습니다.",
        message = "출석에 사용할 이름이 올바르지 않습니다.\n다시 입력해 주세요.",
    ),
    WRONG_GUEST_EMAIL(
        httpStatusCode = HttpStatus.BAD_REQUEST,
        title = "이메일이 올바르지 않습니다.",
        message = "출석에 사용할 이메일이 올바르지 않습니다.\n다시 입력해 주세요.",
    ),
    INVALID_REGISTRATION_WINDOW(
        httpStatusCode = HttpStatus.BAD_REQUEST,
        title = "출석 가능 시간이 아닙니다.",
        message = "현재는 출석을 진행할 수 없는 시간입니다.\n출석 가능 시간을 확인해 주세요.",
    ),
    INVALID_RECORDING_KEY(
        httpStatusCode = HttpStatus.BAD_REQUEST,
        title = "녹음 키가 올바르지 않습니다.",
        message = "유효하지 않은 녹음 키입니다.\n다시 시도해 주세요.",
    ),
    INVALID_OFFSET_MILLI(
        httpStatusCode = HttpStatus.BAD_REQUEST,
        title = "오프셋 값이 올바르지 않습니다.",
        message = "유효하지 않은 오프셋 값입니다.\n다시 시도해 주세요.",
    ),
    ATTENDANCE_NOT_VERIFIED(
        httpStatusCode = HttpStatus.BAD_REQUEST,
        title = "출석이 확인되지 않았습니다.",
        message = "오디오가 일치하지 않아 출석을 인정할 수 없습니다.",
    ),

    // 403 Forbidden
    BANNED_USER_CANNOT_REGISTER(
        httpStatusCode = HttpStatus.FORBIDDEN,
        title = "출석이 제한된 사용자입니다.",
        message = "출석이 제한된 사용자는 해당 세션에 참여할 수 없습니다.",
    ),

    // 404 Not Found
    SESSION_NOT_FOUND(
        httpStatusCode = HttpStatus.NOT_FOUND,
        title = "세션을 찾을 수 없습니다.",
        message = "요청한 출석 세션이 존재하지 않습니다.",
    ),
    CLASS_NOT_FOUND(
        httpStatusCode = HttpStatus.NOT_FOUND,
        title = "수업을 찾을 수 없습니다.",
        message = "요청한 수업 정보를 찾을 수 없습니다.",
    ),
    SOURCE_KEY_NOT_READY(
        httpStatusCode = HttpStatus.INTERNAL_SERVER_ERROR,
        title = "세션 소스 키가 준비되지 않았습니다.",
        message = "세션 음원 준비가 완료되지 않았습니다.\n잠시 후 다시 시도해 주세요.",
    ),
}
