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
import org.springframework.web.reactive.function.client.bodyToMono
import java.time.Duration

@Component
class FastApiClient(
    webClient: WebClient,
    private val props: FastApiProperties,
) {
    private val base = webClient.mutate().baseUrl(props.baseUrl).build()

    suspend fun extractMusic(req: ExtractMusicRequest): PrepareResponse = post("/extract_music", req, PrepareResponse::class.java)

    suspend fun compare(req: CompareRequest): CompareResponse = post("/compare", req, CompareResponse::class.java)

    private suspend fun <T : Any> post(
        path: String,
        body: Any,
        clazz: Class<T>,
    ): T =
        base
            .post()
            .uri(path)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .retrieve()
            .onStatus({ it.is4xxClientError || it.is5xxServerError }) { resp ->
                resp
                    .bodyToMono<String>()
                    .defaultIfEmpty("")
                    .map { raw ->
                        FastApiException(
                            message = "FastAPI call failed: ${resp.statusCode()}",
                            statusCode = resp.statusCode().value(),
                            responseBody = raw,
                        )
                    }
            }.bodyToMono(clazz)
            .timeout(Duration.ofSeconds(props.timeoutSeconds))
            .awaitSingle()
}
