package com.github.mangatmodi.consumer

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.mangatmodi.consumer.EntryPoint.Service.API
import com.github.mangatmodi.consumer.EntryPoint.Service.valueOf
import com.github.mangatmodi.consumer.dependency.ServiceDependencies
import io.ktor.util.KtorExperimentalAPI

@KtorExperimentalAPI
class EntryPoint : CliktCommand(printHelpOnEmptyArgs = true) {
    enum class Service {
        API
    }

    private val service: String? by option("-s", "--service", help = "Service to start: API")

    override fun run() {
        if (valueOf(this.service!!) == API) ServiceDependencies.apiService.start()
    }
}
