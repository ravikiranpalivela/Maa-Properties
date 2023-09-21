package com.tekskills.sampleapp.ui.poster.sticker

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PaintFlagsDrawFilter
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.Typeface
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.MotionEventCompat
import com.tekskills.sampleapp.R
import com.tekskills.sampleapp.ui.poster.sticker.model.BubblePropertyModel
import com.tekskills.sampleapp.ui.poster.sticker.utils.DensityUtils.dip2px
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.sqrt

class BubbleTextView : AppCompatImageView {
    private var deleteBitmap: Bitmap? = null
    private var flipVBitmap: Bitmap? = null
    private var topBitmap: Bitmap? = null
    private var resizeBitmap: Bitmap? = null
    private var mBitmap: Bitmap? = null
    private var originBitmap: Bitmap? = null
    private var dst_delete: Rect? = null
    private var dst_resize: Rect? = null
    private var dst_flipV: Rect? = null
    private var dst_top: Rect? = null
    private var deleteBitmapWidth = 0
    private var deleteBitmapHeight = 0
    private var resizeBitmapWidth = 0
    private var resizeBitmapHeight = 0
    private var flipVBitmapWidth = 0
    private var flipVBitmapHeight = 0

    //置顶
    private var topBitmapWidth = 0
    private var topBitmapHeight = 0
    private var localPaint: Paint? = null
    private var mScreenwidth = 0
    private var mScreenHeight = 0
    private val mid = PointF()
    private var operationListener: OperationListener? = null
    private var lastRotateDegree = 0f

    //是否是第二根手指放下
    private var isPointerDown = false

    private val pointerLimitDis = 20f
    private val pointerZoomCoeff = 0.09f
    private val moveLimitDis = 0.5f

    private var lastLength = 0f
    private var isInResize = false
    private val matrix = Matrix()

    private var isInSide = false
    private var lastX = 0f
    private var lastY = 0f

    private var isInEdit = true
    private var MIN_SCALE = 0.5f
    private var MAX_SCALE = 1.5f
    private var halfDiagonalLength = 0.0
    private var oringinWidth = 0f
    private var dm: DisplayMetrics? = null

    private val defaultStr: String

    private var mStr: String? = ""

    private val mDefultSize = 16f
    private var mFontSize = 16f

    private val mMaxFontSize = 25f
    private val mMinFontSize = 14f

    private val mDefaultMargin = 20f
    private var mMargin = 20f

    private var mFontPaint: TextPaint? = null
    private var canvasText: Canvas? = null
    private var fm: Paint.FontMetrics? = null

    private var baseline = 0f
    var isInit = true

    private var oldDis = 0f

    private var isDown = false

    private var isMove = false

    private var isUp = false

