package com.circuitbreaker.service

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.lang.RuntimeException
import kotlin.random.Random

@Service
class HelloService(
) {

    @CircuitBreaker(name = "backendA", fallbackMethod = "helloFallback")
    fun fail(): Mono<String> {
        println("Client hello service")
        val client = WebClient.builder()
            .baseUrl("http://localhost:8090")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build()

        return client.get()
            .uri("/fail")
            .retrieve()
            .bodyToMono(String::class.java)
    }

    @CircuitBreaker(name = "backendA", fallbackMethod = "helloFallback")
    fun timeout(): Mono<String> {
        println("Client hello service")
        val client = WebClient.builder()
            .baseUrl("http://localhost:8090")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build()

        return client.get()
            .uri("/timeout")
            .retrieve()
            .bodyToMono(String::class.java)
    }

    // circuit 이 열려 호출이 차단될 때와 실패할 때의 예외가 다르기 때문에 예외의 타입 출력
    private fun helloFallback(t: Throwable): Mono<String> {

        return Mono.just("fallback invoked! exception type : ${t.javaClass}")
    }
}
