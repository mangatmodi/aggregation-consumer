package com.github.mangatmodi.consumer.dependency

import com.github.mangatmodi.consumer.config.ApplicationConfig
import com.google.inject.AbstractModule
import com.typesafe.config.ConfigBeanFactory
import com.typesafe.config.ConfigFactory

class ConfigModule : AbstractModule() {
    lateinit var service: ApplicationConfig.Service
    lateinit var deployment: ApplicationConfig.Deployment
    override fun configure() {
        val config = ConfigFactory.defaultApplication().resolve()

        service = ConfigBeanFactory.create(
            config.getConfig("ktor.service"),
            ApplicationConfig.Service::class.java
        )
        deployment = ConfigBeanFactory.create(
            config.getConfig("ktor.deployment"),
            ApplicationConfig.Deployment::class.java
        )

        bind(ApplicationConfig.Aggregation::class.java).toInstance(service.aggregation)
        bind(ApplicationConfig.Deployment::class.java).toInstance(deployment)
    }
}
