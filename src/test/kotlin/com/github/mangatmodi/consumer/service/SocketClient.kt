package com.github.mangatmodi.consumer.service

import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openWriteChannel
import io.ktor.util.KtorExperimentalAPI
import io.ktor.util.cio.write
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.io.cancel
import kotlinx.coroutines.io.jvm.javaio.toByteReadChannel
import kotlinx.coroutines.io.readUTF8Line
import kotlinx.coroutines.runBlocking
import kotlinx.io.core.ExperimentalIoApi
import java.net.InetSocketAddress

@KtorExperimentalAPI
@ExperimentalIoApi
object SocketClient {
    fun sendAll() {
        runBlocking {
            val socket = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp()
                .connect(InetSocketAddress("0.0.0.0", Fixture.apiConfig.port!!))
            val output = socket.openWriteChannel(autoFlush = true)
            val input = this::class.java.classLoader.getResource("messages.txt").openStream().toByteReadChannel()
            while (true) {
                val line = input.readUTF8Line()
                if (line == null) {
                    input.cancel()
                    socket.close()
                    break
                }
                output.write(line)
            }
        }
    }

    fun test(fn: (String?) -> Unit) {
        runBlocking {
            val input = this::class.java.classLoader.getResource("messages.txt").openStream().toByteReadChannel()
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
}
