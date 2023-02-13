package com.circuitbreaker.controller

import com.circuitbreaker.service.HelloService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class HelloController(
    private val helloService: HelloService
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @GetMapping("/fail")
    fun fail(): Mono<String> {
        log.info("hello client fail")
        return helloService.fail()
    }

    @GetMapping("/timeout")
    fun timeout(): Mono<String> {
        log.info("hello client timeout")
        return helloService.timeout()
    }
}
