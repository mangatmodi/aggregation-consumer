package com.github.mangatmodi.consumer.common

import kotlinx.atomicfu.AtomicInt
import kotlinx.atomicfu.atomic
import java.math.BigInteger
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class FileData {
    val version = UUID.randomUUID() // remove
    val counter: AtomicInt = atomic(0)
    var sum: BigInteger = BigInteger.ZERO
    val perUser: ConcurrentHashMap<String, UserMetric> = ConcurrentHashMap()
}

data class UserMetric(val sum: Double, val recent: Long, val count: Int) {
    operator fun plus(new: UserMetric) = UserMetric(
        this.sum + new.sum,
        new.recent,
        this.count + new.count
    )
}

object Field {
    const val ID = 0
    const val SUM = 4
    const val AVG = 2
    const val RECENT = 3
}
