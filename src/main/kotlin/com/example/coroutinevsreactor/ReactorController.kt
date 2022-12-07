package com.example.coroutinevsreactor

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.time.Duration
import java.util.logging.Logger
import kotlinx.coroutines.reactor.mono

@RestController
@RequestMapping("/reactor")
class ReactorController {
    val logger: Logger = Logger.getLogger(ReactorController::class.simpleName)

    @GetMapping("/wait2sJava")
    fun wait2sJava(): Mono<Int> =
        doSomething(1)
            .flatMap(this::doSomething)
            .flatMap(this::doSomething)
            .flatMap(this::doSomething)
            .flatMap(this::doSomething)
            .flatMap(this::doSomething)
            .flatMap(this::doSomething)
            .flatMap(this::doSomething)
            .flatMap(this::doSomething)
            .flatMap(this::doSomething)
            .flatMap(this::doSomething)

    @GetMapping("/wait2sKotlin")
    fun wait2sKotlin(): Mono<Int> = mono {
        val a = doSomething(1).awaitSingle()
        val b = doSomething(a).awaitSingle()
        val c = doSomething(b).awaitSingle()
        val d = doSomething(c).awaitSingle()
        val e = doSomething(d).awaitSingle()
        val f = doSomething(e).awaitSingle()
        val g = doSomething(f).awaitSingle()
        val h = doSomething(g).awaitSingle()
        val i = doSomething(h).awaitSingle()
        val j = doSomething(i).awaitSingle()
        doSomething(j).awaitSingle()
    }

    fun doSomething(value: Int): Mono<Int> =
        Mono.just(value).delayElement(Duration.ofMillis(200L))

}
