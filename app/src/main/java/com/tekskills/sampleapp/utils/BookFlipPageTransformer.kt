package com.tekskills.sampleapp.utils

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

/**
 * A book based page flip animation PageTransformer implementation for ViewPager2
 *
 * Set the object of this transformer to any ViewPager object.
 * For example, myViewPager.setPageTransformer(true, new BookFlipPageTransformer2());
 *
 * @see [EasyFlipViewPager](http://github.com/wajahatkarim3/EasyFlipViewPager)
 *
 * @author Wajahat Karim (http://wajahatkarim.com)
 */
class BookFlipPageTransformer : ViewPager2.PageTransformer {
    private val LEFT = -1
    private val RIGHT = 1
    private val CENTER = 0

    //region Getters/Setters
    var scaleAmountPercent = 5f

    //endregion
    var isEnableScale = true

    override fun transformPage(page: View, position: Float) {
        val percentage = 1 - Math.abs(position)
        val viewPager = requireViewPager(page)
        // Don't move pages once they are on left or right
        if (position > CENTER && position <= RIGHT) {
            if (viewPager.orientation == ViewPager2.ORIENTATION_HORIZONTAL) {
                // This is behind page
                page.translationX = -position * page.width
                page.translationY = 0f
                page.translationZ = -1f
                page.rotation = 0f
                if (isEnableScale) {
                    val amount = 100 - scaleAmountPercent + scaleAmountPercent * percentage / 100
                    setSize(page, position, amount)
                }
            } else {
                // This is behind page
                page.translationY = -position * page.height
                page.translationX = 0f
                page.translationZ = -1f
                page.rotation = 0f
                if (isEnableScale) {
                    val amount = 100 - scaleAmountPercent + scaleAmountPercent * percentage / 100
                    setSize(page, position, amount)
                }
            }
        } else {
            page.visibility = View.VISIBLE
            flipPage(page, position, percentage)
        }
    }

    fun changeDateFormat(olddate: String): String {
        var changedDate: String = ""
        try {
            val dateFormatprev =
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH)
            val date = LocalDateTime.parse(olddate, dateFormatprev)
//            val dateFormatprev = SimpleDateFormat("yyyy-MM-dd")
//            val d: Date = dateFormatprev.parse(olddate)
            val dateFormat = SimpleDateFormat("EEE, MMM dd, yyyy")
            changedDate = dateFormat.format(date)
            println(changedDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return changedDate
    }

    private fun flipPage(page: View, position: Float, percentage: Float) {

        // Flip this page
        page.cameraDistance = -30000f
        setVisibility(page, position)
        setTranslation(page)
        if (requireViewPager(page).orientation == ViewPager2.ORIENTATION_HORIZONTAL) {
            setPivot(page, 0f, page.height * 0.5f)
        } else if (requireViewPager(page).orientation == ViewPager2.ORIENTATION_VERTICAL) {
            setPivot(page, page.width * 0.5f, 0f)
        }
        setRotation(page, position, percentage)
    }

    private fun setPivot(page: View, pivotX: Float, pivotY: Float) {
        page.pivotX = pivotX
        page.pivotY = pivotY
    }

    private fun setVisibility(page: View, position: Float) {
        if (position < 0.5 && position > -0.5) {
            page.visibility = View.VISIBLE
        } else {
            page.visibility = View.INVISIBLE
        }
    }

    private fun setTranslation(page: View) {
        val viewPager = requireViewPager(page)
        if (viewPager.orientation == ViewPager2.ORIENTATION_HORIZONTAL) {
            val scroll = viewPager.scrollX - page.left
            page.translationX = scroll.toFloat()
        } else if (viewPager.orientation == ViewPager2.ORIENTATION_VERTICAL) {
            val scroll = viewPager.scrollY - page.top
            page.translationY = scroll.toFloat()
        }
        page.translationZ = 1f
    }

    private fun setSize(page: View, position: Float, percentage: Float) {
        page.scaleX = (if (position != 0f && position != 1f) percentage else 1) as Float
        page.scaleY = (if (position != 0f && position != 1f) percentage else 1) as Float
    }

    private fun setRotation(page: View, position: Float, percentage: Float) {
        val viewPager = requireViewPager(page)
        if (viewPager.orientation == ViewPager2.ORIENTATION_HORIZONTAL) {
            if (position > 0) {
                page.rotationY = -180 * (percentage + 1)
            } else {
                page.rotationY = 180 * (percentage + 1)
            }
        } else if (viewPager.orientation == ViewPager2.ORIENTATION_VERTICAL) {
            if (position > 0) {
                page.rotationX = 180 * (percentage + 1)
            } else {
                page.rotationX = -180 * (percentage + 1)
            }
        }
    }

    private fun requireViewPager(page: View): ViewPager2 {
        val parent = page.parent
        val parentParent = parent.parent
        if (parent is RecyclerView && parentParent is ViewPager2) {
            return parentParent
        }
        throw IllegalStateException(
            "Expected the page view to be managed by a ViewPager2 instance."
        )
    }

}