    private val isTop = true
    private var isInBitmap = false
    private var fontColor: Int
    private var fontTypeface: Typeface? = null
    private val bubbleId: Long

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        defaultStr = "Double-click to enter text"
        fontColor = Color.BLACK
        bubbleId = 0
        init()
    }

    constructor(context: Context?) : super(context!!) {
        defaultStr = "Double-click to enter text"
        fontColor = Color.BLACK
        bubbleId = 0
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!,
        attrs,
        defStyleAttr
    ) {
        defaultStr = "Double-click to enter text"
        fontColor = Color.BLACK
        bubbleId = 0
        init()
    }

    /**
     * @param context
     * @param fontColor
     * @param bubbleId  some fuck id
     */
    constructor(context: Context?, fontColor: Int, bubbleId: Long) : super(context!!) {
        defaultStr = "Double-click to enter text"
        this.fontColor = fontColor
        this.bubbleId = bubbleId
        init()
    }

    private fun init() {
        dm = resources.displayMetrics
        dst_delete = Rect()
        dst_resize = Rect()
        dst_flipV = Rect()
        dst_top = Rect()
        localPaint = Paint()
        localPaint!!.color =
            resources.getColor(R.color.colorRed)
        localPaint!!.isAntiAlias = true
        localPaint!!.isDither = true
        localPaint!!.style = Paint.Style.STROKE
        localPaint!!.strokeWidth = 2.0f
        mScreenwidth = dm!!.widthPixels
        mScreenHeight = dm!!.heightPixels
        mFontSize = mDefultSize
        mFontPaint = TextPaint()
        mFontPaint!!.textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mFontSize, dm)
        mFontPaint!!.color = fontColor
        mFontPaint!!.textAlign = Paint.Align.CENTER
        mFontPaint!!.isAntiAlias = true
        fm = mFontPaint!!.fontMetrics
        baseline = fm!!.descent - fm!!.ascent
        isInit = true
        mStr = defaultStr
    }

    override fun onDraw(canvas: Canvas) {
        if (mBitmap != null) {
            val arrayOfFloat = FloatArray(9)
            matrix.getValues(arrayOfFloat)
            val f1 = 0.0f * arrayOfFloat[0] + 0.0f * arrayOfFloat[1] + arrayOfFloat[2]
            val f2 = 0.0f * arrayOfFloat[3] + 0.0f * arrayOfFloat[4] + arrayOfFloat[5]
            val f3 = arrayOfFloat[0] * mBitmap!!.width + 0.0f * arrayOfFloat[1] + arrayOfFloat[2]
            val f4 = arrayOfFloat[3] * mBitmap!!.width + 0.0f * arrayOfFloat[4] + arrayOfFloat[5]
            val f5 = 0.0f * arrayOfFloat[0] + arrayOfFloat[1] * mBitmap!!.height + arrayOfFloat[2]
            val f6 = 0.0f * arrayOfFloat[3] + arrayOfFloat[4] * mBitmap!!.height + arrayOfFloat[5]
            val f7 =
                arrayOfFloat[0] * mBitmap!!.width + arrayOfFloat[1] * mBitmap!!.height + arrayOfFloat[2]
            val f8 =
                arrayOfFloat[3] * mBitmap!!.width + arrayOfFloat[4] * mBitmap!!.height + arrayOfFloat[5]
            canvas.save()

            //先往文字上绘图
            mBitmap = originBitmap!!.copy(Bitmap.Config.ARGB_8888, true)
            canvasText!!.setBitmap(mBitmap)
            canvasText!!.drawFilter =
                PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
            canvas.drawFilter =
                PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
            val left = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15f, dm)
            val scalex = arrayOfFloat[Matrix.MSCALE_X]
            val skewy = arrayOfFloat[Matrix.MSKEW_Y]
            val rScale = Math.sqrt((scalex * scalex + skewy * skewy).toDouble()).toFloat()
            val size = rScale * 0.75f * mDefultSize
            mFontSize = if (size > mMaxFontSize) {
                mMaxFontSize
            } else if (size < mMinFontSize) {
                mMinFontSize
            } else {
                size
            }
            mFontPaint!!.textSize =
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mFontSize, dm)
            mFontPaint!!.color = fontColor
            if (fontTypeface != null) {
                mFontPaint!!.typeface = fontTypeface
            }
            val texts = autoSplit(mStr, mFontPaint, mBitmap!!.width - left * 3)
            val height = texts.size * (baseline + fm!!.leading) + baseline
            var top = (mBitmap!!.height - height) / 2
            top += baseline
            for (text in texts) {
                if (TextUtils.isEmpty(text)) {
                    continue
                }
                canvasText!!.drawText(
                    text!!,
                    (mBitmap!!.width / 2).toFloat(),
                    top,
                    mFontPaint!!
                )
                top += baseline + fm!!.leading
            }
            canvas.drawBitmap(mBitmap!!, matrix, null)


            dst_delete!!.left = (f3 - deleteBitmapWidth / 2).toInt()
            dst_delete!!.right = (f3 + deleteBitmapWidth / 2).toInt()
            dst_delete!!.top = (f4 - deleteBitmapHeight / 2).toInt()
            dst_delete!!.bottom = (f4 + deleteBitmapHeight / 2).toInt()

            dst_resize!!.left = (f7 - resizeBitmapWidth / 2).toInt()
            dst_resize!!.right = (f7 + resizeBitmapWidth / 2).toInt()
            dst_resize!!.top = (f8 - resizeBitmapHeight / 2).toInt()
            dst_resize!!.bottom = (f8 + resizeBitmapHeight / 2).toInt()

            dst_top!!.left = (f1 - topBitmapWidth / 2).toInt()
            dst_top!!.right = (f1 + topBitmapWidth / 2).toInt()
            dst_top!!.top = (f2 - topBitmapHeight / 2).toInt()
            dst_top!!.bottom = (f2 + topBitmapHeight / 2).toInt()

