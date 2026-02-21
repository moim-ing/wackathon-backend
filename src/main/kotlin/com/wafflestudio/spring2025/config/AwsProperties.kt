package com.wafflestudio.spring2025.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "aws")
data class AwsProperties(
    val region: String = "us-east-1",
    val s3Bucket: String = "",
)
