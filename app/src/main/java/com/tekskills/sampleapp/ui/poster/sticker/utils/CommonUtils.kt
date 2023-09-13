package com.tekskills.sampleapp.ui.poster.sticker.utils


object CommonUtils {
    private const val TAG = "CommonUtils"
    private const val lastClickTime: Long = 0
    private const val lastClickViewId = 0
    private const val KEY_PREVENT_TS = -20000

    /**
     * @param c
     * @return
     */
    fun calculateLength(c: CharSequence): Long {
        var len = 0.0
        for (i in 0 until c.length) {
            val tmp = c[i].code
            if (tmp > 0 && tmp < 127) {
                len += 0.5
            } else {
                len++
            }
        }
        return Math.round(len)
    }
}