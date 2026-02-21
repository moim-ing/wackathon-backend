package com.wafflestudio.spring2025.domain.participation.exception

import com.wafflestudio.spring2025.common.exception.DomainException

open class ParticipationException(
    error: ParticipationErrorCode,
    cause: Throwable? = null,
) : DomainException(
        httpErrorCode = error.httpStatusCode,
        code = error,
        title = error.title,
        msg = error.message,
        cause = cause,
    )

class ParticipationValidationException(
    error: ParticipationErrorCode,
) : ParticipationException(error = error)

class BannedUserCannotRegisterException : ParticipationException(error = ParticipationErrorCode.BANNED_USER_CANNOT_REGISTER)

class SessionNotFoundException : ParticipationException(error = ParticipationErrorCode.SESSION_NOT_FOUND)

class ClassNotFoundException : ParticipationException(error = ParticipationErrorCode.CLASS_NOT_FOUND)
