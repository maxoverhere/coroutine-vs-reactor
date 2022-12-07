package com.example.coroutinevsreactor

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CoroutineVsReactorApplication

fun main(args: Array<String>) {
    runApplication<CoroutineVsReactorApplication>(*args)
}
