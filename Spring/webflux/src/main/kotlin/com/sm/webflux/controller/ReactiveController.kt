package com.sm.webflux.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class ReactiveController {

    @GetMapping("/api/reactive/hello")
    fun servletApi(): Mono<String> {
        return Mono.just("hello")
    }
}
