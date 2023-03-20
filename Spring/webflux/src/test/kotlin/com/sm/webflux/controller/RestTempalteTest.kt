package com.sm.webflux.controller

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.client.RestTemplate

@SpringBootTest
class RestTempalteTest {

    @Test
    fun testReactiveEndpoint() {
        val restTemplate = RestTemplate()

        val start = System.currentTimeMillis()

        for (i in 1..100000) {
            restTemplate.getForEntity("http://localhost:8080/api/reactive/hello", String::class.java)
        }

        val end = System.currentTimeMillis()
        val elapsedTime = end - start

        println("Elapsed time: $elapsedTime ms")
    }

    @Test
    fun testServletEndPoint() {
        val restTemplate = RestTemplate()

        val start = System.currentTimeMillis()

        for (i in 1..100000) {
            restTemplate.getForEntity("http://localhost:8080/api/hello", String::class.java)
        }
//        val mockMvc = MockMvcBuilders.standaloneSetup(servletController).build()
//
//        val start = System.currentTimeMillis()
//
//        for (i in 1..1000) {
//            mockMvc.perform(MockMvcRequestBuilders.get("/api/hello"))
//        }

        val end = System.currentTimeMillis()
        val elapsedTime = end - start

        println("Elapsed time: $elapsedTime ms")
    }
}
