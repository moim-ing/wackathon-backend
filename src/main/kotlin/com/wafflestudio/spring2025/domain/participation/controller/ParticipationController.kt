package com.wafflestudio.spring2025.domain.participation.controller

import com.wafflestudio.spring2025.domain.participation.dto.ParticipationVerifyResponse
import com.wafflestudio.spring2025.domain.participation.service.ParticipationService
import org.springframework.http.ResponseEntity
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
     * POST /api/participation/verify
     * Content-Type: multipart/form-data
     *
     * form-data:
     * - audioFile: String (예: "hHHQ4bNhwjU")  // videoId로 사용
     * - recordedAt: Long (epoch millis)
     */
    @PostMapping("/verify", consumes = ["multipart/form-data"])
    fun verify(
        @RequestParam("audioFile") audioFile: String,
        @RequestParam("recordedAt") recordedAt: Long,
    ): ResponseEntity<ParticipationVerifyResponse> {
        val resp = participationService.verify(audioFile, recordedAt)
        return ResponseEntity.ok(resp)
    }
}