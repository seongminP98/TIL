package com.circuitbreaker.service

import org.springframework.stereotype.Service
import java.lang.RuntimeException
import kotlin.random.Random

@Service
class HelloService {

    fun fail(): String {
        randomException()
        return "hello world!"
    }

    fun timeout(): String {
        Thread.sleep(10000)
        return "hello world!"
    }

    private fun randomException() { // 0~9 난수 생성 > 7 이하면 실패 > 50% 이상의 확률로 실패 (에러 비율 50%로 설정했음)

        val randomInt = Random.nextInt(10)

        if(randomInt <= 7) {
            throw RuntimeException("failed")
        }
    }
}
