package com.github.mangatmodi.consumer.service

import com.github.mangatmodi.consumer.config.ApplicationConfig

data class Fixture(val testId: String? = "") {

    val aggregationConfig = ApplicationConfig.Aggregation(
        size = 5,
        path = "/tmp/aggregation-consumer/$testId"
    )
}
