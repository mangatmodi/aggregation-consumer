package com.github.mangatmodi.consumer.dependency

import com.github.mangatmodi.consumer.service.ConsumerService
import com.google.inject.Guice
import io.ktor.util.KtorExperimentalAPI

@KtorExperimentalAPI
object ServiceDependencies {
    private val injector by lazy { Guice.createInjector(ConsumerServiceModule()) }
    val apiService by lazy { injector.getInstance(ConsumerService::class.java) }
}
