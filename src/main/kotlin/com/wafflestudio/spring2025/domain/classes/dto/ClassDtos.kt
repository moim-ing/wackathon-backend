package com.wafflestudio.spring2025.domain.classes.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.wafflestudio.spring2025.domain.sessions.entity.SessionStatus
import java.time.Instant

// Request
data class ClassCreateRequest(
    val title: String,
)

// Response
data class ClassCreateResponse(
    val id: Long,
)

data class ClassDetailResponse(
    @field:JsonProperty("class")
    val clazz: ClassInfoResponse,
    val sessions: List<SessionInfoResponse>,
    val currentSessions: SessionInfoResponse?,
)

data class ClassInfoResponse(
    val id: Long,
    val title: String,
)

data class SessionInfoResponse(
    val sessionId: Long,
    val sessionTitle: String,
    val videoId: String,
    val status: SessionStatus,
    val createdAt: Instant,
    val totalParticipants: Int,
)

data class MyClassesResponse(
    val classes: List<MyClassSummary>,
)

data class MyClassSummary(
    val id: Long,
    val title: String,
)
