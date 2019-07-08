package com.github.mangatmodi.consumer.common

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.BufferedWriter

inline fun <reified T> T.logger(): Logger = LoggerFactory.getLogger(T::class.java)

fun BufferedWriter.writeln(line: String) {
    write(line)
    newLine()
}
