package com.tekskills.sampleapp.ui.articleDetails

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.commit451.nativestackblur.NativeStackBlur
import com.google.android.material.transition.MaterialFadeThrough
import com.tekskills.sampleapp.R
import com.tekskills.sampleapp.data.local.BannerItemRepository
import com.tekskills.sampleapp.data.local.BannersDatabase
import com.tekskills.sampleapp.data.local.BookmarksAllNews
import com.tekskills.sampleapp.data.local.BookmarksDatabase
import com.tekskills.sampleapp.data.local.BookmarksRepository
import com.tekskills.sampleapp.data.prefrences.AppPreferences
import com.tekskills.sampleapp.databinding.ActivityPosterEditorBinding
import com.tekskills.sampleapp.model.AllNewsItem
import com.tekskills.sampleapp.model.PosterItemDetails
import com.tekskills.sampleapp.ui.adapter.PostersAdapter
import com.tekskills.sampleapp.ui.main.MainActivity
import com.tekskills.sampleapp.ui.main.MainViewModel
import com.tekskills.sampleapp.ui.main.MainViewModelFactory
import com.tekskills.sampleapp.ui.poster.collegeview.MultiTouchListener
import com.tekskills.sampleapp.ui.poster.costumDialog.TextInputDialog
import com.tekskills.sampleapp.ui.poster.helper.PipFilters
import com.tekskills.sampleapp.ui.poster.sticker.BubbleTextView
import com.tekskills.sampleapp.ui.poster.sticker.StickerView
import com.tekskills.sampleapp.utils.ShareLayout
import com.tekskills.sampleapp.utils.like.LikeButton
import com.tekskills.sampleapp.utils.like.OnAnimationEndListener
import com.tekskills.sampleapp.utils.like.OnLikeListener
import com.theartofdev.edmodo.cropper.CropImage
import java.io.IOException
import java.util.concurrent.Executors

class PosterEditorFragment : Fragment() {

    val TEXT_FONT_COLOR = 856
    private var mViews: ArrayList<View>? = null
    var mCurrentView: StickerView? = null
    private var mCurrentEditTextView: BubbleTextView? = null
    private var editBubbleView: BubbleTextView? = null
    private var mCropImageUri: Uri? = null
    var defualtBackImage: Bitmap? = null
    var preCurrentBackgroundImage: Bitmap? = null
    var preCurrentForegroundImage: Bitmap? = null
    var postCurrentBackgroundImage: Bitmap? = null
    var postCurrentForegroundImage: Bitmap? = null
    var posterItem: PosterItemDetails? =null

    var recyclerAdapter: PostersAdapter? = null
    var whichImage = 0
    var whichBackgroundImageEffect = 0
    private var currentMaskImage: String = ""
    private var clPosterView: View? = null

    lateinit var viewModel: MainViewModel
    lateinit var binding: ActivityPosterEditorBinding
    private lateinit var preferences: AppPreferences

