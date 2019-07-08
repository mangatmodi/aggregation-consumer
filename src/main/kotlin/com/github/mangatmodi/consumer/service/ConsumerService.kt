package com.github.mangatmodi.consumer.service

import com.github.mangatmodi.consumer.common.logger
import com.github.mangatmodi.consumer.config.ApplicationConfig
import com.google.inject.Inject
import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.io.cancel
import kotlinx.coroutines.io.readUTF8Line
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.net.InetSocketAddress

@KtorExperimentalAPI
class ConsumerService @Inject constructor(
    private val deployment: ApplicationConfig.Deployment,
    private val aggregationService: AggregationService
) {
    private val logger = logger()
    fun start() {
        runBlocking {
            val server = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp()
                .bind(InetSocketAddress("0.0.0.0", deployment.port!!))
            logger.info("Started tcp server at ${server.localAddress}")

            Runtime.getRuntime().addShutdownHook(Thread {
                logger.warn("Shutting down server")
                aggregationService.saveBlocking() // flushes the remaining data
                server.close()
                logger.warn("Finished cleaning up")
            })

            while (true) {
                val socket = server.accept()

                launch {
                    logger.info("Socket accepted: ${socket.remoteAddress}")
                    val input = socket.openReadChannel()
                    try {
                        while (true) {
                            val line = input.readUTF8Line()
                            if (line == null) {
                                input.cancel()
                                socket.close()
                                break
                            }
                            aggregationService.process(line)
                        }
                    } catch (e: Throwable) {
                        e.printStackTrace()
                        aggregationService.saveBlocking() // flushes the remaining data, if any
                        socket.close()
                    }
                }
            }
        }
    }
}
