package com.sm.webflux.controller

import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux

@WebFluxTest
class WebFluxTest {

    @Test
    fun testReactiveEndpoint() {
        val client = WebClient.create("http://localhost:8080")
        val startTime = System.currentTimeMillis()

        Flux.range(0, 100000)
            .flatMap {
                client.get()
                    .uri("/api/reactive/hello")
                    .retrieve()
                    .bodyToMono(String::class.java)
//                    .doOnSuccess { endTime ->
//                        val elapsedTime = System.currentTimeMillis() - startTime
//                        println("Request ${it + 1} completed in ${elapsedTime}ms: $endTime")
//                    }
            }
            .blockLast()
        val endTime = System.currentTimeMillis()
        println("time = ${endTime - startTime}")
    }

    @Test
    fun testServletEndPoint() {
        val client = WebClient.create("http://localhost:8080")
        val startTime = System.currentTimeMillis()

        Flux.range(0, 100000)
            .flatMap {
                client.get()
                    .uri("/api/hello")
                    .retrieve()
                    .bodyToMono(String::class.java)
//                    .doOnSuccess { endTime ->
//                        val elapsedTime = System.currentTimeMillis() - startTime
//                        println("Request ${it + 1} completed in ${elapsedTime}ms: $endTime")
//                    }
            }
            .blockLast()
        val endTime = System.currentTimeMillis()
        println("time = ${endTime - startTime}")
    }
}

