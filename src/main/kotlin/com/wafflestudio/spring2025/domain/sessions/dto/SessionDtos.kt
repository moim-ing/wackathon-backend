package com.wafflestudio.spring2025.domain.sessions.dto

import java.time.LocalDateTime

data class SessionCreateRequest(
    val videoId: String,
)

data class SessionCreateResponse(
    val sessionId: Long,
    val sessionTitle: String,
)

data class SessionDetailResponse(
    val sessionId: Long,
    val sessionTitle: String,
    val videoId: String,
    val status: String,
    val createdAt: LocalDateTime,
    val totalParticipants: Int,
    val participants: List<com.wafflestudio.spring2025.domain.participation.dto.ParticipantInfoResponse> = emptyList(),
)

data class SessionStatusUpdateRequest(
    val status: String,
)

data class SessionStatusResponse(
    val currentStatus: String,
    val updatedAt: LocalDateTime,
)
