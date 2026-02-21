package com.wafflestudio.spring2025.integration.fastapi

class FastApiException(
    message: String,
    val statusCode: Int? = null,
    val responseBody: String? = null,
) : RuntimeException(message)
