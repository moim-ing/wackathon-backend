package com.wafflestudio.spring2025.domain.file.exception

import com.wafflestudio.spring2025.common.exception.DomainException

open class FileException(
    error: FileErrorCode,
    cause: Throwable? = null,
) : DomainException(
        httpErrorCode = error.httpStatusCode,
        code = error,
        title = error.title,
        msg = error.message,
        cause = cause,
    )

class FileValidationException(
    error: FileErrorCode,
) : FileException(error = error)
