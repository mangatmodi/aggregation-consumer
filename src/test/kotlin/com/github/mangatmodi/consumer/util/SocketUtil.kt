package com.github.mangatmodi.consumer.util

import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openWriteChannel
import io.ktor.util.KtorExperimentalAPI
import io.ktor.util.cio.write
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.io.ByteWriteChannel
import kotlinx.coroutines.runBlocking
import java.net.InetSocketAddress
import java.net.SocketException
import java.nio.charset.Charset

@KtorExperimentalAPI
object SocketUtil {
    lateinit var output: ByteWriteChannel

    init {
        runBlocking {
            output = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp()
                .connect(InetSocketAddress("0.0.0.0", 9000))
                .openWriteChannel(autoFlush = true)
        }
    }

    fun sendData(line: String?) {
        runBlocking {
            if (line != null) {
                output.write("$line\n", Charset.forName("UTF-8"))
            }
        }
    }

    fun isPortInUse() =
        try {
            java.net.Socket("0.0.0.0", 9000).isBound
        } catch (e: SocketException) {
            e.printStackTrace()
            false
        }
}
