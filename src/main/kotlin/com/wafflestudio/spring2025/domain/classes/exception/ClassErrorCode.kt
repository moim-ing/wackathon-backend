package com.wafflestudio.spring2025.domain.classes.exception

import com.wafflestudio.spring2025.common.exception.DomainErrorCode
import org.springframework.http.HttpStatus

enum class ClassErrorCode(
    override val httpStatusCode: HttpStatus,
    override val title: String,
    override val message: String,
) : DomainErrorCode {
    USER_NOT_FOUND(
        httpStatusCode = HttpStatus.UNAUTHORIZED,
        title = "사용자를 찾을 수 없습니다.",
        message = "로그인이 필요하거나 사용자 정보가 존재하지 않습니다.",
    ),
    CLASS_NOT_FOUND(
        httpStatusCode = HttpStatus.NOT_FOUND,
        title = "수업을 찾을 수 없습니다.",
        message = "요청한 수업 정보가 존재하지 않습니다.",
    ),
}
