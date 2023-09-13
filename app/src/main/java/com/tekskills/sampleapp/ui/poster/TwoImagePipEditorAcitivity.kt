package com.tekskills.sampleapp.ui.poster

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Typeface
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tekskills.sampleapp.R
import com.tekskills.sampleapp.ui.poster.collegeview.CollageView
import com.tekskills.sampleapp.ui.poster.collegeview.MultiTouchListener
import com.tekskills.sampleapp.ui.poster.costumDialog.TextInputDialog
import com.tekskills.sampleapp.ui.poster.helper.PipFilters
import com.tekskills.sampleapp.ui.poster.helper.PipList
import com.tekskills.sampleapp.ui.poster.recyleradapter.EditorAdapter
import com.tekskills.sampleapp.ui.poster.recyleradapter.EditorAdapterString
import com.tekskills.sampleapp.ui.poster.recyleradapter.EditorCollection
import com.tekskills.sampleapp.ui.poster.saveimage.SaveImage
import com.tekskills.sampleapp.ui.poster.sticker.BubbleTextView
import com.tekskills.sampleapp.ui.poster.sticker.StickerView
import com.commit451.nativestackblur.NativeStackBlur
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.theartofdev.edmodo.cropper.CropImage
import java.io.IOException
import java.io.InputStream
import java.util.Arrays

class TwoImagePipEditorAcitivity : AppCompatActivity() {
    val TEXT_FONT_COLOR = 856
    val PIP = 0
    val FILTERS = 1
    private var mViews: ArrayList<View>? = null
    var mCurrentView: StickerView? = null
    private var mCurrentEditTextView: BubbleTextView? = null
    private var editBubbleView: BubbleTextView? = null
    var editorBlurIntensitySeekBar: SeekBar? = null
    var editorPip: Button? = null
    var editorReplace: Button? = null
    var editorFilter: Button? = null
    var editorSticker: Button? = null
    var editorText: Button? = null
    var editorHideSeekBarContainer: Button? = null
    var editorMaskImage: ImageView? = null
    var editorFrameImage: ImageView? = null
    var editorForegroundImageLeft: CollageView? = null
    var editorForegroundImageRight: CollageView? = null
    var editorSelectorForEditingImages: View? = null
    var editorContainerForSeekBar: View? = null
    var editorViewToSave: RelativeLayout? = null
    private var mCropImageUri: Uri? = null
    var defualtBackImage: Bitmap? = null
    var preCurrentBackgroundImage: Bitmap? = null
    var preCurrentForegroundImageLeft: Bitmap? = null
    var preCurrentForegroundImageRight: Bitmap? = null
    var postCurrentForegroundImageLeft: Bitmap? = null
    var postCurrentForegroundImageRight: Bitmap? = null
    var editorRecycler: RecyclerView? = null
    var recyclerManager: RecyclerView.LayoutManager? = null
    var recyclerAdapter: EditorAdapter? = null
    var recyclerAdapterString: EditorAdapterString? = null
    var currentFrameList: ArrayList<EditorCollection>? = null
    var currentMaskList: ArrayList<EditorCollection>? = null
    var filtersCollection: ArrayList<EditorCollection>? = null
    var stickersList: ArrayList<String?>? = null
    var imageToApplyEffect = 0
    var whichImage = 0
    private var currentMaskImage = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_two_image_pip_editor_acitivity)

