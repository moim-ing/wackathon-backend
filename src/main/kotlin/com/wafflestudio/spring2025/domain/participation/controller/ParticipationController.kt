package com.wafflestudio.spring2025.domain.participation.controller

import com.wafflestudio.spring2025.domain.participation.dto.ParticipationVerifyResponse
import com.wafflestudio.spring2025.domain.participation.service.ParticipationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/participation")
class ParticipationController(
    private val participationService: ParticipationService,
) {
    /**
     * POST /api/participation/verify/{sessionId}
     * Content-Type: multipart/form-data
     *
     * form-data:
     * - key: String // 학생 녹음 s3 key
     * - recordedAt: Long (epoch millis)
     */
    @PostMapping("/verify/{sessionId}")
    fun verify(
        @PathVariable sessionId: Long,
        @RequestParam("key") key: String,
        @RequestParam("recordedAt") recordedAt: Long,
    ): ResponseEntity<ParticipationVerifyResponse> {
        val resp = participationService.verify(sessionId, key, recordedAt)
        return ResponseEntity.ok(resp)
    }
}
