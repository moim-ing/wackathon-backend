package com.wafflestudio.spring2025.domain.participation.controller

import com.wafflestudio.spring2025.domain.auth.LoggedInUser
import com.wafflestudio.spring2025.domain.participation.dto.ParticipationVerifyResponse
import com.wafflestudio.spring2025.domain.participation.service.ParticipationService
import com.wafflestudio.spring2025.domain.user.model.User
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/participation")
class ParticipationController(
    private val participationService: ParticipationService,
) {
    /**
     * POST /api/participation/verify
     * Content-Type: multipart/form-data
     *
     * form-data:
     * - sessionId: Long
     * - audioFile: MultipartFile (녹음 파일)
     * - recordedAt: Long (epoch millis)
     * - offsetMilli: Long (ms)  // FastAPI compare에 필요
     *
     * (선택) 로그인 사용자라면 @LoggedInUser user로 userId 저장 가능
     */
    @PostMapping("/verify", consumes = ["multipart/form-data"])
    fun verify(
        @RequestParam("sessionId") sessionId: Long,
        @RequestParam("audioFile") audioFile: MultipartFile,
        @RequestParam("recordedAt") recordedAt: Long,
        @RequestParam("offsetMilli") offsetMilli: Long,
        @LoggedInUser user: User?,
    ): ResponseEntity<ParticipationVerifyResponse> {
        val resp =
            participationService.verifyAttendance(
                sessionId = sessionId,
                audioFile = audioFile,
                recordedAt = recordedAt,
                offsetMilli = offsetMilli,
                user = user,
            )
        return ResponseEntity.ok(resp)
    }
}
