package com.circuitbreaker

import com.circuitbreaker.service.HelloService
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.AfterEach

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.client.ClientHttpResponse
import org.springframework.test.web.client.ExpectedCount
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.*
import org.springframework.web.client.RestTemplate
import java.io.ByteArrayInputStream

@SpringBootTest
class ServiceCircuitBreakerTest @Autowired constructor(
    private val helloService: HelloService,
    private val circuitBreakerRegistry: CircuitBreakerRegistry,
    private val restTemplate: RestTemplate,
    @Value("\${api.request-url}") private val url: String,
) {
    private lateinit var circuitBreaker: CircuitBreaker
    private lateinit var mockServer: MockRestServiceServer
    private val res = """
        {
            "data" : "success"
        }
    """.trimIndent()

    @BeforeEach
    fun setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate)

        circuitBreaker = circuitBreakerRegistry.circuitBreaker("backendA")
    }

    @AfterEach
    fun reset() {
        circuitBreaker.reset()
    }

    @Test
    fun `서버가 정상 응답하면 CircuitBreaker 작동 안해야한다`() {

        val responseEntity = ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, "application/json")
            .body(res)
        val clientHttpResponse = object : ClientHttpResponse {
            override fun getStatusCode() = responseEntity.statusCode
            override fun getRawStatusCode() = responseEntity.statusCodeValue
            override fun getStatusText() = responseEntity.statusCode.reasonPhrase
            override fun getHeaders() = responseEntity.headers
            override fun getBody() = ByteArrayInputStream(res.toByteArray())

            override fun close() {}
        }

        mockServer.expect(
            ExpectedCount.times(20),
            requestTo("$url/getData")
        )
            .andExpect(method(HttpMethod.GET))
            .andRespond { _ -> clientHttpResponse }

        for (i in 1..20) {
            try {
                helloService.getData()
            } catch (e: Exception) {
                println("e = $e")
            }
        }

        mockServer.verify()
        assertThat(circuitBreaker.state).isEqualTo(CircuitBreaker.State.CLOSED)
    }


    @Test
    fun `서버가 일부 실패할 때 CircuitBreaker가 작동하여 일부 요청이 실패해야 한다`() {

        var responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Fail")
        val failClientHttpResponse = object : ClientHttpResponse {
            override fun getStatusCode() = responseEntity.statusCode
            override fun getRawStatusCode() = responseEntity.statusCodeValue
            override fun getStatusText() = responseEntity.statusCode.reasonPhrase
            override fun getHeaders() = responseEntity.headers
            override fun getBody() = ByteArrayInputStream("Fail".toByteArray())

            override fun close() {}
        }

        mockServer.expect(
            ExpectedCount.times(10),
            requestTo("$url/getData")
        )
            .andExpect(method(HttpMethod.GET))
            .andRespond { _ -> failClientHttpResponse }

        for (i in 0..15) {
            try {
                helloService.getData()
            } catch (e: Exception) {
                println("Exception: " + e.message)
            }
        }

        mockServer.verify()
        assertThat(circuitBreaker.state).isEqualTo(CircuitBreaker.State.OPEN)

        mockServer.reset()

        responseEntity = ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, "application/json")
            .body(res)

        val successClientHttpResponse = object : ClientHttpResponse {
            override fun getStatusCode() = responseEntity.statusCode
            override fun getRawStatusCode() = responseEntity.statusCodeValue
            override fun getStatusText() = responseEntity.statusCode.reasonPhrase
            override fun getHeaders() = responseEntity.headers
            override fun getBody() = ByteArrayInputStream(res.toByteArray())

            override fun close() {}
        }

        mockServer.expect(
            ExpectedCount.times(11),
            requestTo("$url/getData")
        )
            .andExpect(method(HttpMethod.GET))
            .andRespond { _ -> successClientHttpResponse }

        Thread.sleep(11000)
        helloService.getData()
        assertThat(circuitBreaker.state).isEqualTo(CircuitBreaker.State.HALF_OPEN)

        for (i in 1..10) {
            helloService.getData()
        }
        assertThat(circuitBreaker.state).isEqualTo(CircuitBreaker.State.CLOSED)
        mockServer.verify()
    }
}
