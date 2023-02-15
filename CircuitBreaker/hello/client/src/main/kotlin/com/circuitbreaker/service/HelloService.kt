package com.circuitbreaker.service

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.timelimiter.annotation.TimeLimiter
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import java.time.Duration
import java.util.concurrent.CompletableFuture
import kotlin.RuntimeException
import kotlin.random.Random

@Service
class HelloService(
    private val circuitBreakerRegistry: CircuitBreakerRegistry,
    @Value("\${api.request-url}") private val url: String,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    private final val circuitBreakerConfig = CircuitBreakerConfig.custom()
        .failureRateThreshold(50.0f)
        .waitDurationInOpenState(Duration.ofSeconds(1))
        .permittedNumberOfCallsInHalfOpenState(1)
        .build()
    val circuitBreaker = io.github.resilience4j.circuitbreaker.CircuitBreaker.of("testA", circuitBreakerConfig)
    val circuitBreakerBackendA = io.github.resilience4j.circuitbreaker.CircuitBreaker.ofDefaults("backendA")

    val client = WebClient.builder()
        .baseUrl("http://localhost:8090")
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build()

    val restTemplate: RestTemplate = RestTemplateBuilder()
        .setConnectTimeout(Duration.ofSeconds(3))
        .setReadTimeout(Duration.ofSeconds(5))
        .build()
    val headers = HttpHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)

    @CircuitBreaker(name = "backendA", fallbackMethod = "helloFallback")
//    @CircuitBreaker(name = "backendA")
    fun fail(): Mono<String> {
        return client.get()
            .uri("/fail")
            .retrieve()
            .bodyToMono(String::class.java)
    }

    //    @TimeLimiter(name = "backendA", fallbackMethod = "timeoutFallback")
    @CircuitBreaker(name = "backendA", fallbackMethod = "helloFallback")
    fun timeout(): Mono<String> {
        return client.get()
            .uri("/timeout")
            .retrieve()
            .bodyToMono(String::class.java)
    }

    //    @CircuitBreaker(name = "backendA", fallbackMethod = "helloFallback")
    @CircuitBreaker(name = "backendA", fallbackMethod = "timeoutFallback")
    fun test(): Mono<String> {
        return client.get()
            .uri("/test")
            .retrieve()
            .bodyToMono(String::class.java)
            .onErrorResume {
                log.error("error message = {}", it.message)
                Mono.empty()
            }
    }

    @CircuitBreaker(name = "backendA", fallbackMethod = "helloFallback")
    fun success(): Mono<String> {
        return client.get()
            .uri("/success")
            .retrieve()
            .bodyToMono(String::class.java)
    }

    @CircuitBreaker(name = "backendA", fallbackMethod = "helloFallback")
    fun alwaysFail(): Mono<String> {

        return client.get()
            .uri("/alwaysFail")
            .retrieve()
            .bodyToMono(String::class.java)
            .onErrorMap { e -> log.error(e.message); e }
    }

    @CircuitBreaker(name = "backendA", fallbackMethod = "restTemplateFallback")
    fun alwaysFailRestTemplate(): String {
        println("url = $url")
        val result = restTemplate.exchange("http://localhost:8090/alwaysFail", HttpMethod.GET, null, String::class.java)
        log.info("result = {}", result)
        log.info("result.body = {}", result.body)
        return result.body.toString()
    }

    @CircuitBreaker(name = "backendB", fallbackMethod = "restTemplateFallback")
    fun alwaysFailRestTemplate2(): String {


        try {
            log.info("try 블록 안")
            val result = restTemplate.exchange("http://localhost:8090/alwaysFail", HttpMethod.GET, null, String::class.java)
            log.info("result = {}", result)
            log.info("result.body = {}", result.body)
            return result.body.toString()
        } catch (e: RuntimeException) {
            log.error("e.message = {}", e.message)
        }
        return "error"
    }

    // circuit 이 열려 호출이 차단될 때와 실패할 때의 예외가 다르기 때문에 예외의 타입 출력
    private fun helloFallback(t: Throwable): Mono<String> {

        return Mono.just("fallback invoked! exception type : ${t.javaClass}")
    }

    private fun restTemplateFallback(t: Throwable): String {
        log.info("restTemplateFallback 실행")
        return "fallback invoked! exception type: ${t.javaClass}"
    }

    private fun timeoutFallback(t: Throwable): Mono<String> {

        return Mono.just("timeout fallback invoked! exception type : ${t.javaClass}")
    }
}
