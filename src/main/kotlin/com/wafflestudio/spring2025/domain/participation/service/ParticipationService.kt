package com.wafflestudio.spring2025.domain.participation.service

import com.wafflestudio.spring2025.domain.classes.repository.ClassRepository
import com.wafflestudio.spring2025.domain.participation.dto.ParticipationCreateRequest
import com.wafflestudio.spring2025.domain.participation.dto.ParticipationCreateResponse
import com.wafflestudio.spring2025.domain.participation.dto.ParticipationVerifyResponse
import com.wafflestudio.spring2025.domain.participation.entity.Participation
import com.wafflestudio.spring2025.domain.participation.repository.ParticipationRepository
import com.wafflestudio.spring2025.domain.sessions.repository.SessionRepository
import com.wafflestudio.spring2025.domain.user.model.User
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class ParticipationService(
    private val participationRepository: ParticipationRepository,
    private val sessionRepository: SessionRepository,
    private val classRepository: ClassRepository,
) {
    fun registerParticipation(
        classId: Long,
        sessionId: Long,
        req: ParticipationCreateRequest,
        user: User?,
    ): ParticipationCreateResponse {
        // validate session exists and belongs to class
        val session =
            sessionRepository.findById(sessionId).orElseThrow {
                ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found")
            }
        if (session.classId != classId) throw ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found")

        val participation =
            Participation(
                sessionId = sessionId,
                userId = user?.id,
                guestName = req.name,
                verifiedAt = req.verifiedAt.toInstant(java.time.ZoneOffset.UTC),
            )
        val saved = participationRepository.save(participation)
        return ParticipationCreateResponse(id = saved.id!!)
    }

    fun verifyAudioAndRespond(audioFile: String): ParticipationVerifyResponse {
        // This is a stub for audio verification logic. For now, return a dummy
        // In reality this would call an ML service and match to a session/class
        throw ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "Audio verification not implemented")
    }
}
