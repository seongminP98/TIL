package com.circuitbreaker.controller

import com.circuitbreaker.service.HelloService
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class HelloController(
    private val helloService: HelloService,
    private val circuitBreakerRegistry: CircuitBreakerRegistry,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @GetMapping("/fail")
    fun fail(): Mono<String> {
        return helloService.fail()
    }

    @GetMapping("/timeout")
    fun timeout(): Mono<String> {
        return helloService.timeout()
    }

    @GetMapping("/test")
    fun test(): Mono<String> {
        return helloService.test()
    }

    @GetMapping("/success")
    fun success(): Mono<String> {
        return helloService.success()
    }

    @GetMapping("/alwaysFail")
    fun alwaysFail(): String {

        return helloService.alwaysFailRestTemplate("zzz")
    }

    @GetMapping("/alwaysFail2")
    fun alwaysFail2(): String {

        println(circuitBreakerRegistry.allCircuitBreakers.size())
        val circuitBreakerA: CircuitBreaker = circuitBreakerRegistry.circuitBreaker("backendA")
        val circuitBreakerB: CircuitBreaker = circuitBreakerRegistry.circuitBreaker("backendB")
        println("circuitBreakerA name = ${circuitBreakerA.name} state = ${circuitBreakerA.state}")
        println("circuitBreakerB name = ${circuitBreakerB.name} state = ${circuitBreakerB.state}")


        return helloService.alwaysFailRestTemplate2()

    }
}
