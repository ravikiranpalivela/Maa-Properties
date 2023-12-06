package com.tekskills.sampleapp.utils

import android.app.DatePickerDialog
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.tekskills.sampleapp.R
import com.tekskills.sampleapp.ui.comment.CommentBottomSheet.Companion.TAG
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Base64
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object AppUtil {
    fun getRealPath(uri: Uri, context: Context): String? {
        var realPath: String? = null
        try {
            if (uri.scheme == AppConstant.CONTENT) {
                val projection = arrayOf(AppConstant.DATA)
                val cursor = context.contentResolver.query(
                    uri,
                    projection, null, null, null
                )
                if (cursor != null) {
                    val id = cursor.getColumnIndexOrThrow(AppConstant.DATA)
                    cursor.moveToNext()
                    realPath = try {
                        cursor.getString(id)
                    } catch (e: Exception) {
                        null
                    } finally {
                        cursor.close()
                    }
                } else if (uri.scheme == AppConstant.FILE) {
                    realPath = uri.path
                }
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            realPath = null
        }
        return realPath
    }
//
//    fun config(){
//        val ivspec: IvParameterSpec  = ""
//        val keyspec: SecretKeySpec  = ""
//        val cipher : Cipher  = Cipher.getInstance("AES/CTR/NoPadding")
//    }

//    fun utlIsNetworkAvailable(): Boolean {
//
//        val connectivityManager = ConfigProvider.getConfiguration().appContext.getSystemService(
//            Context.CONNECTIVITY_SERVICE
//        ) as ConnectivityManager?
//
//        val activeNetworkInfo = connectivityManager?.activeNetworkInfo
//        return activeNetworkInfo != null && activeNetworkInfo.isConnected
//    }

    fun ImageView.loadGif(gifImage: Int) {
        Glide.with(this.context).asGif().load(gifImage).into(this)
//        AppUtil.loadGlideImage(Glide.with(this.context),article.thumbnailUrl, binding.ivVideoPlayer)

    }

    fun EditText.hideKeyboard() {
        setText("")
        clearFocus()
        val imm = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    fun doItDecrypt(imgUrl: String): String {
        val key32Char = hexStringToByteArray("tVN0p+0LnGLy8U33RDZ+lg==")
        val iv32Char = hexStringToByteArray("")
//        val srcBuff = "0123456789012345x".toByteArray()
        val secretKeySpec = SecretKeySpec(key32Char, "AES")
        val ivParameterSpec = IvParameterSpec(iv32Char)
        try {
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec)
            val dstBuff = cipher.doFinal(imgUrl.toByteArray())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return String(Base64.getEncoder().encode(dstBuff))
            } else {
                val base64String =
                    android.util.Base64.encodeToString(dstBuff, android.util.Base64.DEFAULT)
                return base64String
            }

//            return String(Base64.getEncoder().encode(dstBuff))
        } catch (e: java.lang.Exception) {
            print(e.toString())
            return ""
        }
    }

    fun hexStringToByteArray(s: String): ByteArray {
        val len = s.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((s[i].digitToIntOrNull(16) ?: (-1 shl 4)) + s[i + 1].digitToIntOrNull(
                16
            )!!).toByte()
            i += 2
        }
        return data
    }

    fun decryptAndDecodeUrl(
        encryptedUrl: String,
        encryptionKey: String = "tVN0p+0LnGLy8U33RDZ+lg=="
    ): String {
        try {
            val key = SecretKeySpec(encryptionKey.toByteArray(StandardCharsets.UTF_8), "AES")
            val iv = IvParameterSpec(ByteArray(16)) // You may need to provide the actual IV

            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, key, iv)

//            val encryptedBytes = Base64.getDecoder().decode(encryptedUrl)
            val encryptedBytes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Base64.getDecoder().decode(encryptedUrl)
            } else {
                android.util.Base64.decode(encryptedUrl, android.util.Base64.DEFAULT)
            }

            val decryptedBytes = cipher.doFinal(encryptedBytes)

            return String(decryptedBytes, StandardCharsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ""
    }

    fun loadGlideImage(mContext: RequestManager, imageUrl: String?, ivImage: ImageView?) {
        mContext
            .load(imageUrl!!)
//            .apply(RequestOptions.overrideOf(300,300))
            .apply(RequestOptions.placeholderOf(R.drawable.place_holder))
            .apply(RequestOptions.errorOf(R.drawable.no_image))
//            .apply(RequestOptions.fitCenterTransform())
//            .error(AppConstant.ERROR_IMG).placeholder(R.drawable.loader)
            .into(ivImage!!)

//        val thumbnailRequest: DrawableRequestBuilder<String> =
//            mContext
//            .load(imageUrl)
//
//      mContext
//            .load(UsageExampleGifAndVideos.gifUrl)
//            .thumbnail(thumbnailRequest)
//            .into<Target<Drawable>>(imageView)
    }

//    fun showTwoButtonAlert(
//        context: Context,
//        title: String,
//        message: String,
//        positiveButtonText: String = AppConstant.OKAY,
//        negativeButtonText: String = AppConstant.CANCEL,
//        callback: DialogActionListener? = null
//    ) {
//
//        val dialogLayout = LayoutInflater.from(context).inflate(R.layout.dialog_two_buttons, null)
//        val dialog = AlertDialog.Builder(context)
//            .setView(dialogLayout)
//            .create()
//
//        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
//
//        dialogLayout.findViewById<TextView>(R.id.dialogTitle).text = title
//        dialogLayout.findViewById<TextView>(R.id.dialogMessage).text = message
//
//        val btDialogNegative = dialogLayout.findViewById<MaterialButton>(R.id.btDialogNegative)
//        btDialogNegative.text = negativeButtonText
//        btDialogNegative.setOnClickListener {
//            dialog.dismiss()
//            callback?.onNegativeClick()
//        }
//        val btDialogPositive = dialogLayout.findViewById<MaterialButton>(R.id.btDialogPositive)
//        btDialogPositive.text = positiveButtonText
//        btDialogPositive.setOnClickListener {
//            dialog.dismiss()
//            callback?.onPositiveClick()
//        }
//
//        dialog.show()
//    }

    fun showDatePicker(
        context: Context,
        listener: DatePickerListener,
        maxDate: Date?,
        minDate: Date?
    ) {
        val cal = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            context,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                listener.onDatePicked(calendar.time)
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        )
        minDate?.let {
            datePickerDialog.datePicker.minDate = it.time
        }
        maxDate?.let {
            datePickerDialog.datePicker.maxDate = it.time
        }
        datePickerDialog.show()
    }

    fun showSnackbar(rootView: View, message: String) {
        val snackBar = Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT)
        if (!snackBar.isShown) snackBar.show()
    }

    interface DatePickerListener {
        fun onDatePicked(date: Date)
    }

    interface DialogActionListener {
        fun onNegativeClick() {}
        fun onPositiveClick() {}
    }

    fun getLogDebugData(className: String, message: String) {
        Log.d(TAG, "$className.kt message $message")
    }

    fun getLogErrorData(className: String, message: String) {
        Log.e(TAG, "$className.kt message $message")
    }

    fun getDate(date: Long): String? {
        var tempDate = date
        tempDate *= DateUtils.SECOND_IN_MILLIS
        return SimpleDateFormat(AppConstant.DATE_TIME_FORMAT, Locale.getDefault()).format(
            Date(
                tempDate
            )
        )
    }
}