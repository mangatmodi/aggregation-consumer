import io.kotlintest.Duration
import io.kotlintest.seconds

fun <T> eventually(waitDuration: Duration = 10.seconds, sleepMillis: Long = 500, f: () -> T) =
    io.kotlintest.eventually(waitDuration, Throwable::class.java) {
        try {
            f()
        } catch (e: Throwable) {
            Thread.sleep(sleepMillis)
            throw e
        }
    }