//                dst_flipV.left = (int) (f5 - topBitmapWidth / 2);
//                dst_flipV.right = (int) (f5 + topBitmapWidth / 2);
//                dst_flipV.top = (int) (f6 - topBitmapHeight / 2);
//                dst_flipV.bottom = (int) (f6 + topBitmapHeight / 2);
            if (isInEdit) {
                canvas.drawLine(f1, f2, f3, f4, localPaint!!)
                canvas.drawLine(f3, f4, f7, f8, localPaint!!)
                canvas.drawLine(f5, f6, f7, f8, localPaint!!)
                canvas.drawLine(f5, f6, f1, f2, localPaint!!)
                canvas.drawBitmap(deleteBitmap!!, null, dst_delete!!, null)
                canvas.drawBitmap(resizeBitmap!!, null, dst_resize!!, null)
                //                canvas.drawBitmap(flipVBitmap, null, dst_flipV, null);
                canvas.drawBitmap(topBitmap!!, null, dst_top!!, null)
            }
            canvas.restore()
        }
    }

    fun setText(text: String, fontColor: Int, typeface: Typeface?) {
        if (text.isEmpty()) {
            mStr = defaultStr
            mFontSize = mDefultSize
            mMargin = mDefaultMargin
        } else {
            mStr = text
        }
        if (fontColor != 0) {
            this.fontColor = fontColor
        } else {
            this.fontColor = Color.BLACK
        }
        if (typeface != null) {
            fontTypeface = typeface
        } else {
            fontTypeface = null
        }
        if (fontColor != 0) invalidate()
    }

    override fun setImageResource(resId: Int) {
        matrix.reset()
        setBitmap(BitmapFactory.decodeResource(resources, resId))
    }

    fun setImageResource(resId: Int, model: BubblePropertyModel) {
        matrix.reset()
        setBitmap(BitmapFactory.decodeResource(resources, resId), model)
    }

    fun setBitmap(bitmap: Bitmap?, model: BubblePropertyModel) {
        mFontSize = mDefultSize
        originBitmap = bitmap
        mBitmap = originBitmap!!.copy(Bitmap.Config.ARGB_8888, true)
        canvasText = Canvas(mBitmap!!)
        setDiagonalLength()
        initBitmaps()
        val w = mBitmap!!.width
        val h = mBitmap!!.height
        oringinWidth = w.toFloat()
        mStr = model.text
        var scale = model.scaling * mScreenwidth / mBitmap!!.width
        if (scale > MAX_SCALE) {
            scale = MAX_SCALE
        } else if (scale < MIN_SCALE) {
            scale = MIN_SCALE
        }
        val degree = Math.toDegrees(model.degree.toDouble()).toFloat()
        matrix.postRotate(-degree, (w shr 1).toFloat(), (h shr 1).toFloat())
        matrix.postScale(scale, scale, (w shr 1).toFloat(), (h shr 1).toFloat())
        var midX = model.getxLocation() * mScreenwidth
        var midY = model.getyLocation() * mScreenwidth
        val offset = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 22f, dm)
        midX = midX - w * scale / 2 - offset
        midY = midY - h * scale / 2 - offset
        matrix.postTranslate(midX, midY)
        invalidate()
    }

    fun setBitmap(bitmap: Bitmap?) {
        mFontSize = mDefultSize
        originBitmap = bitmap
        mBitmap = originBitmap!!.copy(Bitmap.Config.ARGB_8888, true)
        canvasText = Canvas(mBitmap!!)
        setDiagonalLength()
        initBitmaps()
        val w = mBitmap!!.width
        val h = mBitmap!!.height
        oringinWidth = w.toFloat()
        val topbarHeight = dip2px(context, 50f).toFloat()
        matrix.postTranslate(
            (mScreenwidth / 2 - w / 2).toFloat(),
            (mScreenwidth / 2 - h / 2).toFloat()
        )
        invalidate()
    }

    private fun setDiagonalLength() {
        halfDiagonalLength = Math.hypot(mBitmap!!.width.toDouble(), mBitmap!!.height.toDouble()) / 2
    }

    private fun initBitmaps() {
        val minWidth = (mScreenwidth / 8).toFloat()
        MIN_SCALE = if (mBitmap!!.width < minWidth) {
            1f
        } else {
            1.0f * minWidth / mBitmap!!.width
        }
        MAX_SCALE = if (mBitmap!!.width > mScreenwidth) {
            1f
        } else {
            1.0f * mScreenwidth / mBitmap!!.width
        }
        topBitmap = BitmapFactory.decodeResource(resources, R.mipmap.icon_top_enable)
        deleteBitmap = BitmapFactory.decodeResource(resources, R.mipmap.icon_delete)
        flipVBitmap = BitmapFactory.decodeResource(resources, R.mipmap.icon_flip)
        resizeBitmap = BitmapFactory.decodeResource(resources, R.mipmap.icon_resize)
        deleteBitmapWidth = (deleteBitmap!!.width * BITMAP_SCALE).toInt()
        deleteBitmapHeight = (deleteBitmap!!.height * BITMAP_SCALE).toInt()
        resizeBitmapWidth = (resizeBitmap!!.width * BITMAP_SCALE).toInt()
        resizeBitmapHeight = (resizeBitmap!!.height * BITMAP_SCALE).toInt()
        flipVBitmapWidth = (flipVBitmap!!.width * BITMAP_SCALE).toInt()
        flipVBitmapHeight = (flipVBitmap!!.height * BITMAP_SCALE).toInt()
        topBitmapWidth = (topBitmap!!.width * BITMAP_SCALE).toInt()
        topBitmapHeight = (topBitmap!!.height * BITMAP_SCALE).toInt()
    }

    private var preClicktime: Long = 0
    private val doubleClickTimeLimit: Long = 200
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = MotionEventCompat.getActionMasked(event)
        var handled = true
        isInBitmap = false
        when (action) {
            MotionEvent.ACTION_DOWN -> if (isInButton(event, dst_delete)) {
                if (operationListener != null) {
                    operationListener!!.onDeleteClick()
                }
                isDown = false
            } else if (isInResize(event)) {
                isInResize = true
                lastRotateDegree = rotationToStartPoint(event)
                midPointToStartPoint(event)
                lastLength = diagonalLength(event)
                isDown = false
            } else if (isInButton(event, dst_flipV)) {
                val localPointF = PointF()
                midDiagonalPoint(localPointF)
                matrix.postScale(-1.0f, 1.0f, localPointF.x, localPointF.y)
                isDown = false
                invalidate()
            } else if (isInButton(event, dst_top)) {
                bringToFront()
                if (operationListener != null) {
                    operationListener!!.onTop(this)
                }
                isDown = false
            } else if (isInBitmap(event)) {
                isInSide = true
                lastX = event.getX(0)
                lastY = event.getY(0)
                isDown = true
                isMove = false
                isPointerDown = false
                isUp = false
                isInBitmap = true
                val currentTime = System.currentTimeMillis()
                Log.d(TAG, (currentTime - preClicktime).toString() + "")
                if (currentTime - preClicktime > doubleClickTimeLimit) {
                    preClicktime = currentTime
                } else {
                    if (isInEdit && operationListener != null) {
                        operationListener!!.onClick(this)
                    }
                }
            } else {
                handled = false
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                if (spacing(event) > pointerLimitDis) {
                    oldDis = spacing(event)
                    isPointerDown = true
                    midPointToStartPoint(event)
                } else {
                    isPointerDown = false
                }
                isInSide = false
                isInResize = false
            }

            MotionEvent.ACTION_MOVE ->
                if (isPointerDown) {
                    var scale: Float
                    val disNew = spacing(event)
                    if (disNew == 0f || disNew < pointerLimitDis) {
                        scale = 1f
                    } else {
                        scale = disNew / oldDis
                        //缩放缓慢
                        scale = (scale - 1) * pointerZoomCoeff + 1
                    }
                    val scaleTemp =
                        scale * Math.abs(dst_flipV!!.left - dst_resize!!.left) / oringinWidth
                    if (scaleTemp <= MIN_SCALE && scale < 1 ||
                        scaleTemp >= MAX_SCALE && scale > 1
                    ) {
                        scale = 1f
                    } else {
                        lastLength = diagonalLength(event)
                    }
                    matrix.postScale(scale, scale, mid.x, mid.y)
                    invalidate()
                } else if (isInResize) {
                    matrix.postRotate(
                        (rotationToStartPoint(event) - lastRotateDegree) * 2,
                        mid.x,
                        mid.y
                    )
                    lastRotateDegree = rotationToStartPoint(event)
                    var scale = diagonalLength(event) / lastLength
                    if (diagonalLength(event) / halfDiagonalLength <= MIN_SCALE && scale < 1 ||
                        diagonalLength(event) / halfDiagonalLength >= MAX_SCALE && scale > 1
                    ) {
                        scale = 1f
                        if (!isInResize(event)) {
                            isInResize = false
                        }
                    } else {
                        lastLength = diagonalLength(event)
                    }
                    matrix.postScale(scale, scale, mid.x, mid.y)
                    invalidate()
                } else if (isInSide) {
                    val x = event.getX(0)
                    val y = event.getY(0)

                    isMove =
                        !(!isMove && Math.abs(x - lastX) < moveLimitDis && Math.abs(y - lastY) < moveLimitDis)
                    matrix.postTranslate(x - lastX, y - lastY)
                    lastX = x
                    lastY = y
                    invalidate()
                }

            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                isInResize = false
                isInSide = false
                isPointerDown = false
                isUp = true
            }
        }
        if (handled && operationListener != null) {
            operationListener!!.onEdit(this)
        }
        //
