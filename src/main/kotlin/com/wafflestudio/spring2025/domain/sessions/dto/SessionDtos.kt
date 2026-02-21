package com.wafflestudio.spring2025.domain.sessions.dto

import com.wafflestudio.spring2025.domain.sessions.entity.SessionStatus
import java.time.LocalDateTime

data class SessionCreateRequest(
    val sessionTitle: String? = null,
    val videoUrl: String,
)

data class SessionCreateResponse(
    val sessionId: Long,
)

data class SessionDetailResponse(
    val sessionId: Long,
    val sessionTitle: String,
    val videoId: String,
    val status: SessionStatus,
    val createdAt: LocalDateTime,
    val totalParticipants: Int,
    val participants: List<com.wafflestudio.spring2025.domain.participation.dto.ParticipantInfoResponse> = emptyList(),
)

data class SessionStatusUpdateRequest(
    val status: SessionStatus,
    val currentTime: Int,
    val updatedAt: Long,
)

data class SessionStatusResponse(
    val currentStatus: SessionStatus,
)
