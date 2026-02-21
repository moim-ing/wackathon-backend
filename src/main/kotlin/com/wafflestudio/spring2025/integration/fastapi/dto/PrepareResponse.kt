package com.wafflestudio.spring2025.integration.fastapi.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class PrepareResponse(
    @JsonProperty("key")
    val referenceS3Key: String,
)
