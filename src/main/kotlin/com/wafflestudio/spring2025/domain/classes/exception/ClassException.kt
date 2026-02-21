package com.wafflestudio.spring2025.domain.classes.exception

import com.wafflestudio.spring2025.common.exception.DomainException

open class ClassException(
    error: ClassErrorCode,
    cause: Throwable? = null,
) : DomainException(
        httpErrorCode = error.httpStatusCode,
        code = error,
        title = error.title,
        msg = error.message,
        cause = cause,
    )

class ClassUserNotFoundException : ClassException(error = ClassErrorCode.USER_NOT_FOUND)

class ClassNotFoundException : ClassException(error = ClassErrorCode.CLASS_NOT_FOUND)
