package com.circuitbreaker.controller

import com.circuitbreaker.service.HelloService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController(
    private val helloService: HelloService,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @GetMapping("/fail")
    fun fail(): String {
        log.info("server fail")
        return helloService.fail()
    }

    @GetMapping("/timeout")
    fun timeout(): String {
        log.info("server timeout")
        return helloService.timeout()
    }

    @GetMapping("/test")
    fun test(): String {
        log.info("server test")
        return helloService.failOrTimeout()
    }

    @GetMapping("/success")
    fun success(): String {
        log.info("server success")
        return helloService.success()
    }

    @GetMapping("/slowErrorOrSuccess")
    fun slowErrorOrSuccess(): String {
        helloService.slowErrorOrSuccess()
        return "fail"
    }

    @GetMapping("/alwaysFail")
    fun alwaysFail() {
        throw RuntimeException("failed")
    }

    @GetMapping("/getData")
    fun getData(): String {
        return "success"
    }
}
