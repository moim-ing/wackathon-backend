package com.wafflestudio.spring2025.integration.fastapi.dto

data class CompareRequest(
    val source_key: String,
    val recording_key: String,
    val offset_milli: Long,
)
