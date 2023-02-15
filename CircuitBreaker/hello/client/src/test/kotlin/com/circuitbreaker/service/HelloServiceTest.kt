package com.circuitbreaker.service

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerOnErrorEvent
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerOnStateTransitionEvent
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ServiceTest @Autowired constructor(
    private val helloService: HelloService? = null,
    private val circuitBreakerRegistry: CircuitBreakerRegistry,
) {
    @Test
    fun testCircuitBreaker() {
        print(circuitBreakerRegistry.allCircuitBreakers.size())
        // Circuit Breaker open condition 설정
        val circuitBreaker = circuitBreakerRegistry.circuitBreaker("backendA")
        circuitBreaker.eventPublisher
            .onStateTransition { event: CircuitBreakerOnStateTransitionEvent -> println("State transition: " + event.stateTransition) }
            .onError { event: CircuitBreakerOnErrorEvent -> println("Error: $event") }

        // Circuit Breaker 어노테이션을 사용하여 메서드에 적용
        for (i in 0..15) {
            println("circuitBreaker name = ${circuitBreaker.name} state = ${circuitBreaker.state}")
            println(circuitBreaker.circuitBreakerConfig)
            try {
                helloService!!.alwaysFailRestTemplate()
                println("fail 호출")
            } catch (e: Exception) {
                println("Exception: " + e.message)
            }
        }

        // Circuit Breaker open인 경우, fallback 메서드가 호출되는지 확인
        try {
            println("open : ${helloService!!.alwaysFailRestTemplate()}")
            assertThat(circuitBreaker.state).isEqualTo(CircuitBreaker.State.OPEN)
        } catch (e: Exception) {
            println("Exception: " + e.message)
        }
    }
}
