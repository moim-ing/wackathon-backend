package com.wafflestudio.spring2025.domain.file.service

import com.wafflestudio.spring2025.config.AwsProperties
import com.wafflestudio.spring2025.domain.file.dto.FileResponse
import com.wafflestudio.spring2025.domain.file.exception.FileErrorCode
import com.wafflestudio.spring2025.domain.file.exception.FileException
import com.wafflestudio.spring2025.domain.file.exception.FileValidationException
import com.wafflestudio.spring2025.domain.sessions.repository.SessionRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
import java.time.Duration
import java.util.UUID

@Service
class FileService(
    private val s3Client: S3Client,
    private val presigner: S3Presigner,
    private val awsProps: AwsProperties,
    private val sessionRepository: SessionRepository,
) {
    fun uploadAudio(
        ownerId: Long?,
        file: MultipartFile,
        prefix: String?,
    ): FileResponse {
        validateAudio(file)
        validatePrefix(prefix)

        val directory = sanitizePrefix(prefix)
        val ext = extractExtension(file.originalFilename)
        val key = listOf(directory, ownerId?.toString() ?: "new_user", "${UUID.randomUUID()}$ext").joinToString(separator = "/")

        val putRequest =
            PutObjectRequest
                .builder()
                .bucket(awsProps.s3Bucket)
                .key(key)
                .contentType(file.contentType ?: "application/octet-stream")
                .build()

        file.inputStream.use { inputStream ->
            s3Client.putObject(
                putRequest,
                RequestBody.fromInputStream(inputStream, file.size),
            )
        }

        val url = presignedGetUrl(key)
        return FileResponse(key = key, url = url)
    }

    fun getFileUrl(sessionId: Long): FileResponse {
        val session =
            sessionRepository.findById(sessionId).orElseThrow {
                FileException(FileErrorCode.SESSION_NOT_FOUND)
            }
        val sourceKey = session.sourceKey ?: throw FileException(FileErrorCode.SOURCE_NOT_READY)
        val url = presignedGetUrl(sourceKey)
        return FileResponse(key = sourceKey, url = url)
    }

    fun deleteAudio(key: String) {
        s3Client.deleteObject(
            DeleteObjectRequest
                .builder()
                .bucket(awsProps.s3Bucket)
                .key(key)
                .build(),
        )
    }

    private fun presignedGetUrl(key: String): String {
        val getRequest =
            GetObjectRequest
                .builder()
                .bucket(awsProps.s3Bucket)
                .key(key)
                .build()

        val presignRequest =
            GetObjectPresignRequest
                .builder()
                .signatureDuration(Duration.ofSeconds(awsProps.presignExpireSeconds))
                .getObjectRequest(getRequest)
                .build()

        return presigner.presignGetObject(presignRequest).url().toString()
    }

    private fun validateAudio(file: MultipartFile) {
        if (file.isEmpty) {
            throw FileValidationException(FileErrorCode.FILE_EMPTY)
        }
        val contentType = file.contentType ?: ""
        if (!contentType.startsWith("audio/") && contentType !in ALLOWED_CONTENT_TYPES) {
            throw FileValidationException(FileErrorCode.FILE_TYPE_INVALID)
        }
        if (file.size > MAX_AUDIO_BYTES) {
            throw FileValidationException(FileErrorCode.FILE_TOO_LARGE)
        }
    }

    private fun validatePrefix(prefix: String?) {
        val trimmed = prefix?.trim()?.trim('/') ?: return
        if (trimmed !in ALLOWED_PREFIXES) {
            throw FileValidationException(FileErrorCode.PREFIX_NOT_ALLOWED)
        }
    }

    private fun sanitizePrefix(prefix: String?): String {
        val trimmed = prefix?.trim()?.takeIf { it.isNotEmpty() } ?: DEFAULT_PREFIX
        return trimmed.trim('/').ifEmpty { DEFAULT_PREFIX }
    }

    private fun extractExtension(originalFilename: String?): String {
        if (originalFilename.isNullOrBlank()) return DEFAULT_EXTENSION
        val lastDot = originalFilename.lastIndexOf('.')
        if (lastDot == -1) return DEFAULT_EXTENSION
        val ext = originalFilename.substring(lastDot).lowercase()
        return if (ext in ALLOWED_EXTENSIONS) ext else DEFAULT_EXTENSION
    }

    companion object {
        private const val DEFAULT_PREFIX = "audio"
        private const val DEFAULT_EXTENSION = ".mp3"
        private const val MAX_AUDIO_BYTES = 100L * 1024 * 1024
        private val ALLOWED_EXTENSIONS = setOf(".mp3", ".wav", ".flac", ".aac", ".ogg")
        private val ALLOWED_CONTENT_TYPES = setOf("application/octet-stream")
        private val ALLOWED_PREFIXES = setOf("audio")
    }
}
