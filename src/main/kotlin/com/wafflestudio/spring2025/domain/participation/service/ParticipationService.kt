package com.wafflestudio.spring2025.domain.participation.service

import com.wafflestudio.spring2025.domain.classes.repository.ClassRepository
import com.wafflestudio.spring2025.domain.participation.dto.ParticipationVerifyResponse
import com.wafflestudio.spring2025.domain.participation.exception.AttendanceNotVerifiedException
import com.wafflestudio.spring2025.domain.participation.exception.ClassNotFoundException
import com.wafflestudio.spring2025.domain.participation.exception.ParticipationErrorCode
import com.wafflestudio.spring2025.domain.participation.exception.ParticipationValidationException
import com.wafflestudio.spring2025.domain.participation.exception.SessionNotFoundException
import com.wafflestudio.spring2025.domain.participation.exception.SourceKeyNotReadyException
import com.wafflestudio.spring2025.domain.sessions.repository.SessionRepository
import com.wafflestudio.spring2025.integration.fastapi.FastApiClient
import com.wafflestudio.spring2025.integration.fastapi.FastApiException
import com.wafflestudio.spring2025.integration.fastapi.dto.CompareRequest
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class ParticipationService(
    private val sessionRepository: SessionRepository,
    private val classRepository: ClassRepository,
    private val fastApiClient: FastApiClient,
) {
    private val logger = LoggerFactory.getLogger(ParticipationService::class.java)

    fun verify(
        sessionId: Long,
        key: String,
        recordedAt: Long,
    ): ParticipationVerifyResponse {
        // 요청 검증 (400)
        if (key.isBlank()) {
            throw ParticipationValidationException(ParticipationErrorCode.INVALID_RECORDING_KEY)
        }
        if (recordedAt <= 0) {
            throw ParticipationValidationException(ParticipationErrorCode.INVALID_REGISTRATION_WINDOW)
        }

        // sessionId로 세션 찾기 (404)
        val session =
            sessionRepository.findById(sessionId).orElseThrow {
                SessionNotFoundException()
            }

        // 수업 조회 (404)
        val clazz =
            classRepository.findById(session.classId).orElseThrow {
                ClassNotFoundException()
            }

        val sourceKey = session.sourceKey ?: throw SourceKeyNotReadyException()

        val playStartTime = session.playStartTime
            ?: throw ParticipationValidationException(ParticipationErrorCode.INVALID_REGISTRATION_WINDOW)
        val offsetMilli = (recordedAt - playStartTime.toEpochMilli()).coerceAtLeast(0L)

        val compareResp =
            runBlocking {
                try {
                    fastApiClient.compare(
                        CompareRequest(
                            source_key = sourceKey,
                            recording_key = key,
                            offset_milli = offsetMilli,
                        ),
                    )
                } catch (e: FastApiException) {
                    logger.error("FastAPI compare failed: status=${e.statusCode} body=${e.responseBody}")
                    throw AttendanceNotVerifiedException()
                }
            }

        if (!compareResp.result) {
            throw AttendanceNotVerifiedException()
        }

        return ParticipationVerifyResponse(
            id = clazz.id!!, // classId
            title = clazz.title,
            sessionId = session.id!!,
            sessionTitle = session.title,
            videoId = session.videoId,
            verifiedAt = Instant.ofEpochMilli(recordedAt),
        )
    }
}
