package com.circuitbreaker

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.client.ClientHttpResponse
import org.springframework.test.web.client.ExpectedCount
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.ResponseErrorHandler
import org.springframework.web.client.RestTemplate
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.time.Duration

class Resilience4jCircuitBreakerTest {
    private lateinit var circuitBreaker: CircuitBreaker
    private lateinit var restTemplate: RestTemplate
    private lateinit var mockServer: MockRestServiceServer

    @BeforeEach
    fun setUp() {
        val restTemplateBuilder = RestTemplateBuilder()

        restTemplate = restTemplateBuilder
            .errorHandler(object : ResponseErrorHandler {
                override fun hasError(response: ClientHttpResponse): Boolean {
                    return !response.statusCode.is2xxSuccessful
                }

                @Throws(IOException::class)
                override fun handleError(response: ClientHttpResponse) {
                    throw HttpClientErrorException(response.statusCode)
                }
            })
            .build()

        mockServer = MockRestServiceServer.createServer(restTemplate)

        val circuitBreakerConfig = CircuitBreakerConfig.custom()
            .failureRateThreshold(50.0f)
            .waitDurationInOpenState(Duration.ofSeconds(1))
            .permittedNumberOfCallsInHalfOpenState(1)
            .build()
        circuitBreaker = CircuitBreaker.of("backendA", circuitBreakerConfig)

    }

    @Test
    fun `서버가 정상적으로 응답할 때는 CircuitBreaker가 작동하지 않아야 한다`() {
        // Given
        val responseEntity = ResponseEntity.ok("Hello World")
        val httpEntity = responseEntity.body?.let { HttpEntity(it) }
        val clientHttpResponse = object : ClientHttpResponse {
            override fun getStatusCode() = responseEntity.statusCode
            override fun getRawStatusCode() = responseEntity.statusCodeValue
            override fun getStatusText() = responseEntity.statusCode.reasonPhrase
            override fun getHeaders() = responseEntity.headers
            override fun getBody(): InputStream {
                return ByteArrayInputStream(httpEntity?.body?.toByteArray())
            }

            override fun close() {}
        }
        mockServer.expect(
            ExpectedCount.once(),
            MockRestRequestMatchers.requestTo("/api/some-resource")
        )
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andRespond { _ -> clientHttpResponse }


        // When
        val result = circuitBreaker.executeSupplier {
            val forObject = restTemplate.getForObject("/api/some-resource", String::class.java)
            println("forObject = $forObject")
            forObject
        }

        // Then
        mockServer.verify()
        assertEquals("Hello World", result)
        assertEquals(CircuitBreaker.State.CLOSED, circuitBreaker.state)
    }


    @Test
    fun `서버가 일부 실패할 때 CircuitBreaker가 작동하여 일부 요청이 실패해야 한다`() {
        // Given
        val responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Fail")
        val httpEntity = responseEntity.body?.let { HttpEntity(it) }
        val clientHttpResponse = object : ClientHttpResponse {
            override fun getStatusCode() = responseEntity.statusCode
            override fun getRawStatusCode() = responseEntity.statusCodeValue
            override fun getStatusText() = responseEntity.statusCode.reasonPhrase
            override fun getHeaders() = responseEntity.headers
            override fun getBody(): InputStream {
                return ByteArrayInputStream(httpEntity?.body?.toByteArray())
            }

            override fun close() {}
        }

        mockServer.expect(
            ExpectedCount.times(11),
            MockRestRequestMatchers.requestTo("/api/some-resource")
        )
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andRespond { _ -> clientHttpResponse }

        // When
        val results = mutableListOf<String?>()

        for (i in 1..11) {
            val result = try {
                circuitBreaker.executeSupplier {
                    restTemplate.getForObject("/api/some-resource", String::class.java)
                }
            } catch (ex: Throwable) {
                "Fail"
            }
            println("result = $result")
            println("circuitBreaker state = ${circuitBreaker.state}")

            results.add(result)
        }

        mockServer.verify()

        // Then
        assertEquals(11, results.count { it == "Fail" })
    }
}
