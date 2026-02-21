package com.wafflestudio.spring2025.domain.sessions.exception

import com.wafflestudio.spring2025.common.exception.DomainException

open class SessionException(
    error: SessionErrorCode,
    cause: Throwable? = null,
) : DomainException(
        httpErrorCode = error.httpStatusCode,
        code = error,
        title = error.title,
        msg = error.message,
        cause = cause,
    )

class SessionAuthenticationRequiredException : SessionException(error = SessionErrorCode.AUTHENTICATION_REQUIRED)

class SessionClassNotFoundException : SessionException(error = SessionErrorCode.CLASS_NOT_FOUND)

class SessionNotFoundException : SessionException(error = SessionErrorCode.SESSION_NOT_FOUND)

class SessionStatusUpdateForbiddenException : SessionException(error = SessionErrorCode.SESSION_STATUS_UPDATE_FORBIDDEN)

class SessionSourcePrepareFailedException(
    cause: Throwable? = null,
) : SessionException(error = SessionErrorCode.SOURCE_PREPARE_FAILED, cause = cause)
