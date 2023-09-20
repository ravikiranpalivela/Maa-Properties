package com.tekskills.sampleapp.utils.like


import android.animation.ArgbEvaluator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Property
import android.view.View
import androidx.annotation.ColorInt
import com.flask.colorpicker.Utils
import kotlin.math.roundToInt

class DotsView : View {
    private var COLOR_1 = -0x3ef9
    private var COLOR_2 = -0x6800
    private var COLOR_3 = -0xa8de
    private var COLOR_4 = -0xbbcca
    private var width = 0
    private var height = 0
    private val circlePaints = arrayOfNulls<Paint>(4)
    private var centerX = 0
    private var centerY = 0
    private var maxOuterDotsRadius = 0f
    private var maxInnerDotsRadius = 0f
    private var maxDotSize = 0f
    private var currentProgress = 0f
    private var currentRadius1 = 0f
    private var currentDotSize1 = 0f
    private var currentDotSize2 = 0f
    private var currentRadius2 = 0f
    private val argbEvaluator = ArgbEvaluator()

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
        for (i in circlePaints.indices) {
            circlePaints[i] = Paint()
            circlePaints[i]!!.style = Paint.Style.FILL
            circlePaints[i]!!.isAntiAlias = true
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = w / 2
        centerY = h / 2
        maxDotSize = 5f
        maxOuterDotsRadius = w / 2 - maxDotSize * 2
        maxInnerDotsRadius = 0.8f * maxOuterDotsRadius
    }

    override fun onDraw(canvas: Canvas) {
        drawOuterDotsFrame(canvas)
        drawInnerDotsFrame(canvas)
    }

    private fun drawOuterDotsFrame(canvas: Canvas) {
        for (i in 0 until DOTS_COUNT) {
            val cX =
                (centerX + currentRadius1 * Math.cos(i * OUTER_DOTS_POSITION_ANGLE * Math.PI / 180)).toInt()
            val cY =
                (centerY + currentRadius1 * Math.sin(i * OUTER_DOTS_POSITION_ANGLE * Math.PI / 180)).toInt()
            canvas.drawCircle(
                cX.toFloat(), cY.toFloat(), currentDotSize1,
                circlePaints[i % circlePaints.size]!!
            )
        }
    }

    private fun drawInnerDotsFrame(canvas: Canvas) {
        for (i in 0 until DOTS_COUNT) {
            val cX =
                (centerX + currentRadius2 * Math.cos((i * OUTER_DOTS_POSITION_ANGLE - 10) * Math.PI / 180)).toInt()
            val cY =
                (centerY + currentRadius2 * Math.sin((i * OUTER_DOTS_POSITION_ANGLE - 10) * Math.PI / 180)).toInt()
            canvas.drawCircle(
                cX.toFloat(), cY.toFloat(), currentDotSize2,
                circlePaints[(i + 1) % circlePaints.size]!!
            )
        }
    }

    fun setCurrentProgress(currentProgress: Float) {
        this.currentProgress = currentProgress
        updateInnerDotsPosition()
        updateOuterDotsPosition()
        updateDotsPaints()
        updateDotsAlpha()
        postInvalidate()
    }

    fun getCurrentProgress(): Float {
        return currentProgress
    }

    private fun updateInnerDotsPosition() {
        if (currentProgress < 0.3f) {
            currentRadius2 = LikeUtils.mapValueFromRangeToRange(
                currentProgress,
                0f,
                0.3f,
                0f,
                maxInnerDotsRadius
            ) as Float
        } else {
            currentRadius2 = maxInnerDotsRadius
        }
        if (currentProgress == 0f) {
            currentDotSize2 = 0f
        } else if (currentProgress < 0.2) {
            currentDotSize2 = maxDotSize
        } else if (currentProgress < 0.5) {
            currentDotSize2 = LikeUtils.mapValueFromRangeToRange(
                currentProgress,
                0.2f,
                0.5f,
                maxDotSize,
                0.3f * maxDotSize
            ) as Float
        } else {
            currentDotSize2 = LikeUtils.mapValueFromRangeToRange(
                currentProgress,
                0.5f,
                1f,
                maxDotSize * 0.3f,
                0f
            ) as Float
        }
    }

