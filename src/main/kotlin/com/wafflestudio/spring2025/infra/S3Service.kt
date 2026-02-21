package com.wafflestudio.spring2025.infra

import com.wafflestudio.spring2025.config.AwsProperties
import org.springframework.stereotype.Service
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.InputStream
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.UUID

@Service
class S3Service(
    private val s3Client: S3Client,
    private val awsProperties: AwsProperties,
) {
    fun uploadInputStream(
        prefix: String,
        filename: String,
        input: InputStream,
        contentType: String?,
    ): String {
        val key = generateKey(prefix, filename)

        val req =
            PutObjectRequest
                .builder()
                .bucket(awsProperties.s3Bucket)
                .key(key)
                .contentType(contentType ?: "application/octet-stream")
                .build()

        // ✅ available() 대신 안전하게 전체를 읽어서 업로드
        val bytes = input.readBytes()
        s3Client.putObject(req, RequestBody.fromBytes(bytes))
        return key
    }

    fun generateKey(
        prefix: String,
        filename: String,
    ): String {
        // 운영에서 key가 깨지지 않게 filename을 안전하게 인코딩
        val safe = URLEncoder.encode(filename, StandardCharsets.UTF_8.toString())
        return "%s/%s-%s".format(prefix.trimEnd('/'), UUID.randomUUID().toString(), safe)
    }
}
