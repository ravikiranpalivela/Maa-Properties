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
import com.tekskills.sampleapp.data.local.ArticlesAllNews
import com.tekskills.sampleapp.data.local.ArticlesDatabase
import com.tekskills.sampleapp.data.local.ArticlesRepository
import com.tekskills.sampleapp.data.prefrences.AppPreferences
import com.tekskills.sampleapp.data.prefrences.SharedPrefManager
import com.tekskills.sampleapp.databinding.ActivityPosterEditorBinding
import com.tekskills.sampleapp.model.PosterItem
import com.tekskills.sampleapp.ui.adapter.PostersAdapter
import com.tekskills.sampleapp.ui.comment.CommentBottomSheet
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
    var defaultBackImage: Bitmap? = null
    var preCurrentBackgroundImage: Bitmap? = null
    var preCurrentForegroundImage: Bitmap? = null
    var postCurrentBackgroundImage: Bitmap? = null
    var postCurrentForegroundImage: Bitmap? = null
    var posterItem: PosterItem? = null

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

        val database: ArticlesDatabase = ArticlesDatabase.getInstance(context = requireContext())

        val dao = database.dao
        val bannerDao = database.bannerDao
        val repository = ArticlesRepository(dao)
        val bannerRepo = BannerItemRepository(bannerDao)
        val factory = MainViewModelFactory(repository, bannerRepo, preferences)
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
        defaultBackImage = bm
        preCurrentBackgroundImage = bm
        preCurrentForegroundImage = bm
        binding.editorForegroundImage.setImageBitmap(preCurrentForegroundImage)
    }

    private fun setRecyclerAdapter() {
        recyclerAdapter =
            PostersAdapter(object : PostersAdapter.OnItemClickListener {
                override fun onItemClick(itemView: View, item: PosterItem) {
                    (activity as MainActivity?)!!.appBarLayoutHandle(true)
                }

                override fun onUploadClick(itemView: View, item: PosterItem) {
                    posterItem = item
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

        val sharedPrefManager: SharedPrefManager = SharedPrefManager.getInstance(requireActivity())

        getBannerInfo(sharedPrefManager.bannerSelect)
        if (posterItem != null)
            getArticleInfo(posterItem!!.posterId)
//        val BANNER_SAMPLE =
//            "https://news.maaproperties.com/assets/img/ads-img/Maproperty_Banner.gif"
//        displayImage(BANNER_SAMPLE, binding.ivBannerShare)

        binding.ivBannerShare.visibility = View.GONE
        binding.ivBannerLogo.visibility = View.GONE
        binding.editorForegroundImage!!.setOnTouchListener(MultiTouchListener(this@PosterEditorFragment))

        binding.ivCancel.setOnClickListener {
//            parentFragmentManager.beginTransaction().detach(this).commit()
//            parentFragmentManager.beginTransaction().attach(this).commit()
            visibleEditor()
        }

        binding.heartButton.setOnLikeListener(object : OnLikeListener {
            override fun liked(likeButton: LikeButton?) {
//                    Toast.makeText(activity, "Liked!", Toast.LENGTH_SHORT).show();
            }

            override fun unLiked(likeButton: LikeButton?) {
//                    Toast.makeText(activity, "unLiked!", Toast.LENGTH_SHORT).show();
            }
        })

        binding.heartButton.setOnAnimationEndListener(object : OnAnimationEndListener {
            override fun onAnimationEnd(likeButton: LikeButton?) {
                if (posterItem != null)
                    updateViewCount(posterItem!!.posterId)
            }
        })

        binding.ivShare.setOnClickListener {
            binding.apply {
                getBannerInfo(sharedPrefManager.bannerSelect)
                sharedPrefManager.saveBannerSelect()
                editorPager.visibility = View.GONE
                ivBannerShare.visibility = View.VISIBLE
                ivBannerLogo.visibility = View.VISIBLE
                object : CountDownTimer(200, 100) {
                    override fun onFinish() {
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
                                    rlEditor!!,
                                    "article_image"
                                )
                            ShareLayout.simpleLayoutShare(
                                requireContext(),
                                editorViewToSave,
                                " ",
                                activityOptions
                            )
                            if (posterItem != null)
                                updateShareCount(posterItem!!.posterId)
                            ivBannerShare.visibility = View.GONE
                            ivBannerLogo.visibility = View.GONE
                            visibleEditor()
                        }
                    }

                    override fun onTick(millisUntilFinished: Long) {}
                }.start()

            }
        }

        binding.comment.setOnClickListener {
            if (posterItem != null) {
                val bottomSheetFragment = CommentBottomSheet()
                val bundle = Bundle()
                bundle.putInt("article_id", posterItem!!.posterId)
                bottomSheetFragment.arguments = bundle

                bottomSheetFragment.show(
                    parentFragmentManager,
                    CommentBottomSheet.TAG
                )
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
            val database: ArticlesDatabase = ArticlesDatabase.getInstance(requireContext())
            val dao = database.dao
            val commentsDao = database.commentDao

            val existingArticle = dao.getArticlesById(articleId)
            if (existingArticle != null) {
                // Update the existing article
                var count = existingArticle.view_count + 1
                dao.incrementViewCount(existingArticle.news_id, count)
            } else {
                val article = ArticlesAllNews(news_id = articleId, view_count = 1)
                dao.insertArticles(article)
            }

            // Fetch the updated data from the database
            requireActivity().runOnUiThread(Runnable {
                val commentData = commentsDao.getCommentsForItem(articleId)
                val updatedData = dao.getArticlesById(articleId)
                binding.apply {
                    likes.text = updatedData?.view_count.toString()
                    shares.text = updatedData?.share_count.toString()
                    Log.d("TAG", "comments data ${commentData.toString()}")
                    comments.text =
                        if (commentData.isEmpty()) "0" else commentData.size.toString()
                }
            })
//            database.close()
        }

    }

    fun updateShareCount(articleId: Int) {

        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            // Update the view count in the Room database
            val database: ArticlesDatabase =
                ArticlesDatabase.getInstance(context = requireContext())
            val dao = database.dao
            val commentsDao = database.commentDao
            val existingArticle = dao.getArticlesById(articleId)
            if (existingArticle != null) {
                // Update the existing article
                val count = existingArticle.share_count + 1
                dao.incrementShareCount(existingArticle.news_id, count)
            } else {
                val insertArticle = ArticlesAllNews(news_id = articleId, share_count = 1)
                // Insert a new article
                dao.insertArticles(insertArticle)
            }

            // Fetch the updated data from the database
            requireActivity().runOnUiThread(Runnable {
                val commentData = commentsDao.getCommentsForItem(articleId)
                val updatedData = dao.getArticlesById(articleId)
                binding.apply {
                    likes.text = updatedData?.view_count.toString()
                    shares.text = updatedData?.share_count.toString()
                    Log.d("TAG", "comments data ${commentData.toString()}")
                    comments.text =
                        if (commentData.isEmpty()) "0" else commentData.size.toString()
                }
            })
//            database.close()
        }

    }

    private fun getArticleInfo(newsId: Int) {

        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            // Update the view count in the Room database
            val database: ArticlesDatabase =
                ArticlesDatabase.getInstance(context = requireContext())
            val dao = database.dao
            val commentsDao = database.commentDao

            // Fetch the updated data from the database
            requireActivity().runOnUiThread(Runnable {
                val commentData = commentsDao.getCommentsForItem(newsId)
                val updatedData = dao.getArticlesById(newsId)
                binding.apply {
                    comments.text =
                        if (commentData.isEmpty()) "0" else commentData.size.toString()
                    if (updatedData != null) {
                        likes.text = updatedData.view_count.toString()
                        shares.text = updatedData.share_count.toString()
                    }
                    Log.d("TAG", "comments data ${commentData.toString()}")
                }
            })
//            database.close()
        }
    }

    private fun getBannerInfo(bannerSelect: Int) {
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            // Update the view count in the Room database
            val database: ArticlesDatabase =
                ArticlesDatabase.getInstance(context = requireContext())
            val bannerDao = database.bannerDao

            // Fetch the updated data from the database
            requireActivity().runOnUiThread(Runnable {
                var banners = bannerDao.getAllBannerItems()
                binding.apply {
                    var count = bannerSelect + 1
                    if (count < banners.size) {
                        val updatedData = bannerDao.getAllBannerItems()[count].link
                        displayImage(updatedData, ivBannerShare)
                    } else if (count > banners.size) {
                        val num = count / banners.size
                        val updatedData = bannerDao.getAllBannerItems()[num].link
                        displayImage(updatedData, ivBannerShare)
                    } else if (banners.isNotEmpty()) {
                        val updatedData = bannerDao.getAllBannerItems()[0].link
                        displayImage(updatedData, ivBannerShare)
                    } else {
                        val BANNER_SAMPLE =
                            "https://news.maaproperties.com/assets/img/ads-img/Maproperty_Banner.gif"
                        displayImage(BANNER_SAMPLE, ivBannerShare)
                    }
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
                mCropImageUri = result.uri
                if (whichImage == 0) {
                    setBackgroundImage()
                } else if (whichImage == 1) {
                    setForegroundImage()
                }
                visibleEditor()
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }

    private fun visibleEditor() {
        if (binding.swipeRefreshLayoutEditor.isVisible) {
            binding.swipeRefreshLayoutEditor.visibility = GONE
            binding.editorPager.visibility = GONE
            binding.rlEditor.visibility = VISIBLE
        } else {
            parentFragmentManager.beginTransaction().detach(this).commit()
            parentFragmentManager.beginTransaction().attach(this).commit()
            binding.swipeRefreshLayoutEditor.visibility = VISIBLE
            binding.editorPager.visibility = VISIBLE
            binding.rlEditor.visibility = GONE
        }
    }

    private fun setBackgroundImage() {
        defaultBackImage = null
        defaultBackImage = uriToBitmap(mCropImageUri)
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

    fun makeMaskImage(maskImage: String, frameImage: String) {
        currentMaskImage = maskImage
        Glide.with(requireContext())
            .asBitmap()
            .load(frameImage)
            .error(R.drawable.place_holder)
            .into(binding.editorFrameImage)
        try {
            val mask: Bitmap = Glide
                .with(requireContext())
                .asBitmap()
                .load(maskImage)
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
        val width = defaultBackImage!!.width
        val height = defaultBackImage!!.height
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
            Bitmap.createBitmap(defaultBackImage!!, 0, 0, width, height, matrix, false)
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