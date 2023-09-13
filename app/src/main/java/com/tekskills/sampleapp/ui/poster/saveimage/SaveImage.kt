package com.tekskills.sampleapp.ui.poster.saveimage

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class SaveImage {
    private var mContext: Context? = null
    fun save(context: Context?, view: View): Boolean {
        mContext = context
        val screenShot = takeScreenShot(view)
        val currentImageName = "PIP" + System.currentTimeMillis() + ".png"
        return storeImage(screenShot, currentImageName)
    }

    private fun takeScreenShot(view: View): Bitmap {
        view.isDrawingCacheEnabled = true
        view.buildDrawingCache(true)
        val screenShot = Bitmap.createBitmap(view.drawingCache)
        view.isDrawingCacheEnabled = false
        return screenShot
    }

    private fun storeImage(bitmap: Bitmap, fileName: String): Boolean {
        val dirPath = mContext!!.getExternalFilesDir(null)!!.absolutePath + "/PIPCamera"
        val dir = File(dirPath)
        if (!dir.exists()) {
            dir.mkdir()
        }
        val file = File(dirPath, fileName)
        return try {
            val fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
            notifySystemGallery(file)
            true
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            false
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    fun notifySystemGallery(file: File) {
        require(!(file == null || !file.exists())) { "bmp should not be null" }
        try {
            MediaStore.Images.Media.insertImage(
                mContext!!.contentResolver,
                file.absolutePath,
                file.name,
                null
            )
        } catch (e: FileNotFoundException) {
            throw IllegalStateException("File couldn't be found")
        }
        mContext!!.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)))
    }
}