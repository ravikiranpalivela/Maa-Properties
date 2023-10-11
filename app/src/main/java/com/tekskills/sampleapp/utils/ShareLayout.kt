package com.tekskills.sampleapp.utils

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import com.tekskills.sampleapp.utils.AppConstant.ARTICLE
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.Calendar


object ShareLayout {
    fun simpleLayoutShare(
        ctx: Context,
        view: View,
        msg: String?,
        activityOptions: ActivityOptionsCompat? = null
    ) {
        deleteImagesWithFileNameStartsWith(ctx, "VMR_")

        //Capture the Screenshot
        view.isDrawingCacheEnabled = true
        val bitmap = Bitmap.createBitmap(view.drawingCache)
        view.isDrawingCacheEnabled = false

        //Change into Bitmap
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) saveImageInQ(ctx,bitmap)
        else saveTheImageLegacyStyle(bitmap)


        val iconsStoragePath =
            ctx.getExternalFilesDir(ARTICLE).toString()

        val imageName = "VMR_" + Calendar.getInstance().time
        val pathOfBmp = MediaStore.Images.Media.insertImage(
            ctx.contentResolver,
            bitmap,
            imageName,
            null
        )

        //Share through Intent
        val uri = Uri.parse(pathOfBmp)
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/*"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Share")
        shareIntent.putExtra(
            Intent.EXTRA_TEXT, msg
        )
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        ctx.startActivity(Intent.createChooser(shareIntent, "hello hello"),activityOptions?.toBundle())
//        deleteFile(ctx,uri)
    }

    fun saveImageInQ(ctx: Context, bitmap: Bitmap):Uri {
        val filename = "IMG_${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        var imageUri: Uri? = null
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            put(MediaStore.Video.Media.IS_PENDING, 1)
        }

        //use application context to get contentResolver
        val contentResolver = ctx.contentResolver

        contentResolver.also { resolver ->
            imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            fos = imageUri?.let { resolver.openOutputStream(it) }
        }

        fos?.use { bitmap.compress(Bitmap.CompressFormat.JPEG, 70, it) }

        contentValues.clear()
        contentValues.put(MediaStore.Video.Media.IS_PENDING, 0)
        contentResolver.update(imageUri!!, contentValues, null, null)

        return imageUri!!
    }

    fun saveTheImageLegacyStyle(bitmap:Bitmap){
        val filename = "IMG_${System.currentTimeMillis()}.jpg"
        val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val image = File(imagesDir, filename)
        val fos = FileOutputStream(image)
        fos.use {bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)}
    }

    fun deleteFile(context: Context, uri: Uri): Boolean? {
        val file = File(uri.path)
        var selectionArgs = arrayOf(file.absolutePath)
        val contentResolver = context.contentResolver
        var where: String? = null
        var filesUri: Uri? = null
        if (Build.VERSION.SDK_INT >= 29) {
            filesUri = MediaStore.Images.Media.INTERNAL_CONTENT_URI
            where = MediaStore.Images.Media._ID + "=?"
            selectionArgs = arrayOf(file.name)
        } else {
            where = MediaStore.MediaColumns.DATA + "=?"
            filesUri = MediaStore.Files.getContentUri("external")
        }
        val result = contentResolver.delete(filesUri, where, selectionArgs)
        return file.exists()
    }

    private fun deleteImagesWithFileNameStartsWith(context: Context, prefix: String) {
        val contentResolver: ContentResolver = context.contentResolver
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA
        )
        val selection = "${MediaStore.Images.Media.DATA} LIKE ?"
        val selectionArgs = arrayOf("$prefix%")
        val queryUri = if (Build.VERSION.SDK_INT >= 29) {
            MediaStore.Images.Media.INTERNAL_CONTENT_URI
        } else {
            MediaStore.Files.getContentUri("external")
        }

        val cursor = contentResolver.query(queryUri, projection, selection, selectionArgs, null)

        cursor?.use {
            while (cursor.moveToNext()) {
                val imageId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                val imagePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageId)

                // Delete the file
                val file = File(imagePath)
                if (file.exists()) {
                    if (file.delete()) {
                        // If the file was deleted successfully, also remove it from the MediaStore
                        Log.d("TAG","Delete file at ${file.absolutePath}")
                        contentResolver.delete(imageUri, null, null)
                    }
                }
            }
        }
    }
}