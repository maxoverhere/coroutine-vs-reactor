package com.example.coroutinevsreactor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.BufferedReader
import java.net.URL
import java.util.concurrent.CountDownLatch
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
class ReactorControllerTest{
    companion object {
        const val NUMBER_OF_PINGS = 1000
    }

    @Test
    fun localTest() {
        val countDownLatch = CountDownLatch(NUMBER_OF_PINGS)
        val time = measureTime {
            repeat(NUMBER_OF_PINGS) {
                GlobalScope.launch {
                    delay(Duration.seconds(1))
                    countDownLatch.countDown()
                }
            }
            countDownLatch.await()
        }
        println("completed in $time seconds")
    }

    @Test
    fun hitPureReactorTest() {
        val countDownLatch = CountDownLatch(NUMBER_OF_PINGS)
        val time = measureTime {
            repeat(NUMBER_OF_PINGS) {
                GlobalScope.launch(Dispatchers.IO) {
                    val result = getRequest("http://localhost:8080/reactor/wait2sJava")
                    countDownLatch.countDown()
                }
            }
            countDownLatch.await()
        }
        println("completed in $time seconds")
    }

    @Test
    fun hitKotlinReactorTest() {
        val countDownLatch = CountDownLatch(NUMBER_OF_PINGS)
        val time = measureTime {
            repeat(NUMBER_OF_PINGS) {
                GlobalScope.launch(Dispatchers.IO) {
                    val result = getRequest("http://localhost:8080/reactor/wait2sKotlin")
                    countDownLatch.countDown()
                }
            }
            countDownLatch.await()
        }
        println("completed in $time seconds")
    }

    private fun getRequest(sUrl: String): String {
        val url = URL(sUrl)
        val conn = url.openConnection()
        conn.connect()
        val inputStream = conn.inputStream
        return inputStream.bufferedReader().use(BufferedReader::readText)
    }

}