//        MobileAds.initialize(this, getString(R.string.app_id));
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        componentInitializer()
        listenerInitializer()
        viewInitializer()
    }

    private fun componentInitializer() {
        editorMaskImage = findViewById(R.id.editor_mask_image)
        editorForegroundImageLeft = findViewById(R.id.editor_foreground_image_left)
        editorForegroundImageRight = findViewById(R.id.editor_foreground_image_right)
        editorFrameImage = findViewById(R.id.editor_frame_image)
        editorBlurIntensitySeekBar = findViewById(R.id.editor_blur_intensity_seekbar)
        editorPip = findViewById(R.id.editor_pip)
        editorReplace = findViewById(R.id.editor_replace)
        editorFilter = findViewById(R.id.editor_filters)
        editorSticker = findViewById(R.id.editor_sticker)
        editorText = findViewById(R.id.editor_text)
        editorRecycler = findViewById(R.id.editor_recycler_view)
        editorSelectorForEditingImages = findViewById(R.id.editor_selector_for_editing_images)
        editorContainerForSeekBar = findViewById(R.id.editor_container_for_seek_bar)
        editorViewToSave = findViewById(R.id.editor_view_to_save)
        editorHideSeekBarContainer = findViewById(R.id.editor_hide_seekbar_container)
        currentFrameList = ArrayList()
        currentMaskList = ArrayList()
        currentFrameList = PipList().twoImagePipFrameList
        currentMaskList = PipList().twoImagePipMaskList
        filtersCollection = ArrayList()
        filtersCollection = PipList().filtersList
        mViews = ArrayList()
        try {
            val stickerImagesFromAssets = assets.list("stickers")
            stickersList = ArrayList(Arrays.asList(*stickerImagesFromAssets))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun listenerInitializer() {
        editorForegroundImageLeft!!.setOnTouchListener(MultiTouchListener(this))
        editorForegroundImageRight!!.setOnTouchListener(MultiTouchListener(this))
        editorReplace!!.setOnClickListener { makeBuilder() }
        editorFilter!!.setOnClickListener {
            YoYo.with(Techniques.FadeIn).duration(500)
                .playOn(findViewById(R.id.editor_selector_for_editing_images))
            editorSelectorForEditingImages!!.visibility = View.VISIBLE
            setRecyclerAdapter(filtersCollection, FILTERS)
        }
        editorPip!!.setOnClickListener {
            YoYo.with(Techniques.FadeOut).duration(300)
                .onEnd { editorSelectorForEditingImages!!.visibility = View.GONE }
                .playOn(findViewById(R.id.editor_selector_for_editing_images))
            setRecyclerAdapter(PipList().twoImagePipThumbnails, PIP)
        }
        editorSticker!!.setOnClickListener {
            YoYo.with(Techniques.FadeOut).duration(300)
                .onEnd { editorSelectorForEditingImages!!.visibility = View.GONE }
                .playOn(findViewById(R.id.editor_selector_for_editing_images))
            setRecyclerAdapter1()
        }
        editorText!!.setOnClickListener { addBubble() }
        editorViewToSave!!.setOnClickListener { hideControl() }
        editorBlurIntensitySeekBar!!.progress = 25
        editorBlurIntensitySeekBar!!.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                editorMaskImage!!.setImageBitmap(
                    NativeStackBlur.process(
                        preCurrentBackgroundImage,
                        seekBar.progress
                    )
                )
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        editorHideSeekBarContainer!!.setOnClickListener {
            editorContainerForSeekBar!!.visibility = View.GONE
        }
    }

    private fun viewInitializer() {
        val imageList = intent.getParcelableArrayListExtra<Uri>("result")
        var uri = imageList!![0]
        var bm = uriToBitmap(uri)
        defualtBackImage = bm
        preCurrentBackgroundImage = bm
        preCurrentForegroundImageLeft = bm
        uri = imageList[1]
        bm = uriToBitmap(uri)
        preCurrentForegroundImageRight = bm
        editorForegroundImageLeft!!.setImageBitmap(preCurrentForegroundImageLeft)
        editorForegroundImageRight!!.setImageBitmap(preCurrentForegroundImageRight)
        makeMaskImage(currentMaskList!![4].imageID, currentFrameList!![4].imageID)
        editorRecycler!!.setHasFixedSize(true)
        recyclerManager =
            LinearLayoutManager(this@TwoImagePipEditorAcitivity, RecyclerView.HORIZONTAL, false)
        editorRecycler!!.layoutManager = recyclerManager
        setRecyclerAdapter(PipList().twoImagePipThumbnails, PIP)
    }

    private fun setRecyclerAdapter(recyclerList: ArrayList<EditorCollection>?, whichList: Int) {
        recyclerAdapter = EditorAdapter(recyclerList!!, object : EditorAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                when (whichList) {
                    0 -> makeMaskImage(
                        currentMaskList!![position].imageID,
                        currentFrameList!![position].imageID
                    )

                    1 -> FilterAsyncTask().execute(position)
                    3 -> {}
                }
            }

            override fun onUploadClick(position: Int) {

            }
        })
        editorRecycler!!.adapter = recyclerAdapter
    }

    private fun setRecyclerAdapter1() {
        recyclerAdapterString =
            EditorAdapterString(
                this@TwoImagePipEditorAcitivity,
                stickersList,
                object : EditorAdapterString.OnItemClickListener {
                    override fun onItemClick(position: Int) {
                        var inputstream: InputStream? = null
                        try {
                            inputstream =
                                applicationContext.assets.open("stickers/" + stickersList!![position])
                            val sticker = BitmapFactory.decodeStream(inputstream)
                            addStickerView(sticker)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }

                })
        editorRecycler!!.adapter = recyclerAdapterString
    }

    private fun makeBuilder() {
        val array = arrayOf("Background", "Left", "Right", "Cencel")
        val builder = AlertDialog.Builder(this@TwoImagePipEditorAcitivity)
        builder.setTitle("Select Image").setItems(array) { dialog, which ->
            when (which) {
                0, 1, 2 -> {
                    whichImage = which
                    CropImage.startPickImageActivity(this@TwoImagePipEditorAcitivity)
                }

                else -> {}
            }
        }.setCancelable(true).create().show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.editor_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.editor_menu_save -> {
                val a = false
                val b = false
                if (editorSelectorForEditingImages!!.visibility == View.VISIBLE) {
                    editorSelectorForEditingImages!!.visibility = View.GONE
                    editorContainerForSeekBar!!.visibility = View.GONE
                    if (mCurrentView != null) {
                        mCurrentView!!.setInEdit(false)
                    }
                    if (mCurrentEditTextView != null) {
                        mCurrentEditTextView!!.setInEdit(false)
                    }
                    if (SaveImage().save(this@TwoImagePipEditorAcitivity, editorViewToSave!!)) {
                        Toast.makeText(this@TwoImagePipEditorAcitivity, "Saved", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(this@TwoImagePipEditorAcitivity, "Error", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    editorContainerForSeekBar!!.visibility = View.GONE
                    if (mCurrentView != null) {
                        mCurrentView!!.setInEdit(false)
                    }
                    if (mCurrentEditTextView != null) {
                        mCurrentEditTextView!!.setInEdit(false)
                    }
                    if (SaveImage().save(this@TwoImagePipEditorAcitivity, editorViewToSave!!)) {
                        Toast.makeText(this@TwoImagePipEditorAcitivity, "Saved", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(this@TwoImagePipEditorAcitivity, "Error", Toast.LENGTH_SHORT)
                            .show()
                    }
                    startActivity(Intent(this@TwoImagePipEditorAcitivity, PIPGallery::class.java))
                }
                true
            }

            R.id.editor_menu_control_blur_intensity -> {
                editorContainerForSeekBar!!.visibility = View.VISIBLE
                true
            }

            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun addStickerView(sticker: Bitmap) {
        val stickerView = StickerView(this)
        stickerView.setBitmap(sticker)
        stickerView.setOperationListener(object : StickerView.OperationListener {
            override fun onDeleteClick() {
                mViews!!.remove(stickerView)
                editorViewToSave!!.removeView(stickerView)
            }

            override fun onEdit(stickerView: StickerView?) {
                if (mCurrentEditTextView != null) {
                    mCurrentEditTextView!!.setInEdit(false)
                }
                mCurrentView!!.setInEdit(false)
                mCurrentView = stickerView
                mCurrentView!!.setInEdit(true)
            }

            override fun onTop(stickerView: StickerView?) {
                val position = mViews!!.indexOf(stickerView!!)
                if (position == mViews!!.size - 1) {
                    return
                }
                val stickerTemp = mViews!!.removeAt(position) as StickerView
                mViews!!.add(mViews!!.size, stickerTemp)
            }
        })
        val lp = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        )
        editorViewToSave!!.addView(stickerView, lp)
        mViews!!.add(stickerView)
        setCurrentEdit(stickerView)
    }

    private fun addBubble() {
        val bubbleTextView = BubbleTextView(this, Color.BLACK, 0)
        bubbleTextView.setImageResource(R.mipmap.bubble_rb)
        bubbleTextView.setOperationListener(object : BubbleTextView.OperationListener {
            override fun onDeleteClick() {
                mViews!!.remove(bubbleTextView)
                editorViewToSave!!.removeView(bubbleTextView)
            }

            override fun onEdit(bubbleTextView: BubbleTextView?) {
                if (mCurrentView != null) {
                    mCurrentView!!.setInEdit(false)
                }
                mCurrentEditTextView!!.setInEdit(false)
                mCurrentEditTextView = bubbleTextView
                mCurrentEditTextView!!.setInEdit(true)
            }

            override fun onClick(bubbleTextView: BubbleTextView?) {
                editBubbleView = bubbleTextView
                startActivityForResult(
                    Intent(
                        this@TwoImagePipEditorAcitivity,
                        TextInputDialog::class.java
                    ), TEXT_FONT_COLOR
                )
            }

            override fun onTop(bubbleTextView: BubbleTextView?) {
                val position = mViews!!.indexOf(bubbleTextView!!)
                if (position == mViews!!.size - 1) {
                    return
                }
                val textView = mViews!!.removeAt(position) as BubbleTextView
                mViews!!.add(mViews!!.size, textView)
            }
        })
        val lp = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        )
        editorViewToSave!!.addView(bubbleTextView, lp)
        mViews!!.add(bubbleTextView)
        setCurrentEdit(bubbleTextView)
    }

    @SuppressLint("NewApi")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // handle result of pick image chooser
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK) {
            val imageUri = CropImage.getPickImageResultUri(this, data)

            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                mCropImageUri = imageUri
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE
                )
            } else {
                // no permissions required or already granted, can start crop image activity
                startCropImageActivity(imageUri)
            }
        }
        if (requestCode == TEXT_FONT_COLOR && resultCode == RESULT_OK && data != null) {
            val text = data.getStringExtra("text")
            val fontName = data.getStringExtra("font")
            val color = data.getIntExtra("color", 0)
            var typeface: Typeface? = null
            if (fontName != null) {
                typeface = Typeface.createFromAsset(assets, "$fontName.ttf")
            }
            editBubbleView!!.setText(text!!, color, typeface)
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                mCropImageUri = result.uri
                if (whichImage == 0) {
                    setBackgroundImage()
                } else if (whichImage == 1) {
                    setLeftImage()
                } else if (whichImage == 2) {
                    setRightImage()
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }

    private fun setBackgroundImage() {
        defualtBackImage = null
        defualtBackImage = uriToBitmapRunning(mCropImageUri)
        changeBackgroundImage()
        editorContainerForSeekBar!!.visibility = View.VISIBLE
    }

    private fun setLeftImage() {
        editorContainerForSeekBar!!.visibility = View.GONE
        postCurrentForegroundImageLeft = null
        preCurrentForegroundImageLeft = uriToBitmapRunning(mCropImageUri)
        editorForegroundImageLeft!!.setImageBitmap(preCurrentForegroundImageLeft)
    }

    private fun setRightImage() {
        editorContainerForSeekBar!!.visibility = View.GONE
        postCurrentForegroundImageRight = null
        preCurrentForegroundImageRight = uriToBitmapRunning(mCropImageUri)
        editorForegroundImageRight!!.setImageBitmap(preCurrentForegroundImageRight)
    }

    private fun startCropImageActivity(imageUri: Uri) {
        CropImage.activity(imageUri).start(this)
    }

    fun setImageToApplyEffect(view: View) {
        val checked = (view as RadioButton).isChecked
        when (view.getId()) {
            R.id.editor_bothimages -> if (checked) {
                imageToApplyEffect = 0
            }

            R.id.editor_foregroundimageRightonly -> if (checked) {
                imageToApplyEffect = 2
            }

            R.id.editor_foregroundimageLeftonly -> if (checked) {
                imageToApplyEffect = 1
            }

            else -> if (checked) {
                imageToApplyEffect = 0
            }
        }
    }

    fun makeMaskImage(maskimage: Int, frameimage: Int) {
        currentMaskImage = maskimage
        editorFrameImage!!.setBackgroundResource(frameimage)
        val mask = BitmapFactory.decodeResource(resources, maskimage)
        val original = resizeImage(mask.width, mask.height)
        val result = Bitmap.createBitmap(mask.width, mask.height, Bitmap.Config.ARGB_8888)
        val mCanvas = Canvas(result)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        mCanvas.drawBitmap(original, 0f, 0f, null)
        mCanvas.drawBitmap(mask, 0f, 0f, paint)
        paint.xfermode = null
        editorBlurIntensitySeekBar!!.progress = 25
        editorMaskImage!!.setImageBitmap(NativeStackBlur.process(result, 25))
        preCurrentBackgroundImage = result
    }

    fun changeBackgroundImage() {
        val mask = BitmapFactory.decodeResource(resources, currentMaskImage)
        val original = resizeImage(mask.width, mask.height)
        val result = Bitmap.createBitmap(mask.width, mask.height, Bitmap.Config.ARGB_8888)
        val mCanvas = Canvas(result)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        mCanvas.drawBitmap(original, 0f, 0f, null)
        mCanvas.drawBitmap(mask, 0f, 0f, paint)
        paint.xfermode = null
        editorBlurIntensitySeekBar!!.progress = 25
        editorMaskImage!!.setImageBitmap(NativeStackBlur.process(result, 25))
        preCurrentBackgroundImage = result
        if (postCurrentForegroundImageLeft != null) {
            editorForegroundImageLeft!!.setImageBitmap(postCurrentForegroundImageLeft)
        } else {
            editorForegroundImageLeft!!.setImageBitmap(preCurrentForegroundImageLeft)
        }
        if (postCurrentForegroundImageRight != null) {
            editorForegroundImageRight!!.setImageBitmap(postCurrentForegroundImageRight)
        } else {
            editorForegroundImageRight!!.setImageBitmap(preCurrentForegroundImageRight)
        }
    }

    fun resizeImage(h: Int, w: Int): Bitmap {
        val scaleWidth: Float
        val scaleHeight: Float
        val width = defualtBackImage!!.width
        val height = defualtBackImage!!.height
        if (width < height) {
            scaleWidth = w.toFloat() / width.toFloat()
            scaleHeight = scaleWidth
        } else {
            scaleHeight = h.toFloat() / height.toFloat()
            scaleWidth = scaleHeight
        }
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)
        preCurrentBackgroundImage =
            Bitmap.createBitmap(defualtBackImage!!, 0, 0, width, height, matrix, false)
        return preCurrentBackgroundImage!!
    }

    private fun uriToBitmap(uri: Uri): Bitmap {
        return BitmapFactory.decodeFile(uri.toString())
    }

    private fun uriToBitmapRunning(uri: Uri?): Bitmap? {
        try {
            return MediaStore.Images.Media.getBitmap(contentResolver, uri)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun setCurrentEdit(stickerView: StickerView) {
        if (mCurrentView != null) {
            mCurrentView!!.setInEdit(false)
        }
        if (mCurrentEditTextView != null) {
            mCurrentEditTextView!!.setInEdit(false)
        }
        mCurrentView = stickerView
        stickerView.setInEdit(true)
    }

    private fun setCurrentEdit(bubbleTextView: BubbleTextView) {
        if (mCurrentView != null) {
            mCurrentView!!.setInEdit(false)
        }
        if (mCurrentEditTextView != null) {
            mCurrentEditTextView!!.setInEdit(false)
        }
        mCurrentEditTextView = bubbleTextView
        mCurrentEditTextView!!.setInEdit(true)
    }

    fun hideControl() {
        if (mCurrentView != null) {
            mCurrentView!!.setInEdit(false)
        }
        if (mCurrentEditTextView != null) {
            mCurrentEditTextView!!.setInEdit(false)
        }
    }

    private inner class FilterAsyncTask : AsyncTask<Int?, Void?, Bitmap?>() {
        private var dialog: ProgressDialog? = null
        override fun onPreExecute() {
            dialog = ProgressDialog(this@TwoImagePipEditorAcitivity)
            dialog!!.setMessage("Applying..")
            dialog!!.show()
        }

        override fun onPostExecute(bitmap: Bitmap?) {
            when (imageToApplyEffect) {
                0 -> {
                    if (postCurrentForegroundImageLeft != null) {
                        editorForegroundImageLeft!!.setImageBitmap(postCurrentForegroundImageLeft)
                    } else {
                        editorForegroundImageLeft!!.setImageBitmap(preCurrentForegroundImageLeft)
                    }
                    if (postCurrentForegroundImageRight != null) {
                        editorForegroundImageRight!!.setImageBitmap(postCurrentForegroundImageRight)
                    } else {
                        editorForegroundImageRight!!.setImageBitmap(preCurrentForegroundImageRight)
                    }
                }

                1 -> if (postCurrentForegroundImageLeft != null) {
                    editorForegroundImageLeft!!.setImageBitmap(postCurrentForegroundImageLeft)
                } else {
                    editorForegroundImageLeft!!.setImageBitmap(preCurrentForegroundImageLeft)
                }

                2 -> if (postCurrentForegroundImageRight != null) {
                    editorForegroundImageRight!!.setImageBitmap(postCurrentForegroundImageRight)
                } else {
                    editorForegroundImageRight!!.setImageBitmap(preCurrentForegroundImageRight)
                }
            }
            if (dialog!!.isShowing) {
                dialog!!.dismiss()
            }
        }

        override fun doInBackground(vararg integers: Int?): Bitmap {
            applyEffect(integers[0]!!)
            return null!!
        }
    }

    private fun applyEffect(whichFilter: Int) {
        when (imageToApplyEffect) {
            0 -> applyEffectOnBoth(whichFilter)
            1 -> applyEffectOnLeftFront(whichFilter)
            2 -> applyEffectOnRightFront(whichFilter)
        }
    }

    private fun applyEffectOnBoth(whichFilter: Int) {
        postCurrentForegroundImageLeft =
            PipFilters().applyFilter(whichFilter, preCurrentForegroundImageLeft)
        postCurrentForegroundImageRight =
            PipFilters().applyFilter(whichFilter, preCurrentForegroundImageRight)
    }

    private fun applyEffectOnLeftFront(whichFilter: Int) {
        postCurrentForegroundImageLeft =
            PipFilters().applyFilter(whichFilter, preCurrentForegroundImageLeft)
    }

    private fun applyEffectOnRightFront(whichFilter: Int) {
        postCurrentForegroundImageRight =
            PipFilters().applyFilter(whichFilter, preCurrentForegroundImageRight)
    }
}