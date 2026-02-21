package com.wafflestudio.spring2025

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class WackathonApplication

fun main(args: Array<String>) {
    runApplication<WackathonApplication>(*args)
}
