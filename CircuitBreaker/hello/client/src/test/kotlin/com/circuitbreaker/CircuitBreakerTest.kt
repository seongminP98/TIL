package com.circuitbreaker

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.web.client.HttpServerErrorException
import java.time.Duration
import java.util.function.Predicate

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CircuitBreakerTest {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Test
    fun testCircuitBreaker() {
        val circuitBreakerConfig = CircuitBreakerConfig.custom()
            .failureRateThreshold(50.0f)
            .waitDurationInOpenState(Duration.ofSeconds(1))
            .permittedNumberOfCallsInHalfOpenState(1)
            .build()

        val circuitBreaker = CircuitBreaker.ofDefaults("backendA")


        // Circuit Breaker open 상태인 경우 fallback 메서드 호출
        val result = circuitBreaker.executeSupplier {
            restTemplate.getForObject("/api/some-resource", String::class.java)
        }
        assertThat(result).isEqualTo("Fallback Response")
    }

    // fallback 메서드
    fun restTemplateFallback(ex: Throwable): String {
        return "Fallback Response"
    }
}
