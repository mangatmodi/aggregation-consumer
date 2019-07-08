package com.github.mangatmodi.consumer.util

import eventually
import io.kotlintest.matchers.shouldBe
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.io.cancel
import kotlinx.coroutines.io.jvm.javaio.toByteReadChannel
import kotlinx.coroutines.io.readUTF8Line
import kotlinx.coroutines.runBlocking
import kotlinx.io.core.ExperimentalIoApi

@KtorExperimentalAPI
@ExperimentalIoApi
object ResourceUtil {
    // Read resource and feeds it to the function [fn]
    fun generateLoad(name: String? = "messages.txt", fn: (String?) -> Unit) {
        runBlocking {
            val input = this::class.java.classLoader.getResource(name).openStream().toByteReadChannel()
            while (true) {
                val line = input.readUTF8Line()
                if (line == null) {
                    input.cancel()
                    break
                }
                fn(line)
            }
        }
    }

    fun verifyNumberOfFiles(fn: () -> Int) {
        eventually { fn() shouldBe 2 }
    }

    fun verifyFileData(fn: () -> List<String>) {
        val data = fn()
        generateLoad("testresult.txt") {
            if (it != null)
                data.contains(it) shouldBe true
        }
    }
}