//        if (isDoubleClick && isDown && !isPointerDown && !isMove && isUp && isInBitmap && isInEdit && operationListener != null) {
//            operationListener.onClick(this);
//        }
        return handled
    }

    fun calculate(model: BubblePropertyModel): BubblePropertyModel {
        val v = FloatArray(9)
        matrix.getValues(v)
        // translation is simple
        val tx = v[Matrix.MTRANS_X]
        val ty = v[Matrix.MTRANS_Y]
        Log.d(TAG, "tx : $tx ty : $ty")
        // calculate real scale
        val scalex = v[Matrix.MSCALE_X]
        val skewy = v[Matrix.MSKEW_Y]
        val rScale = sqrt((scalex * scalex + skewy * skewy).toDouble()).toFloat()
        Log.d(TAG, "rScale : $rScale")
        // calculate the degree of rotation
        val rAngle = Math.round(
            Math.atan2(
                v[Matrix.MSKEW_X].toDouble(),
                v[Matrix.MSCALE_X].toDouble()
            ) * (180 / Math.PI)
        ).toFloat()
        Log.d(TAG, "rAngle : $rAngle")
        val minX = ((dst_top!!.centerX() + dst_resize!!.centerX()) / 2).toFloat()
        val minY = ((dst_top!!.centerY() + dst_resize!!.centerY()) / 2).toFloat()
        Log.d(TAG, "midX : $minX midY : $minY")
        model.degree = Math.toRadians(rAngle.toDouble()).toFloat()
        model.bubbleId = bubbleId

        val precentWidth = mBitmap!!.width * rScale / mScreenwidth
        model.scaling = precentWidth
        Log.d(TAG, " x " + minX / mScreenwidth + " y " + minY / mScreenwidth)
        model.setxLocation(minX / mScreenwidth)
        model.setyLocation(minY / mScreenwidth)
        model.text = mStr
        return model
    }

    /**
     *
     * @return
     */
    private fun isInBitmap(event: MotionEvent): Boolean {
        val arrayOfFloat1 = FloatArray(9)
        matrix.getValues(arrayOfFloat1)
        val f1 = 0.0f * arrayOfFloat1[0] + 0.0f * arrayOfFloat1[1] + arrayOfFloat1[2]
        val f2 = 0.0f * arrayOfFloat1[3] + 0.0f * arrayOfFloat1[4] + arrayOfFloat1[5]
        val f3 = arrayOfFloat1[0] * mBitmap!!.width + 0.0f * arrayOfFloat1[1] + arrayOfFloat1[2]
        val f4 = arrayOfFloat1[3] * mBitmap!!.width + 0.0f * arrayOfFloat1[4] + arrayOfFloat1[5]
        val f5 = 0.0f * arrayOfFloat1[0] + arrayOfFloat1[1] * mBitmap!!.height + arrayOfFloat1[2]
        val f6 = 0.0f * arrayOfFloat1[3] + arrayOfFloat1[4] * mBitmap!!.height + arrayOfFloat1[5]
        val f7 =
            arrayOfFloat1[0] * mBitmap!!.width + arrayOfFloat1[1] * mBitmap!!.height + arrayOfFloat1[2]
        val f8 =
            arrayOfFloat1[3] * mBitmap!!.width + arrayOfFloat1[4] * mBitmap!!.height + arrayOfFloat1[5]
        val arrayOfFloat2 = FloatArray(4)
        val arrayOfFloat3 = FloatArray(4)
        arrayOfFloat2[0] = f1
        arrayOfFloat2[1] = f3
        arrayOfFloat2[2] = f7
        arrayOfFloat2[3] = f5
        arrayOfFloat3[0] = f2
        arrayOfFloat3[1] = f4
        arrayOfFloat3[2] = f8
        arrayOfFloat3[3] = f6
        return pointInRect(arrayOfFloat2, arrayOfFloat3, event.getX(0), event.getY(0))
    }

    /**
     *
     * @param xRange
     * @param yRange
     * @param x
     * @param y
     * @return
     */
    private fun pointInRect(xRange: FloatArray, yRange: FloatArray, x: Float, y: Float): Boolean {

        val a1 = Math.hypot((xRange[0] - xRange[1]).toDouble(), (yRange[0] - yRange[1]).toDouble())
        val a2 = Math.hypot((xRange[1] - xRange[2]).toDouble(), (yRange[1] - yRange[2]).toDouble())
        val a3 = Math.hypot((xRange[3] - xRange[2]).toDouble(), (yRange[3] - yRange[2]).toDouble())
        val a4 = Math.hypot((xRange[0] - xRange[3]).toDouble(), (yRange[0] - yRange[3]).toDouble())

        val b1 = Math.hypot((x - xRange[0]).toDouble(), (y - yRange[0]).toDouble())
        val b2 = Math.hypot((x - xRange[1]).toDouble(), (y - yRange[1]).toDouble())
        val b3 = Math.hypot((x - xRange[2]).toDouble(), (y - yRange[2]).toDouble())
        val b4 = Math.hypot((x - xRange[3]).toDouble(), (y - yRange[3]).toDouble())
        val u1 = (a1 + b1 + b2) / 2
        val u2 = (a2 + b2 + b3) / 2
        val u3 = (a3 + b3 + b4) / 2
        val u4 = (a4 + b4 + b1) / 2


        val s = a1 * a2
        val ss = (sqrt(u1 * (u1 - a1) * (u1 - b1) * (u1 - b2))
                + sqrt(u2 * (u2 - a2) * (u2 - b2) * (u2 - b3))
                + sqrt(u3 * (u3 - a3) * (u3 - b3) * (u3 - b4))
                + sqrt(u4 * (u4 - a4) * (u4 - b4) * (u4 - b1)))
        return abs(s - ss) < 0.5
    }

    private fun isInButton(event: MotionEvent, rect: Rect?): Boolean {
        val left = rect!!.left
        val right = rect.right
        val top = rect.top
        val bottom = rect.bottom
        return event.getX(0) >= left && event.getX(0) <= right && event.getY(0) >= top && event.getY(
            0
        ) <= bottom
    }

    private fun isInResize(event: MotionEvent): Boolean {
        val left = -20 + dst_resize!!.left
        val top = -20 + dst_resize!!.top
        val right = 20 + dst_resize!!.right
        val bottom = 20 + dst_resize!!.bottom
        return event.getX(0) >= left && event.getX(0) <= right && event.getY(0) >= top && event.getY(
            0
        ) <= bottom
    }

    private fun midPointToStartPoint(event: MotionEvent) {
        val arrayOfFloat = FloatArray(9)
        matrix.getValues(arrayOfFloat)
        val f1 = 0.0f * arrayOfFloat[0] + 0.0f * arrayOfFloat[1] + arrayOfFloat[2]
        val f2 = 0.0f * arrayOfFloat[3] + 0.0f * arrayOfFloat[4] + arrayOfFloat[5]
        val f3 = f1 + event.getX(0)
        val f4 = f2 + event.getY(0)
        mid[f3 / 2] = f4 / 2
    }

    private fun midDiagonalPoint(paramPointF: PointF) {
        val arrayOfFloat = FloatArray(9)
        matrix.getValues(arrayOfFloat)
        val f1 = 0.0f * arrayOfFloat[0] + 0.0f * arrayOfFloat[1] + arrayOfFloat[2]
        val f2 = 0.0f * arrayOfFloat[3] + 0.0f * arrayOfFloat[4] + arrayOfFloat[5]
        val f3 =
            arrayOfFloat[0] * mBitmap!!.width + arrayOfFloat[1] * mBitmap!!.height + arrayOfFloat[2]
        val f4 =
            arrayOfFloat[3] * mBitmap!!.width + arrayOfFloat[4] * mBitmap!!.height + arrayOfFloat[5]
        val f5 = f1 + f3
        val f6 = f2 + f4
        paramPointF[f5 / 2.0f] = f6 / 2.0f
    }

    /**
     *
     * @param event
     * @return
     */
    private fun rotationToStartPoint(event: MotionEvent): Float {
        val arrayOfFloat = FloatArray(9)
        matrix.getValues(arrayOfFloat)
        val x = 0.0f * arrayOfFloat[0] + 0.0f * arrayOfFloat[1] + arrayOfFloat[2]
        val y = 0.0f * arrayOfFloat[3] + 0.0f * arrayOfFloat[4] + arrayOfFloat[5]
        val arc = Math.atan2((event.getY(0) - y).toDouble(), (event.getX(0) - x).toDouble())
        return Math.toDegrees(arc).toFloat()
    }

    /**
     *
     * @param event
     * @return
     */
    private fun diagonalLength(event: MotionEvent): Float {
        return Math.hypot((event.getX(0) - mid.x).toDouble(), (event.getY(0) - mid.y).toDouble())
            .toFloat()
    }

    /**
     * Determine the space between the first two fingers
     */
    private fun spacing(event: MotionEvent): Float {
        return if (event.pointerCount == 2) {
            val x = event.getX(0) - event.getX(1)
            val y = event.getY(0) - event.getY(1)
            Math.sqrt((x * x + y * y).toDouble()).toFloat()
        } else {
            0f
        }
    }

    interface OperationListener {
        fun onDeleteClick()
        fun onEdit(bubbleTextView: BubbleTextView?)
        fun onClick(bubbleTextView: BubbleTextView?)
        fun onTop(bubbleTextView: BubbleTextView?)
    }

    fun setOperationListener(operationListener: OperationListener?) {
        this.operationListener = operationListener
    }

    fun setInEdit(isInEdit: Boolean) {
        this.isInEdit = isInEdit
        invalidate()
    }

    /**
     *
     * @param content
     * @param p
     * @param width
     * @return
     */
    private fun autoSplit(content: String?, p: Paint?, width: Float): Array<String?> {
        val length = content!!.length
        val textWidth = p!!.measureText(content)
        if (textWidth <= width) {
            return arrayOf(content)
        }
        var start = 0
        var end = 1
        var i = 0
        val lines = ceil((textWidth / width).toDouble()).toInt()
        val lineTexts = arrayOfNulls<String>(lines)
        while (start < length) {
            if (p.measureText(content, start, end) > width) {
                lineTexts[i++] = content.subSequence(start, end) as String
                start = end
            }
            if (end == length) {
                lineTexts[i] = content.subSequence(start, end) as String
                break
            }
            end += 1
        }
        return lineTexts
    }

    fun getmStr(): String? {
        return mStr
    }

    companion object {
        private const val TAG = "BubbleTextView"
        private const val BITMAP_SCALE = 0.7f
    }
}