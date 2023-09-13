package com.tekskills.sampleapp.ui.poster.costumDialog

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.tekskills.sampleapp.R
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.OnColorSelectedListener
import com.flask.colorpicker.builder.ColorPickerClickListener
import com.flask.colorpicker.builder.ColorPickerDialogBuilder

class TextInputDialog : AppCompatActivity() {
    var mTextDialog: EditText? = null
    var mOkDialog: Button? = null
    var mCancelDialog: Button? = null
    var mColorDialog: Button? = null
    var mTextFontDialog: Button? = null
    var sampleTextDialog: TextView? = null
    protected var mColor = 0
    var fontName: String? = null
    var text: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.text_input_dialog)
        mTextDialog = findViewById(R.id.custom_dialog_text)
        mOkDialog = findViewById(R.id.costum_dialog_ok)
        mCancelDialog = findViewById(R.id.costum_dialog_cancel)
        mColorDialog = findViewById(R.id.custom_dialog_text_color)
        mTextFontDialog = findViewById(R.id.custom_dialog_text_type_face)
        sampleTextDialog = findViewById(R.id.custom_dialog_sampe_text)
        listeners()
    }

    private fun listeners() {
        mOkDialog!!.setOnClickListener {
            text = mTextDialog!!.text.toString()
            if (text!!.isEmpty()) {
                Toast.makeText(applicationContext, "Text cannot be empty", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val returnIntent = Intent()
                returnIntent.putExtra("text", text)
                returnIntent.putExtra("font", fontName)
                returnIntent.putExtra("color", mColor)
                setResult(RESULT_OK, returnIntent)
                finish()
            }
        }
        mCancelDialog!!.setOnClickListener {
            val returnIntent = Intent()
            setResult(RESULT_CANCELED, returnIntent)
            finish()
        }
        mColorDialog!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                colour
            }
        })
        mTextFontDialog!!.setOnClickListener { setFont() }
        mTextDialog!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                sampleTextDialog!!.text = s
            }

            override fun afterTextChanged(s: Editable) {
                if (sampleTextDialog!!.text.toString().isEmpty()) {
                    sampleTextDialog!!.text = "Type Something..."
                }
            }
        })
    }

    private fun setFont() {
        val builder = AlertDialog.Builder(this@TextInputDialog)
        builder.setTitle("Select Font").setItems(R.array.fonts) { dialog, which ->
            fontName = resources.getStringArray(R.array.fonts)[which]
            sampleTextDialog!!.typeface = Typeface.createFromAsset(assets, "$fontName.ttf")
        }.create().show()
    }

    private val colour: Unit
        private get() {
            ColorPickerDialogBuilder.with(this@TextInputDialog).setTitle("Choose Color")
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER).density(12)
                .setOnColorSelectedListener(
                    OnColorSelectedListener { })
                .setPositiveButton("ok", object : ColorPickerClickListener {
                    override fun onClick(
                        dialog: DialogInterface,
                        selectedColor: Int,
                        allColors: Array<Int>
                    ) {
                        mColor = selectedColor
                        sampleTextDialog!!.setTextColor(selectedColor)
                    }
                }).setNegativeButton("cancel", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface, which: Int) {}
            }).build().show()
        }
}