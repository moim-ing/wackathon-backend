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
import com.wafflestudio.spring2025.domain.sessions.exception.SessionSourcePrepareFailedException
import com.wafflestudio.spring2025.domain.sessions.exception.SessionStatusUpdateForbiddenException
import com.wafflestudio.spring2025.domain.sessions.repository.SessionRepository
import com.wafflestudio.spring2025.domain.user.model.User
import com.wafflestudio.spring2025.integration.fastapi.FastApiClient
import com.wafflestudio.spring2025.integration.fastapi.dto.ExtractMusicRequest
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneId

@Service
class SessionService(
    private val sessionRepository: SessionRepository,
    private val participationRepository: ParticipationRepository,
    private val classRepository: ClassRepository,
    private val fastApiClient: FastApiClient,
) {
    private val logger = LoggerFactory.getLogger(SessionService::class.java)

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
            logger.error("FastAPI extractMusic failed for sessionId=${saved.id}: ${ex.message}", ex)
            saved.status = SessionStatus.CLOSED
            sessionRepository.save(saved)
            throw SessionSourcePrepareFailedException(ex)
        }
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
        val saved = sessionRepository.save(session)
        return SessionStatusResponse(
            currentStatus = saved.status,
        )
    }
}
