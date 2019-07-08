package com.github.mangatmodi.consumer

import io.ktor.util.KtorExperimentalAPI

@KtorExperimentalAPI
class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) = EntryPoint().main(args)
    }
}
