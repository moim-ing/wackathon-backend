package com.wafflestudio.spring2025.domain.participation.controller

import com.wafflestudio.spring2025.domain.auth.LoggedInUser
import com.wafflestudio.spring2025.domain.participation.dto.ParticipationVerifyRequest
import com.wafflestudio.spring2025.domain.participation.dto.ParticipationVerifyResponse
import com.wafflestudio.spring2025.domain.participation.service.ParticipationService
import com.wafflestudio.spring2025.domain.user.model.User
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/participation")
class ParticipationController(
    private val participationService: ParticipationService,
) {
    /**
     * POST /api/participation/verify
     * Content-Type: application/json
     *
     * body:
     * - audioFile: String (S3 recording key)
     * - recordedAt: Long (epoch millis)
     */
    @PostMapping("/verify", consumes = ["application/json"])
    fun verify(
        @RequestBody request: ParticipationVerifyRequest,
        @LoggedInUser user: User?,
    ): ResponseEntity<ParticipationVerifyResponse> {
        val resp =
            participationService.verifyAttendance(
                recordingKey = request.audioFile,
                recordedAt = request.recordedAt,
                user = user,
            )
        return ResponseEntity.ok(resp)
    }
}
