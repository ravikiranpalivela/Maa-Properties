package com.tekskills.sampleapp.utils

import android.annotation.SuppressLint
import android.content.Context
import com.tekskills.sampleapp.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object TimeUtil {

    fun getTimeAgo(context: Context, timestamp: Long): String {
        val currentTimeMillis = System.currentTimeMillis()
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
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                sdf.timeZone = TimeZone.getDefault()
                val formattedDate = sdf.format(Date(timestamp))
                resources.getString(R.string.days_ago, formattedDate)
            }
        }
    }
}
