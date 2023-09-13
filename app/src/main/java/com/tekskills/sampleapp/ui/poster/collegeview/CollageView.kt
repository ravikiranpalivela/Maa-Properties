package com.tekskills.sampleapp.ui.poster.collegeview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class CollageView(context: Context?, attrs: AttributeSet?, defStyle: Int) : AppCompatImageView(
    context!!, attrs, defStyle
) {
    private var mBorderPaint: Paint? = null

    @JvmOverloads
    constructor(context: Context?, attrs: AttributeSet? = null) : this(context, attrs, 0) {
        setPadding(PADDING, PADDING, PADDING, PADDING)
    }

    init {
        initBorderPaint()
    }

    private fun initBorderPaint() {
        mBorderPaint = Paint()
        mBorderPaint!!.isAntiAlias = true
        mBorderPaint!!.style = Paint.Style.STROKE
        mBorderPaint!!.color = Color.WHITE
        mBorderPaint!!.strokeWidth = STROKE_WIDTH
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(
            PADDING.toFloat(),
            PADDING.toFloat(),
            (width - PADDING).toFloat(),
            (height - PADDING).toFloat(),
            mBorderPaint!!
        )
    }

    companion object {
        private const val PADDING = 2
        private const val STROKE_WIDTH = 8.0f
    }
}