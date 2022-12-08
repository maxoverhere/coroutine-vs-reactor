package com.example.coroutinevsreactor

import kotlinx.coroutines.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.BufferedReader
import java.net.URL
import java.util.concurrent.CountDownLatch
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
class ReactorControllerTest {
    companion object {
        const val NUMBER_OF_PINGS = 10000
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
                    val result = getRequest("http://localhost:8080/reactor/wait1sJava")
                    countDownLatch.countDown()
                }
            }
            countDownLatch.await()
        }
        println("completed in $time")
    } // 10000 - completed in 3m 23.701389232s
    // 10000 - completed in 3m 24.807527864s

    @Test
    fun hitKotlinReactorTest() {
        val countDownLatch = CountDownLatch(NUMBER_OF_PINGS)
        val time = measureTime {
            repeat(NUMBER_OF_PINGS) {
                GlobalScope.launch(Dispatchers.IO) {
                    val result = getRequest("http://localhost:8080/reactor/wait1sKotlin")
                    countDownLatch.countDown()
                }
            }
            countDownLatch.await()
        }
        println("completed in $time")
    } // 10000 completed in 3m 23.727064984s
    // 10000 completed in 3m 25.437766526s

    @Test
    fun hitKotlinCoroutinePureTest() {
        val countDownLatch = CountDownLatch(NUMBER_OF_PINGS)
        val time = measureTime {
            repeat(NUMBER_OF_PINGS) {
                GlobalScope.launch(Dispatchers.IO) {
                    val result = getRequest("http://localhost:8081/ktor/coroutine-pure")
                    countDownLatch.countDown()
                }
            }
            countDownLatch.await()
        }
        println("completed in $time")
    } // 10000 completed in 3m 33.796529371s

    @Test
    fun howLongSingleApiCallReactorCodeTakes() {
        val time = measureTime {
            val result = getRequest("http://localhost:8080/reactor/wait1sJava")
            println(result)
        }
        println("completed in $time")
    }

    @Test
    fun howLongSingleApiCallMixedCoroutineCodeTakes() {
        val time = measureTime {
            val result = getRequest("http://localhost:8080/reactor/wait1sKotlin")
            println(result)
        }
        println("completed in $time")
    }

    @Test
    fun howLongSingleApiCallKotlinCoroutinePureTakes() {
        val time = measureTime {
            val result = getRequest("http://localhost:8081/ktor/coroutine-pure")
            println(result)
        }
        println("completed in $time")
    }

    @Test
    fun howLongReactorCodeTakes() {
        val time = measureTime {
            val result = ReactorController().wait1sJava().block()
            println(result)
        }
        println("completed in $time")
    }

    @Test
    fun howLongMixedCoroutineCodeTakes() {
        val time = measureTime {
            val result = ReactorController().wait1sKotlin().block()
            println(result)
        }
        println("completed in $time")
    }

    private fun getRequest(sUrl: String): String {
        val url = URL(sUrl)
        val conn = url.openConnection()
        conn.connect()
        val inputStream = conn.inputStream
        return inputStream.bufferedReader().use(BufferedReader::readText)
    }
}
