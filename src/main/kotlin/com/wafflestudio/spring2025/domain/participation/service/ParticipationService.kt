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
import com.wafflestudio.spring2025.integration.fastapi.dto.CompareRequest
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class ParticipationService(
    private val sessionRepository: SessionRepository,
    private val classRepository: ClassRepository,
    private val fastApiClient: FastApiClient,
) {
    fun verify(
        audioFile: String,
        recordingKey: String,
        offsetMilli: Long,
        recordedAt: Long,
    ): ParticipationVerifyResponse {
        // 요청 검증 (400)
        if (audioFile.isBlank()) {
            throw ParticipationValidationException(ParticipationErrorCode.WRONG_GUEST_NAME)
        }
        if (recordingKey.isBlank()) {
            throw ParticipationValidationException(ParticipationErrorCode.INVALID_RECORDING_KEY)
        }
        if (offsetMilli < 0) {
            throw ParticipationValidationException(ParticipationErrorCode.INVALID_OFFSET_MILLI)
        }
        if (recordedAt <= 0) {
            throw ParticipationValidationException(ParticipationErrorCode.INVALID_REGISTRATION_WINDOW)
        }

        // audioFile == videoId로 세션 찾기 (404)
        val session =
            sessionRepository.findByVideoId(audioFile)
                ?: throw SessionNotFoundException()

        // 수업 조회 (404)
        val clazz =
            classRepository.findById(session.classId).orElseThrow {
                ClassNotFoundException()
            }

        val sourceKey = session.sourceKey ?: throw SourceKeyNotReadyException()

        val compareResp =
            runBlocking {
                fastApiClient.compare(
                    CompareRequest(
                        source_key = sourceKey,
                        recording_key = recordingKey,
                        offset_milli = offsetMilli,
                    ),
                )
            }

        if (!compareResp.result) {
            throw AttendanceNotVerifiedException()
        }

        return ParticipationVerifyResponse(
            id = clazz.id!!, // classId
            title = clazz.title,
            sessionId = session.id!!,
            sessionTitle = "%d주차".format(session.id),
            videoId = session.videoId,
            verifiedAt = Instant.ofEpochMilli(recordedAt),
        )
    }
}
