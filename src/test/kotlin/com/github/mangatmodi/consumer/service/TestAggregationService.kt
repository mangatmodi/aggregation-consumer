package com.github.mangatmodi.consumer.service

import com.github.mangatmodi.consumer.util.ResourceUtil
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.ShouldSpec
import io.ktor.util.KtorExperimentalAPI
import kotlinx.io.core.ExperimentalIoApi
import java.io.File
import java.util.*

@ExperimentalIoApi
@KtorExperimentalAPI
class TestAggregationService : ShouldSpec() {
    override val oneInstancePerTest = true

    init {
        "AggregationService" {
            "when starting" {
                should("create path directory") {
                    with(Fixture(testId())) {
                        AggregationService(aggregationConfig)
                        File(aggregationConfig.path).exists() shouldBe true
                    }
                }
            }
            "when processing" {
                should("create 2 files when data exceeds size") {
                    with(Fixture(testId())) {
                        val service = AggregationService(aggregationConfig)
                        ResourceUtil.generateLoad { service.process(it) }
                        ResourceUtil.verifyNumberOfFiles {
                            File(aggregationConfig.path).listFiles().size
                        }
                    }
                }
                should("aggregate correctly") {
                    with(Fixture(testId())) {
                        val service = AggregationService(aggregationConfig)
                        ResourceUtil.generateLoad { service.process(it) }
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

    fun testId() = "test/${UUID.randomUUID()}/"
}
