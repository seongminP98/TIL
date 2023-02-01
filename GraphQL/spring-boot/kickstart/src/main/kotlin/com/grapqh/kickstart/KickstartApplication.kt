package com.grapqh.kickstart

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KickstartApplication

fun main(args: Array<String>) {
	runApplication<KickstartApplication>(*args)
}
