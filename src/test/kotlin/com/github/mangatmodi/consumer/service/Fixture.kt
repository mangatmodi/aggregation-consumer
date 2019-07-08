package com.github.mangatmodi.consumer.service

import com.github.mangatmodi.consumer.config.ApplicationConfig
import io.kotlintest.Duration
import io.kotlintest.eventually
import io.kotlintest.seconds
import java.util.*

object Fixture {

    //TODO: File rotation not working
    val aggregationConfig = ApplicationConfig.Aggregation(
        size = 5,
        path = "/tmp/aggregation-consumer/test/${UUID.randomUUID()}/"
    )

    val apiConfig = ApplicationConfig.Deployment(
        port = 9000
    )

    fun <T> eventually(waitDuration: Duration = 10.seconds, sleepMillis: Long = 500, f: () -> T) =
        eventually(waitDuration, Throwable::class.java) {
            try {
                f()
            } catch (e: Throwable) {
                Thread.sleep(sleepMillis)
                throw e
            }
        }
}
