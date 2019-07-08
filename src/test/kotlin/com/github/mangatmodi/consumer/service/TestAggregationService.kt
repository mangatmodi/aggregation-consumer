package com.github.mangatmodi.consumer.service

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.ShouldSpec
import io.ktor.util.KtorExperimentalAPI
import kotlinx.io.core.ExperimentalIoApi
import java.io.File

@ExperimentalIoApi
@KtorExperimentalAPI
class TestAggregationService : ShouldSpec() {
    override val oneInstancePerTest = true

    init {
        "AggregationService" {
            "when starting"{
                should("create path directory") {
                    AggregationService(Fixture.aggregationConfig)
                    File(Fixture.aggregationConfig.path).exists() shouldBe true
                }
            }
            "when processing"{
                should("create 2 files when data exceeds size") {
                    val service = AggregationService(Fixture.aggregationConfig)
                    SocketClient.test { service.process(it) }
                }
            }
        }
    }
}
