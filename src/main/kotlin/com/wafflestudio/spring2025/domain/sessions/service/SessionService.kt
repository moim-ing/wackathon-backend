package com.wafflestudio.spring2025.domain.sessions.service

import com.wafflestudio.spring2025.domain.classes.repository.ClassRepository
import com.wafflestudio.spring2025.domain.participation.dto.ParticipantInfoResponse
import com.wafflestudio.spring2025.domain.participation.repository.ParticipationRepository
import com.wafflestudio.spring2025.domain.sessions.dto.SessionCreateRequest
import com.wafflestudio.spring2025.domain.sessions.dto.SessionCreateResponse
import com.wafflestudio.spring2025.domain.sessions.dto.SessionDetailResponse
import com.wafflestudio.spring2025.domain.sessions.dto.SessionStatusResponse
import com.wafflestudio.spring2025.domain.sessions.dto.SessionStatusUpdateRequest
import com.wafflestudio.spring2025.domain.sessions.entity.Session
import com.wafflestudio.spring2025.domain.sessions.entity.SessionStatus
import com.wafflestudio.spring2025.domain.sessions.exception.SessionAuthenticationRequiredException
import com.wafflestudio.spring2025.domain.sessions.exception.SessionClassNotFoundException
import com.wafflestudio.spring2025.domain.sessions.exception.SessionNotFoundException
import com.wafflestudio.spring2025.domain.sessions.exception.SessionStatusUpdateForbiddenException
import com.wafflestudio.spring2025.domain.sessions.repository.SessionRepository
import com.wafflestudio.spring2025.domain.user.model.User
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@Service
class SessionService(
    private val sessionRepository: SessionRepository,
    private val participationRepository: ParticipationRepository,
    private val classRepository: ClassRepository,
) {
    fun createSession(
        classId: Long,
        request: SessionCreateRequest,
        user: User?,
    ): SessionCreateResponse {
        if (user == null) throw SessionAuthenticationRequiredException()

        val session =
            Session(
                classId = classId,
                videoId = request.videoId,
                title = request.sessionTitle,
                sourceKey = request.videoKey,
                status = SessionStatus.ACTIVE,
                playStartTime = Instant.now(),
            )
        val saved = sessionRepository.save(session)

        return SessionCreateResponse(
            sessionId = saved.id!!,
        )
    }

    fun getSessionDetail(
        classId: Long,
        sessionId: Long,
    ): SessionDetailResponse {
        val session =
            sessionRepository.findById(sessionId).orElseThrow { SessionNotFoundException() }
        if (session.classId != classId) throw SessionNotFoundException()

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
            sessionTitle = session.title,
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
        val requesterId = user?.id ?: throw SessionAuthenticationRequiredException()
        val clazz =
            classRepository.findById(classId).orElseThrow { SessionClassNotFoundException() }
        if (clazz.ownerId != requesterId) {
            throw SessionStatusUpdateForbiddenException()
        }

        val session =
            sessionRepository.findById(sessionId).orElseThrow { SessionNotFoundException() }
        if (session.classId != classId) throw SessionNotFoundException()

        session.status = req.status
        session.playStartTime = Instant.now().minusMillis(req.currentTime.toLong() * 1000)
        val saved = sessionRepository.save(session)
        return SessionStatusResponse(
            currentStatus = saved.status,
        )
    }
}
