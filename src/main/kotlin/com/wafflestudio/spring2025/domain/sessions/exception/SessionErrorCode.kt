package com.wafflestudio.spring2025.domain.sessions.exception

import com.wafflestudio.spring2025.common.exception.DomainErrorCode
import org.springframework.http.HttpStatus

enum class SessionErrorCode(
    override val httpStatusCode: HttpStatus,
    override val title: String,
    override val message: String,
) : DomainErrorCode {
    AUTHENTICATION_REQUIRED(
        httpStatusCode = HttpStatus.UNAUTHORIZED,
        title = "로그인이 필요합니다.",
        message = "비로그인 상태에서 이용할 수 없는 기능입니다.",
    ),
    CLASS_NOT_FOUND(
        httpStatusCode = HttpStatus.NOT_FOUND,
        title = "수업을 찾을 수 없습니다.",
        message = "요청한 수업 정보가 존재하지 않습니다.",
    ),
    SESSION_NOT_FOUND(
        httpStatusCode = HttpStatus.NOT_FOUND,
        title = "세션을 찾을 수 없습니다.",
        message = "요청한 세션 정보가 존재하지 않습니다.",
    ),
    SESSION_STATUS_UPDATE_FORBIDDEN(
        httpStatusCode = HttpStatus.FORBIDDEN,
        title = "세션 상태를 변경할 권한이 없습니다.",
        message = "수업 소유자만 세션 상태를 변경할 수 있습니다.",
    ),
    SOURCE_PREPARE_FAILED(
        httpStatusCode = HttpStatus.INTERNAL_SERVER_ERROR,
        title = "소스 준비에 실패했습니다.",
        message = "세션 생성에 필요한 소스 준비에 실패했습니다.",
    ),
}
