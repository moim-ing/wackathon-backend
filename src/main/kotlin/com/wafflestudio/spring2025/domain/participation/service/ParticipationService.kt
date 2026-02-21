package com.wafflestudio.spring2025.domain.participation.service

import com.wafflestudio.spring2025.domain.classes.repository.ClassRepository
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
    /**
     * /api/participation/verify 출석 인증 API
     *
     * 1) sessionId로 Session 조회
     * 2) session.sourceKey(기준 음원) 확보
     * 3) 녹음파일 S3 업로드 -> recordingKey
     * 4) FastAPI compare(source_key, recording_key, offset_milli)
     * 5) 성공 시 participation 저장(원하면 제거 가능)
     * 6) ParticipationVerifyResponse 반환
     */
    fun verifyAttendance(
        sessionId: Long,
        audioFile: MultipartFile,
        recordedAt: Long,
        offsetMilli: Long,
        user: User?,
    ): ParticipationVerifyResponse {
        val session =
            sessionRepository.findById(sessionId).orElseThrow {
                ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found")
            }

        // 기준 음원 준비 여부 (sessions.source_key)
        val sourceKey =
            session.sourceKey
                ?: throw ResponseStatusException(HttpStatus.CONFLICT, "Session reference not ready")

        if (session.status != SessionStatus.ACTIVE) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Session is not ACTIVE")
        }

        // 녹음 파일 업로드
        val recordingKey =
            s3Service.uploadInputStream(
                prefix = "recordings/session-$sessionId",
                filename = audioFile.originalFilename ?: "recording.wav",
                input = audioFile.inputStream,
                contentType = audioFile.contentType ?: "audio/wav",
            )

        // FastAPI compare 호출 (반드시 소통)
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
                // FastAPI가 죽었거나 timeout/502 등
                throw ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "FastAPI compare failed: ${e.message}",
                )
            }

        if (!compareResp.result) {
            // 출석 인증 실패
            // 정책상 401/403 중 택1: 보통 인증 실패는 401, 금지는 403
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Audio verification failed")
        }

        // (선택) 인증 성공이면 participation 저장
        val participation =
            Participation(
                sessionId = sessionId,
                userId = user?.id,
                guestName = null, // 필요하면 프론트에서 name 받아서 저장하도록 확장
                verifiedAt = Instant.now(), // 서버 시간
            )
        participationRepository.save(participation)

        // class 정보 붙여서 응답
        val clazz =
            classRepository.findById(session.classId).orElseThrow {
                ResponseStatusException(HttpStatus.NOT_FOUND, "Class not found")
            }

        return ParticipationVerifyResponse(
            id = clazz.id!!, // classId
            title = clazz.title,
            sessionId = session.id!!,
            sessionTitle = "%d주차".format(session.id),
            videoId = session.videoId,
            verifiedAt = Instant.ofEpochMilli(recordedAt), // 명세대로 recordedAt을 응답에 넣음
        )
    }
}
