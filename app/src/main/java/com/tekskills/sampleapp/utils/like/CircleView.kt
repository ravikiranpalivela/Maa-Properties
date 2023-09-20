package com.tekskills.sampleapp.utils.like

import android.animation.ArgbEvaluator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.AttributeSet
import android.util.Property
import android.view.View
import androidx.annotation.ColorInt

class CircleView : View {
    private var START_COLOR = -0xa8de
    private var END_COLOR = -0x3ef9
    private val argbEvaluator = ArgbEvaluator()
    private val circlePaint = Paint()
    private val maskPaint = Paint()
    private var tempBitmap: Bitmap? = null
    private var tempCanvas: Canvas? = null
    private var outerCircleRadiusProgress = 0f
    private var innerCircleRadiusProgress = 0f
    private var width = 0
    private var height = 0
    private var maxCircleSize = 0

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        circlePaint.style = Paint.Style.FILL
        circlePaint.isAntiAlias = true
        maskPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        maskPaint.isAntiAlias = true
    }

    fun setSize(width: Int, height: Int) {
        this.width = width
        this.height = height
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (width != 0 && height != 0) setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        maxCircleSize = w / 2
        tempBitmap = Bitmap.createBitmap(getWidth(), getWidth(), Bitmap.Config.ARGB_8888)
        tempCanvas = Canvas(tempBitmap!!)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        tempCanvas!!.drawColor(0xffffff, PorterDuff.Mode.CLEAR)
        tempCanvas!!.drawCircle(
            (getWidth() / 2).toFloat(),
            (getHeight() / 2).toFloat(),
            outerCircleRadiusProgress * maxCircleSize,
            circlePaint
        )
        tempCanvas!!.drawCircle(
            (getWidth() / 2).toFloat(),
            (getHeight() / 2).toFloat(),
            innerCircleRadiusProgress * maxCircleSize + 1,
            maskPaint
        )
        canvas.drawBitmap(tempBitmap!!, 0f, 0f, null)
    }

    fun setInnerCircleRadiusProgress(innerCircleRadiusProgress: Float) {
        this.innerCircleRadiusProgress = innerCircleRadiusProgress
        postInvalidate()
    }

    fun getInnerCircleRadiusProgress(): Float {
        return innerCircleRadiusProgress
    }

    fun setOuterCircleRadiusProgress(outerCircleRadiusProgress: Float) {
        this.outerCircleRadiusProgress = outerCircleRadiusProgress
        updateCircleColor()
        postInvalidate()
    }

    private fun updateCircleColor() {
        var colorProgress = LikeUtils.clamp(outerCircleRadiusProgress, 0.5f, 1f) as Float
        colorProgress = LikeUtils.mapValueFromRangeToRange(colorProgress, 0.5f, 1f, 0f, 1f) as Float
        circlePaint.color = (argbEvaluator.evaluate(colorProgress, START_COLOR, END_COLOR) as Int)
    }

    fun getOuterCircleRadiusProgress(): Float {
        return outerCircleRadiusProgress
    }

    fun setStartColor(@ColorInt color: Int) {
        START_COLOR = color
        invalidate()
    }

    fun setEndColor(@ColorInt color: Int) {
        END_COLOR = color
        invalidate()
    }

    companion object {
        val INNER_CIRCLE_RADIUS_PROGRESS: Property<CircleView, Float> =
            object : Property<CircleView, Float>(
                Float::class.java, "innerCircleRadiusProgress"
            ) {
                override fun get(`object`: CircleView): Float {
                    return `object`.getInnerCircleRadiusProgress()
                }

                override fun set(`object`: CircleView, value: Float) {
                    `object`.setInnerCircleRadiusProgress(value)
                }
            }
        val OUTER_CIRCLE_RADIUS_PROGRESS: Property<CircleView, Float> =
            object : Property<CircleView, Float>(
                Float::class.java, "outerCircleRadiusProgress"
            ) {
                override fun get(`object`: CircleView): Float {
                    return `object`.getOuterCircleRadiusProgress()
                }

                override fun set(`object`: CircleView, value: Float) {
                    `object`.setOuterCircleRadiusProgress(value)
                }
            }
    }
}