    var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.activity_poster_editor, container, false)

        preferences = AppPreferences(requireContext())

        val database: BookmarksDatabase = BookmarksDatabase.getInstance(context = requireContext())
        val bannerDatabase: BannersDatabase = BannersDatabase.getInstance(context = requireContext())

        val dao = database.dao
        val bannerDao = bannerDatabase.bannerDao
        val repository = BookmarksRepository(dao)
        val bannerRepo = BannerItemRepository(bannerDao)
        val factory = MainViewModelFactory(repository,bannerRepo,preferences)
        viewModel = ViewModelProvider(requireActivity(), factory)[MainViewModel::class.java]

        binding.viewModel = viewModel

        initObservers()
        listenerInitializer()

        enterTransition = MaterialFadeThrough()
        mViews = ArrayList()
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    fun initObservers() {
        setRecyclerAdapter()
        viewModel.responseEditorLiveData.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                it.body()?.let { articles ->
                    isLoading = true
                    recyclerAdapter?.setEditorList(articles)
                    isLoading = true
                }
            }
        })

        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            if (!isLoading) {
                if (binding.swipeRefreshLayoutEditor.isRefreshing) binding.swipeRefreshLayoutEditor.isRefreshing =
                    false

                binding.editorPager.visibility = VISIBLE

                val context = binding.editorPager.context
                val layoutAnimationController =
                    AnimationUtils.loadLayoutAnimation(context, R.anim.layout_down_to_up)
                binding.editorPager.layoutAnimation = layoutAnimationController
            } else {
                if (!binding.swipeRefreshLayoutEditor.isRefreshing) binding.swipeRefreshLayoutEditor.isRefreshing =
                    true
                binding.editorPager.visibility = View.INVISIBLE
            }
        })

        binding.swipeRefreshLayoutEditor.setOnRefreshListener {
            viewModel.refreshResponse()
        }
    }

    private fun viewInitializer(imagePath: String) {
        val bm: Bitmap? = uriToBitmap(Uri.parse(imagePath))
        defualtBackImage = bm
        preCurrentBackgroundImage = bm
        preCurrentForegroundImage = bm
        binding.editorForegroundImage.setImageBitmap(preCurrentForegroundImage)
    }

    private fun setRecyclerAdapter() {
        recyclerAdapter =
            PostersAdapter(object : PostersAdapter.OnItemClickListener {
                override fun onItemClick(itemView: View, item: PosterItemDetails) {
                    (activity as MainActivity?)!!.appBarLayoutHandle(true)
//                    makeMaskImage(
//                        item.backgroundImagePath,
//                        item.posterImagePath
//                    )
                }

                override fun onUploadClick(itemView: View, item: PosterItemDetails) {
                    viewInitializer(item.backgroundImagePath)
                    clPosterView = itemView
                    makeMaskImage(
                        item.backgroundImagePath,
                        item.posterImagePath
                    )
                    makeBuilder()
                }
            })

        binding.editorPager.orientation = ViewPager2.ORIENTATION_VERTICAL
        binding.editorPager.registerOnPageChangeCallback(onPageChangeCallback)
        binding.editorPager.adapter = recyclerAdapter
        binding.editorPager.setPageTransformer(ArticlesFragment.CardTransformer(1.2f))
    }

    private val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)
            Log.d("Event", "action response page scroll ${state}")
            when (state) {
                ViewPager2.SCROLL_STATE_DRAGGING -> {

                }

                ViewPager2.SCROLL_STATE_IDLE -> {
                    (activity as MainActivity?)!!.appBarLayoutHandle(false)
                }

                ViewPager2.SCROLL_STATE_SETTLING -> {
                    // Pager is automatically settling to the current page.
                }
            }
        }
    }

    private fun listenerInitializer() {
        val BANNER_SAMPLE =
            "https://news.maaproperties.com/assets/img/ads-img/Maproperty_Banner.gif"
        displayImage(BANNER_SAMPLE, binding.ivBannerShare)

        binding.ivBannerShare.visibility = View.GONE
        binding.ivBannerLogo.visibility = View.GONE
        binding.editorForegroundImage!!.setOnTouchListener(MultiTouchListener(this@PosterEditorFragment))

        binding.ivCancel.setOnClickListener {
            parentFragmentManager.beginTransaction().detach(this).commit()
            parentFragmentManager.beginTransaction().attach(this).commit()
            visibleEditor()
        }

        binding.heartButton.setOnLikeListener(object: OnLikeListener {
            override fun liked(likeButton: LikeButton?) {
//                    Toast.makeText(activity, "Liked!", Toast.LENGTH_SHORT).show();
            }

            override fun unLiked(likeButton: LikeButton?) {
//                    Toast.makeText(activity, "unLiked!", Toast.LENGTH_SHORT).show();
            }
        })

        binding.heartButton.setOnAnimationEndListener(object: OnAnimationEndListener {
            override fun onAnimationEnd(likeButton: LikeButton?) {
                if(posterItem != null)
                updateViewCount(posterItem!!.posterId)
            }
        })

        binding.ivShare.setOnClickListener {
            binding.apply {
                editorPager.visibility = View.GONE
                ivBannerShare.visibility = View.VISIBLE
                ivBannerLogo.visibility = View.VISIBLE
                object : CountDownTimer(200, 100) {
                    override fun onFinish() {
                        val a = false
                        val b = false

                        if (mCurrentView != null) {
                            mCurrentView!!.setInEdit(false)
                        }
                        if (mCurrentEditTextView != null) {
                            mCurrentEditTextView!!.setInEdit(false)
                        }
                        if (clPosterView != null) {
                            val activityOptions =
                                ActivityOptionsCompat.makeSceneTransitionAnimation(
                                    requireActivity(),
                                    editorPager!!,
                                    "article_image"
                                )
                            ShareLayout.simpleLayoutShare(
                                requireActivity(),
                                editorViewToSave,
                                " ",
                                activityOptions
                            )

                            ivBannerShare.visibility = View.GONE
                            ivBannerLogo.visibility = View.GONE
                            visibleEditor()
                        }
                    }

                    override fun onTick(millisUntilFinished: Long) {}
                }.start()

            }
        }

        binding.editorText.setOnClickListener {
            addBubble()
        }

        binding.editorViewToSave.setOnClickListener {
            hideControl()
        }
    }

    fun updateViewCount(articleId: Int) {

        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            // Update the view count in the Room database
            val database: BookmarksDatabase = BookmarksDatabase.getInstance(requireContext())
            val dao = database.dao
            val existingBookmark = dao.getBookmarksById(articleId)
            if (existingBookmark != null) {
                // Update the existing article
                var count = existingBookmark.view_count + 1
                dao.incrementViewCount(existingBookmark.news_id, count)
            } else {
                val bookMark = BookmarksAllNews(news_id = articleId, view_count = 1)
                dao.insertBookmarks(bookMark)
            }

            // Fetch the updated data from the database
            requireActivity().runOnUiThread(Runnable {
                val updatedData = dao.getBookmarksById(articleId)
                binding.apply {
                    likes.text = updatedData?.view_count.toString()
                }
            })
//            database.close()
        }

    }


    fun displayImage(videoUrl: String?, itemView: ImageView?) {
        Glide.with(requireContext())
            .asBitmap()
            .load(videoUrl)
            .error(R.drawable.place_holder)
            .into(itemView!!)
    }

    private fun applyEffectOnBack(whichFilter: Int) {
        postCurrentBackgroundImage =
            PipFilters().applyFilter(whichFilter, preCurrentBackgroundImage)
        if (postCurrentForegroundImage != null) {
            backgroundFilter()
        } else {
            changeBackgroundImage()
        }
    }

    private fun makeBuilder() {
        whichImage = 1
        CropImage.startPickImageActivity(requireActivity())
    }

    private fun addBubble() {
        val bubbleTextView = BubbleTextView(requireContext(), Color.BLACK, 0)
        bubbleTextView.setImageResource(R.mipmap.bubble_rb)
        bubbleTextView.setOperationListener(object : BubbleTextView.OperationListener {
            override fun onDeleteClick() {
                mViews!!.remove(bubbleTextView)
                binding.editorViewToSave!!.removeView(bubbleTextView)
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
                    Intent(activity, TextInputDialog::class.java),
                    TEXT_FONT_COLOR
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
        binding.editorViewToSave!!.addView(bubbleTextView, lp)
        mViews!!.add(bubbleTextView)
        bubbleTextView?.let {
            setCurrentEdit(bubbleTextView)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // handle result of pick image chooser
        super.onActivityResult(requestCode, resultCode, data)
//        val scanResult: IntentResult =
//            IntentIntegrator.parseActivityResult(requestCode, resultCode, intent)

        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK) {
            val imageUri = CropImage.getPickImageResultUri(requireContext(), data)

            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(requireContext(), imageUri)) {
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
                typeface = Typeface.createFromAsset(requireContext().assets, "$fontName.ttf")
            }
            editBubbleView!!.setText(text!!, color, typeface)
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                visibleEditor()
                mCropImageUri = result.uri
                if (whichImage == 0) {
                    setBackgroundImage()
                } else if (whichImage == 1) {
                    setForegroundImage()
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }

    private fun visibleEditor() {
        if (binding.swipeRefreshLayoutEditor.isVisible) {
            binding.swipeRefreshLayoutEditor.visibility = GONE
            binding.rlEditor.visibility = VISIBLE
        } else {
            binding.swipeRefreshLayoutEditor.visibility = VISIBLE
            binding.rlEditor.visibility = GONE
        }
    }

    private fun setBackgroundImage() {
        defualtBackImage = null
        defualtBackImage = uriToBitmap(mCropImageUri)
        changeBackgroundImage()
    }

    private fun setForegroundImage() {
        postCurrentForegroundImage = null
        preCurrentForegroundImage = uriToBitmap(mCropImageUri)
        binding.editorForegroundImage!!.setImageBitmap(preCurrentForegroundImage)
    }

    private fun startCropImageActivity(imageUri: Uri) {
        CropImage.activity(imageUri).start(requireActivity())
    }

    fun makeMaskImage(maskimage: String, frameimage: String) {
        currentMaskImage = maskimage
        Glide.with(requireContext())
            .asBitmap()
            .load(frameimage)
            .error(R.drawable.place_holder)
            .into(binding.editorFrameImage)
        try {
            val mask: Bitmap = Glide
                .with(requireContext())
                .asBitmap()
                .load(maskimage)
                .submit()
                .get()

            val original = resizeImage(mask.width, mask.height)
            val result = Bitmap.createBitmap(mask.width, mask.height, Bitmap.Config.ARGB_8888)
            val mCanvas = Canvas(result)
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
            mCanvas.drawBitmap(original, 0f, 0f, null)
            mCanvas.drawBitmap(mask, 0f, 0f, paint)
            paint.xfermode = null
            binding.editorMaskImage!!.setImageBitmap(NativeStackBlur.process(result, 25))
            preCurrentBackgroundImage = result
            if (whichBackgroundImageEffect != 0) {
                applyEffectOnBack(whichBackgroundImageEffect)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun changeBackgroundImage() {
        postCurrentBackgroundImage = null
        whichBackgroundImageEffect = 0

        try {
            val mask: Bitmap = Glide
                .with(requireContext())
                .asBitmap()
                .load(currentMaskImage)
                .submit()
                .get()

            val original = resizeImage(mask.width, mask.height)
            val result = Bitmap.createBitmap(mask.width, mask.height, Bitmap.Config.ARGB_8888)
            val mCanvas = Canvas(result)
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
            mCanvas.drawBitmap(original, 0f, 0f, null)
            mCanvas.drawBitmap(mask, 0f, 0f, paint)
            paint.xfermode = null
            binding.editorMaskImage!!.setImageBitmap(NativeStackBlur.process(result, 25))
            preCurrentBackgroundImage = result
            if (postCurrentForegroundImage != null) {
                binding.editorForegroundImage!!.setImageBitmap(postCurrentForegroundImage)
            } else {
                binding.editorForegroundImage!!.setImageBitmap(preCurrentForegroundImage)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
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

    fun backgroundFilter() {
        try {
            val mask: Bitmap = Glide
                .with(requireContext())
                .asBitmap()
                .load(currentMaskImage)
                .submit()
                .get()

            val original = resizePostImage(mask.width, mask.height)
            val result = Bitmap.createBitmap(mask.width, mask.height, Bitmap.Config.ARGB_8888)
            val mCanvas = Canvas(result)
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
            mCanvas.drawBitmap(original, 0f, 0f, null)
            mCanvas.drawBitmap(mask, 0f, 0f, paint)
            paint.xfermode = null
            binding.editorMaskImage!!.setImageBitmap(NativeStackBlur.process(result, 25))
            postCurrentBackgroundImage = result
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun resizePostImage(h: Int, w: Int): Bitmap {
        val scaleWidth: Float
        val scaleHeight: Float
        val width = postCurrentBackgroundImage!!.width
        val height = postCurrentBackgroundImage!!.height
        if (width < height) {
            scaleWidth = w.toFloat() / width.toFloat()
            scaleHeight = scaleWidth
        } else {
            scaleHeight = h.toFloat() / height.toFloat()
            scaleWidth = scaleHeight
        }
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)
        postCurrentBackgroundImage =
            Bitmap.createBitmap(
                postCurrentBackgroundImage!!,
                0,
                0,
                width,
                height,
                matrix,
                false
            )
        return postCurrentBackgroundImage as Bitmap
    }

    private fun uriToBitmap(uri: Uri?): Bitmap? {
        try {
            return MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
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

}