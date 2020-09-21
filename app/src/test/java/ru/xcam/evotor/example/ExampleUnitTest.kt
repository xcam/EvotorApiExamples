package ru.xcam.evotor.example

import org.junit.Test
import java.io.IOException
import kotlin.concurrent.thread

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun testMe90(){
        println(false && false || true && true)
        println(false && false || true && false)
    }
    @Test
    fun addition_isCorrect() {

        println("io.reactivex")

        var s3 = io.reactivex.Single.just(1)
            .observeOn(io.reactivex.schedulers.Schedulers.from { command ->
                thread {
                    try {
                        command.run()
                    } catch (e: Throwable) {
                        println("Catch $e")
                    }
                }
            })
            .flatMap {
                io.reactivex.Single.create<String> {
                    Thread.sleep(200)
                    it.onError(throw NetworkConnectionException(""))
                }
            }
            .subscribe({ println("onNext") }, { println("onError $it") })

        Thread.sleep(100)
        s3.dispose()
        Thread.sleep(50000000)

        //-----------------
//        println("rxjava3 NetworkConnectionException")
//        io.reactivex.rxjava3.core.Single.just(1)
//            .flatMap {
//                io.reactivex.rxjava3.core.Single.create<String> { throw NetworkConnectionException("") }
//            }
//            .subscribe({ println("onNext") }, { println("onError $it") })
//
//
//        println("rx NetworkConnectionException")
//        rx.Single.just(1)
//            .flatMap {
//                rx.Single.create<String> { throw NetworkConnectionException("") }
//            }
//            .subscribe({ println("onNext") }, { println("onError $it") })
//
//
//        println("io.reactivex NetworkConnectionException")
//        io.reactivex.Single.just(1)
//            .flatMap {
//                io.reactivex.Single.create<String> { throw NetworkConnectionException("") }
//            }
//            .subscribe({ println("onNext") }, { println("onError $it") })
    }
}

class NetworkConnectionException constructor(m: String? = null) : Exception()
