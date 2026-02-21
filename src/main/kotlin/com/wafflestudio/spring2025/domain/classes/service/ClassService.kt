package com.wafflestudio.spring2025.domain.classes.service

import com.wafflestudio.spring2025.domain.classes.dto.ClassCreateRequest
import com.wafflestudio.spring2025.domain.classes.dto.ClassCreateResponse
import com.wafflestudio.spring2025.domain.classes.dto.ClassDetailResponse
import com.wafflestudio.spring2025.domain.classes.dto.ClassInfoResponse
import com.wafflestudio.spring2025.domain.classes.dto.MyClassSummary
import com.wafflestudio.spring2025.domain.classes.dto.MyClassesResponse
import com.wafflestudio.spring2025.domain.classes.dto.SessionInfoResponse
import com.wafflestudio.spring2025.domain.classes.entity.Class
import com.wafflestudio.spring2025.domain.classes.exception.ClassNotFoundException
import com.wafflestudio.spring2025.domain.classes.exception.ClassUserNotFoundException
import com.wafflestudio.spring2025.domain.classes.repository.ClassRepository
import com.wafflestudio.spring2025.domain.participation.repository.ParticipationRepository
import com.wafflestudio.spring2025.domain.sessions.repository.SessionRepository
import com.wafflestudio.spring2025.domain.user.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class ClassService(
    private val classRepository: ClassRepository,
    private val userRepository: UserRepository,
    private val sessionRepository: SessionRepository,
    private val participationRepository: ParticipationRepository,
) {
    fun createClass(
        request: ClassCreateRequest,
        userId: Long,
    ): ClassCreateResponse {
        // verify user exists
        val userExists = userRepository.existsById(userId)
        if (!userExists) {
            throw ClassUserNotFoundException()
        }

        val clazz =
            Class(
                title = request.title,
                ownerId = userId,
            )

        val saved = classRepository.save(clazz)
        return ClassCreateResponse(id = saved.id!!)
    }

    fun getClassDetail(classId: Long): ClassDetailResponse {
        val clazz =
            classRepository.findById(classId).orElseThrow { ClassNotFoundException() }

        val sessions = sessionRepository.findByClassId(classId)
        val sessionDtos =
            sessions.map { session ->
                val count = participationRepository.countBySessionId(session.id!!)
                SessionInfoResponse(
                    sessionId = session.id!!,
                    sessionTitle = session.title,
                    videoId = session.videoId,
                    status = session.status,
                    createdAt = session.createdAt ?: java.time.Instant.EPOCH,
                    totalParticipants = count,
                )
            }

        // current session: pick most recent by createdAt Instant
        val currentSession = sessionDtos.maxByOrNull { it.createdAt }

        val classInfo =
            ClassInfoResponse(
                id = clazz.id!!,
                title = clazz.title,
            )

        return ClassDetailResponse(
            clazz = classInfo,
            sessions = sessionDtos,
            currentSession = currentSession,
        )
    }

    fun getMyClasses(userId: Long): MyClassesResponse {
        val userExists = userRepository.existsById(userId)
        if (!userExists) {
            throw ClassUserNotFoundException()
        }

        val classes =
            classRepository
                .findByOwnerId(userId)
                .sortedBy { it.id ?: Long.MAX_VALUE }
                .map { clazz ->
                    MyClassSummary(
                        id = clazz.id!!,
                        title = clazz.title,
                    )
                }

        return MyClassesResponse(classes = classes)
    }
}
