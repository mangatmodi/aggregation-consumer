package com.github.mangatmodi.consumer.service

import com.github.mangatmodi.consumer.common.*
import com.github.mangatmodi.consumer.config.ApplicationConfig
import com.google.inject.Inject
import io.reactivex.Single
import java.io.BufferedWriter
import java.io.File

fun BufferedWriter.writeln(line: String) {
    write(line)
    newLine()
}

class AggregationService @Inject constructor(
    private val config: ApplicationConfig.Aggregation
) {
    private val logger = logger()
    private var data = FileData()

    init {
        try {
            File(config.path).mkdirs()
        } catch (e: Throwable) {
            logger.error("Exiting server", e)
            System.exit(1)
        }
    }

    //TODO: check in a synchronized and then make a copy
    fun process(line: String?) {
        Single.fromCallable {
            val message = line?.split(",") ?: listOf()
            if (message.size < 5) {
                throw IllegalStateException("Message should be 5 column csv, message:$line")
            } else {
                message
            }
        }
            .map { append(it) }
            .flatMap {
                val copy = copyIfReached()
                if (copy != null) {
                    Single.fromCallable {
                        data.counter.getAndSet(0)
                        save(data)
                        data = FileData()
                    }.subscribeOn(ioScheduler)
                } else {
                    Single.just(Unit)
                }
            }
            .subscribe(
                { logger.trace("Successfully processed:$line", it) },
                { logger.error("Error processing:$line", it) }
            )
    }

    private fun append(message: List<String>) {
        data.sum = data.sum + message[Field.SUM].toBigInteger()
        //merge is thread safe
        data.perUser.merge(message[Field.ID], toUserMetric(message)) { existing, default ->
            existing + default
        }
    }

    private fun save(data: FileData) {
        val fileName = "${config.path}${System.nanoTime()}.txt"
        File(fileName).bufferedWriter().use { out ->
            out.writeln(data.sum.toString())
            out.writeln(data.perUser.size.toString())
            data.perUser.forEach { (k, v) ->
                val line = "$k,${v.sum / v.count},${v.recent}"
                out.writeln(line)
            }
        }
    }

    fun unsafeSave() = save(this.data)

    private fun toUserMetric(message: List<String>) =
        UserMetric(
            sum = message[Field.AVG].toDouble(),
            recent = message[Field.RECENT].toLong(),
            count = 1
        )

    @Synchronized
    private fun copyIfReached(): FileData? {
        return if (data.counter.incrementAndGet() == config.size) {
            //Switch refrences
            val temp = data
            data = FileData()
            temp
        } else {
            null
        }
    }
}

