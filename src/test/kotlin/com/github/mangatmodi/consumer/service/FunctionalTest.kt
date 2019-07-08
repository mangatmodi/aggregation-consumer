package com.github.mangatmodi.consumer.service

import com.github.mangatmodi.consumer.util.ResourceUtil
import com.github.mangatmodi.consumer.util.SocketUtil
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.ShouldSpec
import io.ktor.util.KtorExperimentalAPI
import kotlinx.io.core.ExperimentalIoApi
import java.io.File

@ExperimentalIoApi
@KtorExperimentalAPI
class FunctionalTest : ShouldSpec() {
    override val oneInstancePerTest = true

    init {
        "aggregation-consumer" {
            "when started" {
                should("listen to requests at port 9000") {
                    with(Fixture()) {
                        SocketUtil.isPortInUse() shouldBe true
                    }
                }
            }
            "when data is sent" {
                should("Save in the given path") {
                    with(Fixture()) {
                        ResourceUtil.generateLoad { SocketUtil.sendData(it) }
                        ResourceUtil.verifyNumberOfFiles {
                            File(aggregationConfig.path).listFiles().size
                        }

                        ResourceUtil.verifyFileData {
                            File(aggregationConfig.path).listFiles().map {
                                it.readLines()
                            }.flatten()
                        }
                    }
                }
            }
        }
    }
}
