package com.tekskills.sampleapp.ui.poster.sticker.utils

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class FileUtils private constructor() {
    /**
     * @param prefix
     * @param extension
     * @return
     * @throws IOException
     */
    @Throws(IOException::class)
    fun createTempFile(prefix: String, extension: String): File {
        val file = File(
            appDirPath + ".TEMP/" + prefix
                    + System.currentTimeMillis() + extension
        )
        file.createNewFile()
        return file
    }

    /**
     *
     * @return
     */
    val appDirPath: String?
        get() {
            var path: String? = null
            if (localPath != null) {
                path = localPath + APP_DIR + "/"
            }
            return path
        }

    /**
     *
     * @return
     */
    val isSDCanWrite: Boolean
        get() {
            val status = Environment.getExternalStorageState()
            return (status == Environment.MEDIA_MOUNTED && Environment.getExternalStorageDirectory()
                .canWrite()
                    && Environment.getExternalStorageDirectory().canRead())
        }

    init {
        // 创建应用内容目录
        if (isSDCanWrite) {
            creatSDDir(APP_DIR)
            creatSDDir(TEMP_DIR)
        }
    }

    /**
     *
     * @param dirName
     */
    fun creatSDDir(dirName: String): File {
        val dir = File(localPath + dirName)
        dir.mkdirs()
        return dir
    }

    companion object {
        private var instance: FileUtils? = null
        private var mContext: Context? = null
        private const val APP_DIR = "Abner"
        private const val TEMP_DIR = "Abner/.TEMP"
        fun getInstance(context: Context): FileUtils? {
            if (instance == null) {
                synchronized(FileUtils::class.java) {
                    if (instance == null) {
                        mContext = context.applicationContext
                        instance = FileUtils()
                    }
                }
            }
            return instance
        }

        /**
         *
         * @param bm
         * @return
         */
        fun saveBitmapToLocal(bm: Bitmap, context: Context): String? {
            var path: String? = null
            path = try {
                val file = getInstance(context)!!
                    .createTempFile("IMG_", ".jpg")
                val fos = FileOutputStream(file)
                bm.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.flush()
                fos.close()
                file.absolutePath
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                return null
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            }
            return path
        }

        /**
         *
         * @return
         */
        private val localPath: String
            private get() {
                var sdPath: String? = null
                sdPath = mContext!!.filesDir.absolutePath + "/"
                return sdPath
            }
    }
}