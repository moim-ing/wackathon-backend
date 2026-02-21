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
                .contentType(contentType)
                .build()

        s3Client.putObject(req, RequestBody.fromInputStream(input, input.available().toLong()))
        return key
    }

    fun generateKey(
        prefix: String,
        filename: String,
    ): String {
        val safe = URLEncoder.encode(filename, StandardCharsets.UTF_8.toString())
        return "%s/%s-%s".format(prefix.trimEnd('/'), UUID.randomUUID().toString(), safe)
    }
}
