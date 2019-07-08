package com.github.mangatmodi.consumer.config

object ApplicationConfig {
    data class Aggregation(
        var size: Int? = 1000,
        var path: String? = "/tmp/aggregation-consumer"
    )

    data class Service(
        var aggregation: Aggregation? = null
    )

    data class Deployment(var port: Int? = null)
}
