package com.circuitbreaker.service

import io.github.resilience4j.circuitbreaker.CallNotPermittedException
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.time.Duration
import kotlin.RuntimeException

@Service
class HelloService(
    private val restTemplate: RestTemplate,
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
    val circuitBreakerBackendA = circuitBreakerRegistry.circuitBreaker("backendA")

    val client = WebClient.builder()
        .baseUrl(url)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build()
    val headers = HttpHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)

    @CircuitBreaker(name = "backendA", fallbackMethod = "helloFallback")
    fun fail(): Mono<String> {
        return client.get()
            .uri("/fail")
            .retrieve()
            .bodyToMono(String::class.java)
    }

    @CircuitBreaker(name = "backendA", fallbackMethod = "helloFallback")
    fun timeout(): Mono<String> {
        return client.get()
            .uri("/timeout")
            .retrieve()
            .bodyToMono(String::class.java)
    }

    @CircuitBreaker(name = "backendA", fallbackMethod = "testFallback")
    fun test(): Mono<String> {
        return client
            .get()
            .uri("/test")
            .retrieve()
            .bodyToMono(String::class.java)
            .onErrorResume {
                log.error("error message = {}", it.message)
                Mono.empty()
            }
    }

    private fun testFallback(t: Throwable): Mono<String> {
        return Mono.just("fallback invoked! exception type : ${t.javaClass}")
    }

    private fun testFallback(e: CallNotPermittedException): Mono<String> {
        return Mono.just("circuit-breaker open fallback invoked! exception type : ${e.javaClass}")
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

    @CircuitBreaker(name = "backendA", fallbackMethod = "alwaysFailRestTemplateFallback")
    fun alwaysFailRestTemplate(id: String): String {
        println("alwaysFail call")
        return restTemplate.exchange("$url/alwaysFail", HttpMethod.GET, null, String::class.java).body.toString()
    }

    private fun alwaysFailRestTemplateFallback(e: CallNotPermittedException): String {
        println("fallback callNotPermittedException = $e")
        return "fallback invoked! CallNotPermittedException circuit state = ${circuitBreakerBackendA.state} exception type: ${e.javaClass}"
    }

    private fun alwaysFailRestTemplateFallback(t: Throwable): String {
        println("fallback t = $t")
        return "fallback invoked! Throwable  circuit state = ${circuitBreakerBackendA.state} exception type: ${t.javaClass}"
    }

    @CircuitBreaker(name = "backendA", fallbackMethod = "getDataFallback")
    fun getData(): String {
        return restTemplate.exchange("$url/getData", HttpMethod.GET, null, String::class.java).body.toString()
    }

    private fun getDataFallback(e: CallNotPermittedException): String {
        log.warn("fallback invoked! exception type: ${e.javaClass}")
        throw RuntimeException("Fallback call CircuitBreaker open, message = ${e.message}", e.cause)
    }

    private fun getDataFallback(t: Throwable): String {
        log.warn("fallback invoked! exception type: ${t.javaClass}")
        throw RuntimeException("message = ${t.message}", t.cause)
    }


    @CircuitBreaker(name = "backendB")
    fun alwaysFailRestTemplate2(): String {
        try {
            log.info("try 블록 안")
            val result =
                restTemplate.exchange("http://localhost:8090/alwaysFail", HttpMethod.GET, null, String::class.java)
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


}
