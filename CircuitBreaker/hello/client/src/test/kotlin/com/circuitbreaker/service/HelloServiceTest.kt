package com.circuitbreaker.service

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerOnErrorEvent
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerOnStateTransitionEvent
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.web.client.RestTemplate

@SpringBootTest
class ServiceTest @Autowired constructor(
    private val helloService: HelloService,
    private val circuitBreakerRegistry: CircuitBreakerRegistry,

) {
    @Test
    fun testCircuitBreaker() {
        val circuitBreaker = circuitBreakerRegistry.circuitBreaker("backendA")
        circuitBreaker.eventPublisher
            .onStateTransition { event: CircuitBreakerOnStateTransitionEvent -> println("State transition: " + event.stateTransition) }
            .onError { event: CircuitBreakerOnErrorEvent -> println("Error: $event") }

        // Circuit Breaker 어노테이션을 사용하여 메서드에 적용
        for (i in 0..15) {
            println("circuitBreaker name = ${circuitBreaker.name} state = ${circuitBreaker.state}")
            try {
                helloService.alwaysFailRestTemplate("zzz")
                println("fail 호출")
            } catch (e: Exception) {
                println("test에서 Exception: " + e.message)
            }
        }

        assertThat(circuitBreaker.state).isEqualTo(CircuitBreaker.State.OPEN)
    }
}
