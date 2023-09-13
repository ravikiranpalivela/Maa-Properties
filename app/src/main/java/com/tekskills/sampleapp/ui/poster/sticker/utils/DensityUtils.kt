package com.tekskills.sampleapp.ui.poster.sticker.utils

import android.content.Context
import android.view.WindowManager
import java.lang.reflect.Field

object DensityUtils {
    /**
     * dp px
     */
    fun dip2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * px to dp
     */
    fun px2dip(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    /**
     *
     * @param context
     * @return
     */
    fun getScreenWidth(context: Context): Int {
        val display = (context
            .getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        // Point p = new Point();
        // display.getSize(p);//need api13
        return display.width
    }

    fun getScreenHeight(context: Context): Int {
        val display = (context
            .getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        // Point p = new Point();
        // display.getSize(p);//need api13
        return display.height
    }

    /**
     *
     * @param context
     * @return
     */
    fun getStatusBarHeight(context: Context): Int {
        var c: Class<*>? = null
        var obj: Any? = null
        var field: Field? = null
        var x = 0
        var sbar = 38 //默认为38，貌似大部分是这样的
        try {
            c = Class.forName("com.android.internal.R\$dimen")
            obj = c.newInstance()
            field = c.getField("status_bar_height")
            x = field[obj].toString().toInt()
            sbar = context.resources.getDimensionPixelSize(x)
        } catch (e1: Exception) {
            e1.printStackTrace()
        }
        return sbar
    }

    fun getNavigationBarHeight(context: Context): Int {
        val resources = context.resources
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else 0
    }

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