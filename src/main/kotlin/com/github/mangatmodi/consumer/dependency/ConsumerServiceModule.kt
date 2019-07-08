package com.github.mangatmodi.consumer.dependency

import com.google.inject.AbstractModule

class ConsumerServiceModule : AbstractModule() {
    override fun configure() {
        install(ConfigModule())
    }
}
