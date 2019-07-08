package com.github.mangatmodi.consumer.common

import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * Custom io scheduler for rx-chain, with bac-pressure.
 * The defaults one risks OOM. The Params need to optimised
 * for the app
 * */
val ioScheduler: Scheduler = Schedulers.from(
    ThreadPoolExecutor(
        50,
        500,
        1,
        TimeUnit.MINUTES,
        ArrayBlockingQueue<Runnable>(1000),
        ThreadPoolExecutor.CallerRunsPolicy()
    )
)
