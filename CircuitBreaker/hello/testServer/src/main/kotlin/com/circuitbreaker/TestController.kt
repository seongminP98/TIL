package com.circuitbreaker

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController {

    @GetMapping("/{state}")
    fun test(@PathVariable("state") state: String): String {
        return if (state == "success") {
            "success"
        } else if (state == "fail") {
            throw RuntimeException("fail")
        } else if (state == "slow") {
            Thread.sleep(10000)
            "success"
        } else {
            "success"
        }
    }
}
