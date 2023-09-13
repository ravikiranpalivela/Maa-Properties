package com.tekskills.sampleapp.utils.video

import android.util.Log
import android.util.Patterns
import android.webkit.URLUtil
import java.net.MalformedURLException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Extract medium quality thumbnail
 */
fun String.getThumbnail(): String {
    return "http://img.youtube.com/vi/${this.getVideoId()}/mqdefault.jpg"
}

fun String.isYoutubeUrl(): Boolean {
    val success: Boolean

    val reg = "(youtu.*be.*)/(watch\\?v=|embed/|v|shorts|)(.*?((?=[&#?])|\$))"
    val pattern = Pattern.compile(reg)
    val matcher = pattern.matcher(this)
    success = this.isNotEmpty() && matcher.find()
    return success
}

fun String.isValidUrl(): Boolean {
    try {
        return URLUtil.isValidUrl(this) && Patterns.WEB_URL.matcher(this).matches()
    } catch (ignored: MalformedURLException) {
    }
    return false
}

fun String.isDataContains(): Boolean {
    try {
        return URLUtil.isValidUrl(this) && Patterns.WEB_URL.matcher(this).matches()
    } catch (ignored: MalformedURLException) {
    }
    return false
}

fun String.isValidURL(): String {
    // Regex to check valid URL
    val regex = ("((http|https)://)(www.)?"
            + "[a-zA-Z0-9@:%._\\+~#?&//=]"
            + "{2,256}\\.[a-z]"
            + "{2,6}\\b([-a-zA-Z0-9@:%"
            + "._\\+~#?&//=]*)")

    // Compile the ReGex
    val p: Pattern = Pattern.compile(regex)

    if (this.isNullOrEmpty()) {
        return "null"
    }

    val m: Matcher = p.matcher(this)

    return if (m.matches()) this else "null"
}

fun changeDateFormat(olddate: String): String {
    var changedDate: String = ""
    try {
        val dateFormatprev = SimpleDateFormat("yyyy-MM-dd")
        val d: Date = dateFormatprev.parse(olddate)
        val dateFormat = SimpleDateFormat("EEE, MMM dd, yyyy")
        changedDate = dateFormat.format(d)
        println(changedDate)
    } catch (e: ParseException) {
        Log.d("TAG", "change Date Exception ${e.message}")
    }
    return changedDate
}

fun validateOneValue(first: String?, second: String?): String {
    return when {
        !first.isNullOrEmpty() && first.isValidUrl() -> first.isValidURL()
        !second.isNullOrEmpty() && second.isValidUrl() -> second.isValidURL()
        else -> "null"
    }
}

fun validateOneString(first: String?, second: String?): String {
    return first ?: second ?: ""
}


/**
 * Regex is to extract Video Id from url
 *
 * http://www.youtube.com/watch?v=0zM3nApSvMg&feature=feedrec_grec_index
 * http://www.youtube.com/user/IngridMichaelsonVEVO#p/a/u/1/QdK8U-VIH_o
 * http://www.youtube.com/v/0zM3nApSvMg?fs=1&amp;hl=en_US&amp;rel=0
 * https://www.youtube.com/watch?v=0zM3nApSvMg#t=0m10s
 * http://www.youtube.com/embed/0zM3nApSvMg?rel=0
 * http://www.youtube.com/watch?v=0zM3nApSvMg
 * https://www.youtube.com/watch?v=aqz-KE-bpKQ
 * http://youtu.be/0zM3nApSvMg
 * https://youtube.com/shorts/0dPkkQeRwTI?feature=share
 * https://youtube.com/shorts/0dPkkQeRwTI
 */

fun String.getVideoId(): String? {
    val reg = "(youtu.*be.*)/(watch\\?v=|embed/|v|shorts|)(.*?((?=[&#?])|\$))"
    val pattern = Pattern.compile(reg)
    val matcher = pattern.matcher(this)
    return if (matcher.find()) matcher.group(3) else null
}