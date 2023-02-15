package com.circuitbreaker.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import kotlin.RuntimeException
import kotlin.random.Random

@Service
class HelloService {

    private val log = LoggerFactory.getLogger(javaClass)

    fun fail(): String {
        randomException()
        return "hello world!"
    }

    fun timeout(): String {
        val randomInt = Random.nextInt(10)
        log.info("server timeout method randomInt = {}", randomInt)
        if (randomInt <= 7) {
            log.info("randomInt는 5이상 10초 걸림")
            Thread.sleep(10000)
            log.info("10초 로직 끝")
        }
        return "hello world!"
    }

    fun failOrTimeout(): String {
        val randomInt = Random.nextInt(10)
        log.info("server > randomInt = {}", randomInt)
        if (randomInt < 3) {
            log.info("failed")
            throw RuntimeException("failed")
        } else if(randomInt < 6) {
            log.info("timeout")
            Thread.sleep(10000)
        } else {
            log.info("pass")
        }
        return "hello world!"
    }

    fun success(): String {
        return "hello world!"
    }

    fun slowErrorOrSuccess() {
        val randomInt = Random.nextInt(10)
        log.info("randomInt = {}", randomInt)
        if(randomInt < 5) {
            Thread.sleep(10000)
            throw RuntimeException("failed")
        } else {
            return
        }
    }

    private fun randomException() { // 0~9 난수 생성 > 7 이하면 실패 > 50% 이상의 확률로 실패 (에러 비율 50%로 설정했음)

        val randomInt = Random.nextInt(10)

        if (randomInt <= 7) {
            throw RuntimeException("failed")
        }
    }
}
