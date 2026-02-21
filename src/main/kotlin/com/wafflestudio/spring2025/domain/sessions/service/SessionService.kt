package com.wafflestudio.spring2025.domain.sessions.service

import com.wafflestudio.spring2025.domain.participation.dto.ParticipantInfoResponse
import com.wafflestudio.spring2025.domain.participation.repository.ParticipationRepository
import com.wafflestudio.spring2025.domain.sessions.dto.SessionCreateRequest
import com.wafflestudio.spring2025.domain.sessions.dto.SessionCreateResponse
import com.wafflestudio.spring2025.domain.sessions.dto.SessionDetailResponse
import com.wafflestudio.spring2025.domain.sessions.dto.SessionStatusResponse
import com.wafflestudio.spring2025.domain.sessions.dto.SessionStatusUpdateRequest
import com.wafflestudio.spring2025.domain.sessions.entity.Session
import com.wafflestudio.spring2025.domain.sessions.repository.SessionRepository
import com.wafflestudio.spring2025.domain.user.model.User
import com.wafflestudio.spring2025.domain.user.repository.UserRepository
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
    private val userRepository: UserRepository,
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
            )
        val saved = sessionRepository.save(session)

        // Prepare reference via FastAPI (synchronous call for MVP)
        try {
            val prepareReq = ExtractMusicRequest(url = request.videoId)
            val prepareResp = runBlocking { fastApiClient.extractMusic(prepareReq) }
            saved.referenceS3Key = prepareResp.referenceS3Key
            saved.status = "READY"
            sessionRepository.save(saved)
        } catch (ex: Exception) {
            // mark failed but keep session
            saved.status = "FAILED"
            sessionRepository.save(saved)
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to prepare reference: ${ex.message}")
        }

        return SessionCreateResponse(
            sessionId = saved.id!!,
            sessionTitle = "%d주차".format(saved.id),
        )
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
        val session =
            sessionRepository.findById(sessionId).orElseThrow {
                ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found")
            }
        if (session.classId != classId) throw ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found")

        session.status = req.status
        val saved = sessionRepository.save(session)
        return SessionStatusResponse(
            currentStatus = saved.status,
            updatedAt = LocalDateTime.ofInstant(saved.updatedAt ?: java.time.Instant.EPOCH, ZoneId.of("UTC")),
        )
    }
}
