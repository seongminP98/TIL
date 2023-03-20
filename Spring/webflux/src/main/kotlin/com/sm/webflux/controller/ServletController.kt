package com.sm.webflux.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ServletController {

    @GetMapping("/api/hello")
    fun servletApi(): String {
        return "hello"
    }
}
