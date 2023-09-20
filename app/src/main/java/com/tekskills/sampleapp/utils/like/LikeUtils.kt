package com.tekskills.sampleapp.utils.like

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.os.Build
import android.util.TypedValue
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.tekskills.sampleapp.R


object LikeUtils {
    fun mapValueFromRangeToRange(
        value: Float,
        fromLow: Float,
        fromHigh: Float,
        toLow: Float,
        toHigh: Float
    ): Float {
        return toLow + (value - fromLow) / (fromHigh - fromLow) * (toHigh - toLow)
    }

    fun clamp(value: Float, low: Float, high: Float): Float {
        return Math.min(Math.max(value, low), high)
    }

    val icons: List<Icon>
        get() {
            val icons: MutableList<Icon> = ArrayList()
            icons.add(Icon(R.drawable.heart_on, R.drawable.heart_off, IconType.Heart))
//            icons.add(Icon(R.drawable.star_on, R.drawable.star_off, IconType.Star))
//            icons.add(Icon(R.drawable.thumb_on, R.drawable.thumb_off, IconType.Thumb))
            return icons
        }

    fun resizeDrawable(context: Context, drawable: Drawable?, width: Int, height: Int): Drawable {
        val bitmap = getBitmap(drawable, width, height)
        return BitmapDrawable(
            context.resources,
            Bitmap.createScaledBitmap(bitmap, width, height, true)
        )
    }

    fun getBitmap(drawable: Drawable?, width: Int, height: Int): Bitmap {
        return if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else if (drawable is VectorDrawableCompat) {
            getBitmap(drawable, width, height)
        } else if (drawable is VectorDrawable) {
            getBitmap(drawable, width, height)
        } else {
            throw IllegalArgumentException("Unsupported drawable type")
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun getBitmap(vectorDrawable: VectorDrawable, width: Int, height: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable.draw(canvas)
        return bitmap
    }

    private fun getBitmap(vectorDrawable: VectorDrawableCompat, width: Int, height: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable.draw(canvas)
        return bitmap
    }

    fun dipToPixels(context: Context, dipValue: Float): Float {
        val metrics = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics)
    }
}

enum class IconType {
    Heart, Thumb, Star
}
