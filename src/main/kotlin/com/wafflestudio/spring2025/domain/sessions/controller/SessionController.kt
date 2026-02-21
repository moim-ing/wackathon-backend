package com.wafflestudio.spring2025.domain.sessions.controller

import com.wafflestudio.spring2025.domain.auth.AuthRequired
import com.wafflestudio.spring2025.domain.auth.LoggedInUser
import com.wafflestudio.spring2025.domain.sessions.dto.SessionCreateRequest
import com.wafflestudio.spring2025.domain.sessions.dto.SessionCreateResponse
import com.wafflestudio.spring2025.domain.sessions.dto.SessionDetailResponse
import com.wafflestudio.spring2025.domain.sessions.dto.SessionStatusResponse
import com.wafflestudio.spring2025.domain.sessions.dto.SessionStatusUpdateRequest
import com.wafflestudio.spring2025.domain.sessions.service.SessionService
import com.wafflestudio.spring2025.domain.user.model.User
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/classes/{classId}/sessions")
class SessionController(
    private val sessionService: SessionService,
) {
    @PostMapping
    fun createSession(
        @PathVariable classId: Long,
        @RequestBody request: SessionCreateRequest,
        @LoggedInUser user: User?,
    ): ResponseEntity<SessionCreateResponse> {
        val resp = sessionService.createSession(classId, request, user)
        return ResponseEntity.status(HttpStatus.OK).body(resp)
    }

    @GetMapping("/{sessionId}")
    fun getSessionDetail(
        @PathVariable classId: Long,
        @PathVariable sessionId: Long,
        @LoggedInUser user: User?,
    ): ResponseEntity<SessionDetailResponse> {
        val resp = sessionService.getSessionDetail(classId, sessionId)
        return ResponseEntity.ok(resp)
    }

    @PatchMapping("/{sessionId}/status")
    @AuthRequired
    fun updateStatus(
        @PathVariable classId: Long,
        @PathVariable sessionId: Long,
        @RequestBody req: SessionStatusUpdateRequest,
        @LoggedInUser user: User?,
    ): ResponseEntity<SessionStatusResponse> {
        val resp = sessionService.updateSessionStatus(classId, sessionId, req, user)
        return ResponseEntity.ok(resp)
    }
}
