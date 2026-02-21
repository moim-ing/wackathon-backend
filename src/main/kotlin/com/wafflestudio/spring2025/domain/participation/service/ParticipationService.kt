package com.wafflestudio.spring2025.domain.participation.service

import com.wafflestudio.spring2025.domain.classes.repository.ClassRepository
import com.wafflestudio.spring2025.domain.participation.dto.ParticipationCreateRequest
import com.wafflestudio.spring2025.domain.participation.dto.ParticipationCreateResponse
import com.wafflestudio.spring2025.domain.participation.dto.ParticipationVerifyResponse
import com.wafflestudio.spring2025.domain.participation.entity.Participation
import com.wafflestudio.spring2025.domain.participation.repository.ParticipationRepository
import com.wafflestudio.spring2025.domain.sessions.entity.SessionStatus
import com.wafflestudio.spring2025.domain.sessions.repository.SessionRepository
import com.wafflestudio.spring2025.domain.user.model.User
import com.wafflestudio.spring2025.infra.S3Service
import com.wafflestudio.spring2025.integration.fastapi.FastApiClient
import com.wafflestudio.spring2025.integration.fastapi.dto.CompareRequest
import kotlinx.coroutines.runBlocking
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

@Service
class ParticipationService(
    private val participationRepository: ParticipationRepository,
    private val sessionRepository: SessionRepository,
    private val classRepository: ClassRepository,
    private val s3Service: S3Service,
    private val fastApiClient: FastApiClient,
) {
    fun registerParticipation(
        classId: Long,
        sessionId: Long,
        req: ParticipationCreateRequest,
        audioFile: MultipartFile,
        offsetMilli: Long,
        user: User?,
    ): ParticipationCreateResponse {
        // 1) 세션 검증
        val session =
            sessionRepository.findById(sessionId).orElseThrow {
                ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found")
            }
        if (session.classId != classId) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found")
        }

        // 2) referenceS3Key 준비 여부
        val sourceKey =
            session.sourceKey
                ?: throw ResponseStatusException(HttpStatus.CONFLICT, "Session reference not ready")

        if (session.status != SessionStatus.ACTIVE) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Session is not ACTIVE")
        }

        // 3) 학생 음성 S3 업로드 → recording_key
        val recordingKey =
            s3Service.uploadInputStream(
                prefix = "recordings/class-$classId/session-$sessionId",
                filename = audioFile.originalFilename ?: "recording.wav",
                input = audioFile.inputStream,
                contentType = audioFile.contentType ?: "audio/wav",
            )

        // 4) FastAPI compare 호출
        val compareResp =
            try {
                runBlocking {
                    fastApiClient.compare(
                        CompareRequest(
                            source_key = sourceKey,
                            recording_key = recordingKey,
                            offset_milli = offsetMilli,
                        ),
                    )
                }
            } catch (e: Exception) {
                throw ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "FastAPI compare failed: ${e.message}",
                )
            }

        if (!compareResp.result) {
            // 인증 실패: 401(혹은 403)로 내려도 됨
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Audio verification failed")
        }

        // 5) 성공이면 Participation 저장
        val participation =
            Participation(
                sessionId = sessionId,
                userId = user?.id,
                guestName = req.name,
                // req.verifiedAt을 신뢰하기보단 서버 시간으로 찍는 걸 권장
                verifiedAt = Instant.now(),
                // verifiedAt = req.verifiedAt.toInstant(ZoneOffset.UTC)  // 꼭 req를 쓰고 싶으면 이걸로
            )

        val saved = participationRepository.save(participation)
        return ParticipationCreateResponse(id = saved.id!!)
    }

    fun verifyAudioAndRespond(
        audioFile: String,
        recordedAt: Long,
    ): ParticipationVerifyResponse {
        val session =
            sessionRepository
                .findAll()
                .firstOrNull { it.videoId == audioFile }
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found")

        val clazz =
            classRepository.findById(session.classId).orElseThrow {
                ResponseStatusException(HttpStatus.NOT_FOUND, "Class not found")
            }

        return ParticipationVerifyResponse(
            id = clazz.id!!,
            title = clazz.title,
            sessionId = session.id!!,
            sessionTitle = "%d주차".format(session.id),
            videoId = session.videoId,
            verifiedAt = Instant.ofEpochMilli(recordedAt),
        )
    }
}
