package com.github.mangatmodi.consumer.config

import com.github.mangatmodi.consumer.dependency.ConfigModule
import com.google.inject.Guice
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.ShouldSpec

class ConfigModuleTest : ShouldSpec() {
    init {
        "ConfigModuleTest" {
            "When given the configuration in `application.conf` file" {
                should("read config") {
                    val injector = Guice.createInjector(ConfigModule())
                    val config = injector.getInstance(ApplicationConfig.Aggregation::class.java)
                    config.size shouldBe 5
                }
            }
        }
    }
}
