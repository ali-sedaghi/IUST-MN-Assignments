package ir.mag.interview.note.util

import ir.mag.interview.note.R
import java.util.*
import java.util.concurrent.TimeUnit

object TimeUtil {

    class TimeDifference(val type: TYPE, val amount: Long) {
        enum class TYPE {
            YEAR, MONTH, DAY, HOUR, MINUTE, SECOND
        }
    }

    fun getDistanceDescription(date: Date): TimeDifference {
        val now = Date()
        val difference = now.time - date.time
        return when {
            TimeUnit.SECONDS.convert(difference, TimeUnit.MILLISECONDS) < 60 -> {
                TimeDifference(
                    TimeDifference.TYPE.SECOND,
                    TimeUnit.SECONDS.convert(difference, TimeUnit.MILLISECONDS)
                )
            }
            TimeUnit.MINUTES.convert(difference, TimeUnit.MILLISECONDS) < 60 -> {
                TimeDifference(
                    TimeDifference.TYPE.MINUTE,
                    TimeUnit.MINUTES.convert(difference, TimeUnit.MILLISECONDS)
                )
            }
            TimeUnit.HOURS.convert(difference, TimeUnit.MILLISECONDS) < 24 -> {
                TimeDifference(
                    TimeDifference.TYPE.HOUR,
                    TimeUnit.HOURS.convert(difference, TimeUnit.MILLISECONDS)
                )
            }
            TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS) < 30 -> {
                TimeDifference(
                    TimeDifference.TYPE.DAY,
                    TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS)
                )
            }
            TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS) < 365 -> {
                TimeDifference(
                    TimeDifference.TYPE.MONTH,
                    TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS) / 30
                )
            }
            else -> {
                TimeDifference(
                    TimeDifference.TYPE.YEAR,
                    TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS) / 365
                )
            }
        }
    }

    fun typeToPersianString(distanceDescription: TimeDifference): String {
        return when (distanceDescription.type) {
            TimeDifference.TYPE.SECOND -> "ثانیه"
            TimeDifference.TYPE.MINUTE -> "دقیقه"
            TimeDifference.TYPE.HOUR -> "ساعت"
            TimeDifference.TYPE.DAY -> "روز"
            TimeDifference.TYPE.MONTH -> "ماه"
            TimeDifference.TYPE.YEAR -> "سال"
        }
    }


}