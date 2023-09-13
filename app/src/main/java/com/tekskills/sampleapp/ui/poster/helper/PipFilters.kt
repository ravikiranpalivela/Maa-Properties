package com.tekskills.sampleapp.ui.poster.helper

import android.graphics.Bitmap
import net.alhazmy13.imagefilter.ImageFilter

class PipFilters {
    fun applyFilter(whichFilter: Int, bitmap: Bitmap?): Bitmap? {
        return when (whichFilter) {
            0 -> null
            1 -> ImageFilter.applyFilter(
                bitmap,
                ImageFilter.Filter.HDR
            )

            2 -> ImageFilter.applyFilter(
                bitmap,
                ImageFilter.Filter.LOMO
            )

            3 -> ImageFilter.applyFilter(
                bitmap,
                ImageFilter.Filter.SOFT_GLOW
            )

            4 -> ImageFilter.applyFilter(
                bitmap,
                ImageFilter.Filter.SKETCH
            )

            5 -> ImageFilter.applyFilter(
                bitmap,
                ImageFilter.Filter.GRAY
            )

            6 -> ImageFilter.applyFilter(
                bitmap,
                ImageFilter.Filter.BLOCK
            )

            7 -> ImageFilter.applyFilter(
                bitmap,
                ImageFilter.Filter.RELIEF
            )

            8 -> ImageFilter.applyFilter(
                bitmap,
                ImageFilter.Filter.TV
            )

            9 -> ImageFilter.applyFilter(
                bitmap,
                ImageFilter.Filter.OLD
            )

            else -> null
        }
    }
}