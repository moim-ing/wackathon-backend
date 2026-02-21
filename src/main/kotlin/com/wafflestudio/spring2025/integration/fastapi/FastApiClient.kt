package com.wafflestudio.spring2025.integration.fastapi

import com.wafflestudio.spring2025.config.FastApiProperties
import com.wafflestudio.spring2025.integration.fastapi.dto.CompareRequest
import com.wafflestudio.spring2025.integration.fastapi.dto.CompareResponse
import com.wafflestudio.spring2025.integration.fastapi.dto.ExtractMusicRequest
import com.wafflestudio.spring2025.integration.fastapi.dto.PrepareResponse
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.time.Duration

@Component
class FastApiClient(
    private val webClient: WebClient,
    private val props: FastApiProperties,
) {
    private val base = webClient.mutate().baseUrl(props.baseUrl).build()

    suspend fun extractMusic(req: ExtractMusicRequest): PrepareResponse =
        base
            .post()
            .uri("/extract_music")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .header("x-api-key", props.apiKey)
            .body(Mono.just(req), ExtractMusicRequest::class.java)
            .retrieve()
            .bodyToMono(PrepareResponse::class.java)
            .timeout(Duration.ofSeconds(props.timeoutSeconds))
            .awaitSingle()

    suspend fun compare(req: CompareRequest): CompareResponse =
        base
            .post()
            .uri("/compare")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .header("x-api-key", props.apiKey)
            .body(Mono.just(req), CompareRequest::class.java)
            .retrieve()
            .bodyToMono(CompareResponse::class.java)
            .timeout(Duration.ofSeconds(props.timeoutSeconds))
            .awaitSingle()
}
