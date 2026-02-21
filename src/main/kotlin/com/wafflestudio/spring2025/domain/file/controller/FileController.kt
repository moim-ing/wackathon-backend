package com.wafflestudio.spring2025.domain.file.controller

import com.wafflestudio.spring2025.domain.auth.LoggedInUser
import com.wafflestudio.spring2025.domain.file.dto.FileResponse
import com.wafflestudio.spring2025.domain.file.service.FileService
import com.wafflestudio.spring2025.domain.user.model.User
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/file")
class FileController(
    private val fileService: FileService,
) {
    @Operation(summary = "음원 업로드", description = "서버 저장소에 음원 파일을 업로드합니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "파일 업로드 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청 (파일 누락/형식 오류 등)"),
            ApiResponse(responseCode = "401", description = "인증 실패 (유효하지 않은 토큰)"),
        ],
    )
    @PostMapping(
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
    )
    fun uploadAudio(
        @Parameter(hidden = true) @LoggedInUser user: User?,
        @RequestPart("file") file: MultipartFile,
        @Parameter(description = "파일을 저장할 상위 경로", required = false)
        @RequestParam(name = "prefix", required = false)
        prefix: String?,
    ): ResponseEntity<FileResponse> {
        val response = fileService.uploadAudio(ownerId = user?.id, file = file, prefix = prefix)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "음원 URL 조회", description = "세션 ID로 해당 세션의 음원 presigned URL을 반환합니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "URL 조회 성공"),
            ApiResponse(responseCode = "404", description = "세션 없음 또는 음원 미준비"),
        ],
    )
    @GetMapping("/{sessionId}")
    fun getFileUrl(
        @Parameter(description = "조회할 세션의 ID", required = true)
        @PathVariable sessionId: Long,
    ): ResponseEntity<FileResponse> {
        val response = fileService.getFileUrl(sessionId)
        return ResponseEntity.ok(response)
    }
}
