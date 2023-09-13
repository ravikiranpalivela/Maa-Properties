package com.tekskills.sampleapp.ui.poster

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Rect

class FrameCreate(private val mFrameName: String, left: Int,
                  top: Int, right: Int, bottom: Int, rorate: Float) {
    //Rect of picture area in frame
    private val mPictureRect: Rect

    //degree of rotation to fit picture and frame.
    private val mRorate: Float

    init {
        mPictureRect = Rect(left, top, right, bottom)
        mRorate = rorate
    }

    fun mergeWith(context: Context?,frameBitmap: Bitmap, pictureBitmap: Bitmap): Bitmap {
//        val frameBitmap: Bitmap = AssetsUtil.getBitmapFromAsset(context, mFrameName)
        val conf = Bitmap.Config.ARGB_8888
        val bitmap = Bitmap.createBitmap(frameBitmap.width, frameBitmap.height, conf)
        val canvas = Canvas(bitmap)
        val matrix: Matrix = getMatrix(pictureBitmap)
        canvas.drawBitmap(pictureBitmap, matrix, null)
        canvas.drawBitmap(frameBitmap, 0f, 0f, null)
        return bitmap
    }

    fun getMatrix(pictureBitmap: Bitmap): Matrix {
        val widthRatio = mPictureRect.width() / pictureBitmap.width.toFloat()
        val heightRatio = mPictureRect.height() / pictureBitmap.height.toFloat()
        val ratio: Float
        ratio = if (widthRatio > heightRatio) {
            widthRatio
        } else {
            heightRatio
        }
        val width = pictureBitmap.width * ratio
        val height = pictureBitmap.height * ratio
        val left = mPictureRect.left - (width - mPictureRect.width()) / 2f
        val top = mPictureRect.top - (height - mPictureRect.height()) / 2f
        val matrix = Matrix()
        matrix.postRotate(mRorate)
        matrix.postScale(ratio, ratio)
        matrix.postTranslate(left, top)
        return matrix
    }
}