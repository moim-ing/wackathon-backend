package com.wafflestudio.spring2025.config

import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

@Validated
@ConfigurationProperties(prefix = "fastapi")
data class FastApiProperties(
    @NotBlank val baseUrl: String = "http://localhost:8000",
    val apiKey: String = "",
    val timeoutSeconds: Long = 10,
)
