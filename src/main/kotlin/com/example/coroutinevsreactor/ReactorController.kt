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

    @GetMapping("/wait1sJava")
    fun wait1sJava(): Mono<Int> =
        make10ReactiveCalls(1) { make10ReactiveCalls(it, this::doSomething) }


    @GetMapping("/wait1sKotlin")
    fun wait1sKotlin(): Mono<Int> =
        do10CallsWrappedInCoroutine(1) { do10CallsWrappedInCoroutine(it, this::doSomething) }

    fun do10CallsWrappedInCoroutine(x: Int, foo: (Int) -> Mono<Int>): Mono<Int> = mono {
        val a = foo(x).awaitSingle()
        val b = foo(a).awaitSingle()
        val c = foo(b).awaitSingle()
        val d = foo(c).awaitSingle()
        val e = foo(d).awaitSingle()
        val f = foo(e).awaitSingle()
        val g = foo(f).awaitSingle()
        val h = foo(g).awaitSingle()
        val i = foo(h).awaitSingle()
        val j = foo(i).awaitSingle()
        foo(j).awaitSingle()
    }

    fun make10ReactiveCalls(x: Int, foo: (Int) -> Mono<Int>): Mono<Int> =
        foo(x)
            .flatMap(foo)
            .flatMap(foo)
            .flatMap(foo)
            .flatMap(foo)
            .flatMap(foo)
            .flatMap(foo)
            .flatMap(foo)
            .flatMap(foo)
            .flatMap(foo)
            .flatMap(foo)

    fun doSomething(value: Int): Mono<Int> =
        Mono.just(value).delayElement(Duration.ofMillis(10L))
}
