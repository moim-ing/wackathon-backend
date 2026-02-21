package com.wafflestudio.spring2025.domain.participation.dto

import java.time.Instant
import java.time.LocalDateTime

data class ParticipationCreateRequest(
    val name: String?,
    val verifiedAt: LocalDateTime,
)

data class ParticipationCreateResponse(
    val id: Long,
)

data class ParticipantInfoResponse(
    val id: Long,
    val name: String,
    val participatedAt: LocalDateTime,
)

data class ParticipationVerifyResponse(
    val id: Long,
    val title: String,
    val sessionId: Long,
    val sessionTitle: String,
    val videoId: String,
    val verifiedAt: Instant,
)

data class ParticipationVerifyRequest(
    val audioFile: String,
    val recordedAt: Long,
)
