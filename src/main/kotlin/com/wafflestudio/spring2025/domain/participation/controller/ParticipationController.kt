package com.wafflestudio.spring2025.domain.participation.controller

import com.wafflestudio.spring2025.domain.participation.dto.ParticipationVerifyResponse
import com.wafflestudio.spring2025.domain.participation.service.ParticipationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class VerifyRequest(val key: String, val recordedAt: Long)

@RestController
@RequestMapping("/api/participation")
class ParticipationController(
    private val participationService: ParticipationService,
) {
    /**
     * POST /api/participation/verify/{sessionId}
     * Content-Type: application/json
     *
     * body:
     * - key: String // 학생 녹음 s3 key
     * - recordedAt: Long (epoch millis)
     */
    @PostMapping("/verify/{sessionId}")
    fun verify(
        @PathVariable sessionId: Long,
        @RequestBody req: VerifyRequest,
    ): ResponseEntity<ParticipationVerifyResponse> {
        val resp = participationService.verify(sessionId, req.key, req.recordedAt)
        return ResponseEntity.ok(resp)
    }
}
