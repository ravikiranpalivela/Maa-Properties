package com.tekskills.sampleapp.utils

import android.annotation.SuppressLint
import android.content.Context
import com.tekskills.sampleapp.R
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object TimeUtil {

    fun getTimeAgo(context: Context, timestampDate: String): String {
        val currentTimeMillis = System.currentTimeMillis()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
        val dateTime = LocalDateTime.parse(timestampDate, formatter)

        // Convert LocalDateTime to a timestamp (milliseconds since epoch)
        val timestamp = dateTime.toEpochSecond(java.time.ZoneOffset.UTC) * 1000

        val timeDifferenceMillis = currentTimeMillis - timestamp

        val resources = context.resources

        val justNowThresholdMillis = 10000 // 10 seconds
        val minuteMillis = 60 * 1000
        val hourMillis = 60 * minuteMillis
        val dayMillis = 24 * hourMillis

        return when {
            timeDifferenceMillis < justNowThresholdMillis -> {
                resources.getString(R.string.just_now)
            }
            timeDifferenceMillis < minuteMillis -> {
                val secondsAgo = timeDifferenceMillis / 1000
                resources.getQuantityString(R.plurals.seconds_ago, secondsAgo.toInt(), secondsAgo)
            }
            timeDifferenceMillis < hourMillis -> {
                val minutesAgo = timeDifferenceMillis / minuteMillis
                resources.getQuantityString(R.plurals.minutes_ago, minutesAgo.toInt(), minutesAgo)
            }
            timeDifferenceMillis < dayMillis -> {
                val hoursAgo = timeDifferenceMillis / hourMillis
                resources.getQuantityString(R.plurals.hours_ago, hoursAgo.toInt(), hoursAgo)
            }
            else -> {
                val daysAgo = timeDifferenceMillis / dayMillis
//                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//                sdf.timeZone = TimeZone.getDefault()
//                val formattedDate = sdf.format(Date(timestamp))
                resources.getQuantityString(R.plurals.days_ago, daysAgo.toInt(),daysAgo)
            }
        }
    }
}
