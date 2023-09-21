package com.tekskills.sampleapp.utils

import android.app.Activity
import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.Calendar


object ShareLayout {
    val TEXT_FONT_COLOR = 856

    fun simpleLayoutShare(
        activity: Activity,
        view: View,
        msg: String?,
        activityOptions: ActivityOptionsCompat? = null
    ) {
        deleteImagesWithFileNameStartsWith(activity, "VMR_")

        //Capture the Screenshot
        view.isDrawingCacheEnabled = true
        val bitmap = Bitmap.createBitmap(view.drawingCache)
        view.isDrawingCacheEnabled = false

        //Change into Bitmap
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)

        val imageName = "VMR_" + Calendar.getInstance().time

        val pathofBmp = MediaStore.Images.Media.insertImage(
            activity.contentResolver,
            bitmap,
            imageName,
            null
        )

        //Share through Intent
        val uri = Uri.parse(pathofBmp)
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/*"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Share")
        shareIntent.putExtra(
            Intent.EXTRA_TEXT, msg
        )
        val activityOptions =
            ActivityOptionsCompat.makeSceneTransitionAnimation(
                activity,
                view,
                "article_image"
            )
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
//        activity.startActivityForResult(Intent.createChooser(shareIntent, "hello hello"),TEXT_FONT_COLOR,activityOptions.toBundle())
        activity.startActivity(Intent.createChooser(shareIntent, "hello hello"),activityOptions.toBundle())
//        deleteFile(activity,uri)
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