package com.wafflestudio.spring2025.domain.sessions.service

import com.wafflestudio.spring2025.domain.participation.dto.ParticipantInfoResponse
import com.wafflestudio.spring2025.domain.participation.repository.ParticipationRepository
import com.wafflestudio.spring2025.domain.classes.repository.ClassRepository
import com.wafflestudio.spring2025.domain.sessions.dto.SessionCreateRequest
import com.wafflestudio.spring2025.domain.sessions.dto.SessionCreateResponse
import com.wafflestudio.spring2025.domain.sessions.dto.SessionDetailResponse
import com.wafflestudio.spring2025.domain.sessions.dto.SessionStatusResponse
import com.wafflestudio.spring2025.domain.sessions.dto.SessionStatusUpdateRequest
import com.wafflestudio.spring2025.domain.sessions.entity.Session
import com.wafflestudio.spring2025.domain.sessions.entity.SessionStatus
import com.wafflestudio.spring2025.domain.sessions.repository.SessionRepository
import com.wafflestudio.spring2025.domain.user.model.User
import com.wafflestudio.spring2025.integration.fastapi.FastApiClient
import com.wafflestudio.spring2025.integration.fastapi.dto.ExtractMusicRequest
import kotlinx.coroutines.runBlocking
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime
import java.time.ZoneId

@Service
class SessionService(
    private val sessionRepository: SessionRepository,
    private val participationRepository: ParticipationRepository,
    private val classRepository: ClassRepository,
    private val fastApiClient: FastApiClient,
) {
    fun createSession(
        classId: Long,
        request: SessionCreateRequest,
        user: User?,
    ): SessionCreateResponse {
        if (user == null) throw ResponseStatusException(HttpStatus.UNAUTHORIZED)

        val session =
            Session(
                classId = classId,
                videoId = request.videoId,
                status = SessionStatus.ACTIVE,
            )
        val saved = sessionRepository.save(session)

        try {
            val prepareReq = ExtractMusicRequest(url = request.videoId)
            val prepareResp = runBlocking { fastApiClient.extractMusic(prepareReq) }

            // ⚠️ 여기서 FastAPI 응답 필드명이 "referenceS3Key"인 상태라면 그대로 쓰고,
            // 나중에 FastAPI를 "sourceKey"로 바꾸면 이 줄만 바꾸면 됨.
            val sourceKeyFromFastApi = prepareResp.referenceS3Key

            saved.sourceKey = sourceKeyFromFastApi // ✅ DB: sessions.source_key 저장
            saved.status = SessionStatus.ACTIVE
            sessionRepository.save(saved)

            return SessionCreateResponse(
                sessionId = saved.id!!,
                sourceKey = sourceKeyFromFastApi, // ✅ 프론트에 내려줌
            )
        } catch (ex: Exception) {
            saved.status = SessionStatus.CLOSED
            sessionRepository.save(saved)
            throw ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Failed to prepare source: ${ex.message}",
            )
        }
    }

    fun getSessionDetail(
        classId: Long,
        sessionId: Long,
    ): SessionDetailResponse {
        val session =
            sessionRepository.findById(sessionId).orElseThrow {
                ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found")
            }
        if (session.classId != classId) throw ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found")

        val participants = participationRepository.findBySessionId(sessionId)
        val participantDtos =
            participants.map { p ->
                ParticipantInfoResponse(
                    id = p.id!!,
                    name = p.guestName ?: "user",
                    participatedAt = LocalDateTime.ofInstant(p.verifiedAt, ZoneId.of("UTC")),
                )
            }

        return SessionDetailResponse(
            sessionId = session.id!!,
            sessionTitle = "%d주차".format(session.id),
            videoId = session.videoId,
            status = session.status,
            createdAt = LocalDateTime.ofInstant(session.createdAt ?: java.time.Instant.EPOCH, ZoneId.of("UTC")),
            totalParticipants = participants.size,
            participants = participantDtos,
        )
    }

    fun updateSessionStatus(
        classId: Long,
        sessionId: Long,
        req: SessionStatusUpdateRequest,
        user: User?,
    ): SessionStatusResponse {
        val requesterId = user?.id ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        val clazz =
            classRepository.findById(classId).orElseThrow {
                ResponseStatusException(HttpStatus.NOT_FOUND, "Class not found")
            }
        if (clazz.ownerId != requesterId) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Only class owner can update session status")
        }

        val session =
            sessionRepository.findById(sessionId).orElseThrow {
                ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found")
            }
        if (session.classId != classId) throw ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found")

        session.status = req.status
        val saved = sessionRepository.save(session)
        return SessionStatusResponse(
            currentStatus = saved.status,
        )
    }
}
