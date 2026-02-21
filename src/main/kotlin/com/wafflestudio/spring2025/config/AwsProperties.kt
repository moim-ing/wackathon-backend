package com.wafflestudio.spring2025.config

import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

@Validated
@ConfigurationProperties(prefix = "aws")
data class AwsProperties(
    @NotBlank val region: String = "ap-northeast-2",
    @NotBlank val s3Bucket: String,
)
