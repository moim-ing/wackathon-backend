package com.wafflestudio.spring2025.domain.file.exception

import com.wafflestudio.spring2025.common.exception.DomainErrorCode
import org.springframework.http.HttpStatus

enum class FileErrorCode(
    override val httpStatusCode: HttpStatus,
    override val title: String,
    override val message: String,
) : DomainErrorCode {
    FILE_EMPTY(
        httpStatusCode = HttpStatus.BAD_REQUEST,
        title = "음원 파일이 비어있습니다.",
        message = "업로드한 파일이 비어있습니다. 파일을 다시 확인해 주세요.",
    ),
    FILE_TYPE_INVALID(
        httpStatusCode = HttpStatus.BAD_REQUEST,
        title = "지원하지 않는 음원 형식입니다.",
        message = "mp3, wav, flac, aac, ogg 파일만 업로드할 수 있습니다.",
    ),
    FILE_TOO_LARGE(
        httpStatusCode = HttpStatus.BAD_REQUEST,
        title = "음원 파일 크기가 너무 큽니다.",
        message = "음원 파일 크기는 100MB 이하로 제한되어 있습니다.",
    ),
    PREFIX_NOT_ALLOWED(
        httpStatusCode = HttpStatus.BAD_REQUEST,
        title = "허용되지 않는 업로드 경로입니다.",
        message = "지원하지 않는 업로드 경로입니다.",
    ),
    SESSION_NOT_FOUND(
        httpStatusCode = HttpStatus.NOT_FOUND,
        title = "세션을 찾을 수 없습니다.",
        message = "존재하지 않는 세션입니다.",
    ),
    SOURCE_NOT_READY(
        httpStatusCode = HttpStatus.NOT_FOUND,
        title = "음원이 준비되지 않았습니다.",
        message = "해당 세션의 음원 파일이 아직 준비되지 않았습니다.",
    ),
}