    private fun updateOuterDotsPosition() {
        if (currentProgress < 0.3f) {
            currentRadius1 = LikeUtils.mapValueFromRangeToRange(
                currentProgress,
                0.0f,
                0.3f,
                0f,
                maxOuterDotsRadius * 0.8f
            ) as Float
        } else {
            currentRadius1 = LikeUtils.mapValueFromRangeToRange(
                currentProgress,
                0.3f,
                1f,
                0.8f * maxOuterDotsRadius,
                maxOuterDotsRadius
            ) as Float
        }
        if (currentProgress == 0f) {
            currentDotSize1 = 0f
        } else if (currentProgress < 0.7) {
            currentDotSize1 = maxDotSize
        } else {
            currentDotSize1 =
                LikeUtils.mapValueFromRangeToRange(currentProgress, 0.7f, 1f, maxDotSize, 0f) as Float
        }
    }

    private fun updateDotsPaints() {
        if (currentProgress < 0.5f) {
            val progress = LikeUtils.mapValueFromRangeToRange(currentProgress, 0f, 0.5f, 0f, 1f) as Float
            circlePaints[0]!!.color = (argbEvaluator.evaluate(progress, COLOR_1, COLOR_2) as Int)
            circlePaints[1]!!.color = (argbEvaluator.evaluate(progress, COLOR_2, COLOR_3) as Int)
            circlePaints[2]!!.color = (argbEvaluator.evaluate(progress, COLOR_3, COLOR_4) as Int)
            circlePaints[3]!!.color = (argbEvaluator.evaluate(progress, COLOR_4, COLOR_1) as Int)
        } else {
            val progress = LikeUtils.mapValueFromRangeToRange(currentProgress, 0.5f, 1f, 0f, 1f) as Float
            circlePaints[0]!!.color = (argbEvaluator.evaluate(progress, COLOR_2, COLOR_3) as Int)
            circlePaints[1]!!.color = (argbEvaluator.evaluate(progress, COLOR_3, COLOR_4) as Int)
            circlePaints[2]!!.color = (argbEvaluator.evaluate(progress, COLOR_4, COLOR_1) as Int)
            circlePaints[3]!!.color = (argbEvaluator.evaluate(progress, COLOR_1, COLOR_2) as Int)
        }
    }

    fun setColors(@ColorInt primaryColor: Int, @ColorInt secondaryColor: Int) {
        COLOR_1 = primaryColor
        COLOR_2 = secondaryColor
        COLOR_3 = primaryColor
        COLOR_4 = secondaryColor
        invalidate()
    }

    private fun updateDotsAlpha() {
        val progress = LikeUtils.clamp(currentProgress, 0.6f, 1f) as Float
        val alpha = LikeUtils.mapValueFromRangeToRange(progress, 0.6f, 1f, 255f, 0f)
        circlePaints[0]!!.alpha = alpha.roundToInt()
        circlePaints[1]!!.alpha = alpha.roundToInt()
        circlePaints[2]!!.alpha = alpha.roundToInt()
        circlePaints[3]!!.alpha = alpha.roundToInt()
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

    companion object {
        private const val DOTS_COUNT = 7
        private const val OUTER_DOTS_POSITION_ANGLE = 51
        val DOTS_PROGRESS: Property<DotsView, Float> = object : Property<DotsView, Float>(
            Float::class.java, "dotsProgress"
        ) {
            override fun get(`object`: DotsView): Float {
                return `object`.getCurrentProgress()
            }

            override fun set(`object`: DotsView, value: Float) {
                `object`.setCurrentProgress(value)
            }
        }
    }
}