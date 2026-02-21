package com.wafflestudio.spring2025.domain.participation.controller

import com.wafflestudio.spring2025.domain.auth.LoggedInUser
import com.wafflestudio.spring2025.domain.participation.dto.ParticipationCreateRequest
import com.wafflestudio.spring2025.domain.participation.dto.ParticipationCreateResponse
import com.wafflestudio.spring2025.domain.participation.service.ParticipationService
import com.wafflestudio.spring2025.domain.user.model.User
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/participation")
class ParticipationController(
    private val participationService: ParticipationService,
) {
    @PostMapping
    fun register(
        @RequestBody req: ParticipationCreateRequest,
        @LoggedInUser user: User?,
    ): ResponseEntity<ParticipationCreateResponse> {
        // This endpoint structure in spec is a bit different; assume class/session included in request
        // For now, user should include classId and sessionId in request (but dto not defined)
        throw UnsupportedOperationException("Use /api/classes/{classId}/sessions/{sessionId}/participants for registration")
    }

    @PostMapping("/verify")
    fun verify(
        @RequestBody body: Map<String, String>,
    ): ResponseEntity<Any> {
        val audio = body["audioFile"] ?: throw IllegalArgumentException("audioFile required")
        val resp = participationService.verifyAudioAndRespond(audio)
        return ResponseEntity.ok(resp)
    }
}
