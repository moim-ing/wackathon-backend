package com.wafflestudio.spring2025.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "fastapi")
data class FastApiProperties(
    val baseUrl: String = "http://localhost:8000",
    val apiKey: String = "",
    val timeoutSeconds: Long = 10,
)
