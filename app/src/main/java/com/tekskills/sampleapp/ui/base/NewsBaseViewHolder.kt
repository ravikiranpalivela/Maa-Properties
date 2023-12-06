package com.tekskills.sampleapp.ui.base

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.URLUtil
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.radiobutton.MaterialRadioButton
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.tekskills.sampleapp.R
import com.tekskills.sampleapp.data.prefrences.SharedPrefManager
import com.tekskills.sampleapp.databinding.ItemArticleViewtypeListBinding
import com.tekskills.sampleapp.model.BannerItem
import com.tekskills.sampleapp.model.ItemOption
import com.tekskills.sampleapp.model.NewsItem
import com.tekskills.sampleapp.model.PollDetails
import com.tekskills.sampleapp.model.PublicAdsDetails
import com.tekskills.sampleapp.ui.adapter.OnNewsClickListener
import com.tekskills.sampleapp.ui.adapter.OptionsAdapter
import com.tekskills.sampleapp.ui.main.MainActivity
import com.tekskills.sampleapp.ui.main.MainViewModel
import com.tekskills.sampleapp.utils.AppConstant.ADS_IMAGE_URL
import com.tekskills.sampleapp.utils.AppUtil
import com.tekskills.sampleapp.utils.like.LikeButton
import com.tekskills.sampleapp.utils.like.OnAnimationEndListener
import com.tekskills.sampleapp.utils.like.OnLikeListener
import com.tekskills.sampleapp.utils.reactions.ReactionPopup
import com.tekskills.sampleapp.utils.reactions.ReactionsConfigBuilder
import com.tekskills.sampleapp.utils.video.changeDateFormat
import com.tekskills.sampleapp.utils.video.getThumbnail
import com.tekskills.sampleapp.utils.video.isValidURL
import com.tekskills.sampleapp.utils.video.isValidUrl
import com.tekskills.sampleapp.utils.video.isYoutubeUrl
import java.util.concurrent.Executors

abstract class NewsBaseViewHolder<viewDataBinding : ViewDataBinding>(
    private val activity: Activity,
    private val viewModel: MainViewModel,
    private val bindingView: viewDataBinding,
    private val lifecycle: Lifecycle
) :
    RecyclerView.ViewHolder(bindingView.root) {

    protected abstract val binding: ItemArticleViewtypeListBinding
    private var playbackPosition = 0L
    private var playWhenReady = true
    private var initializedYouTubePlayer: YouTubePlayer? = null
    private val optionAdapter by lazy {
        OptionsAdapter(activity)
    }

    fun bind(
        article: NewsItem,
        onClickListener: OnNewsClickListener,
    ) {
        // Get the view count using the ViewModel
        val sharedPrefManager: SharedPrefManager = SharedPrefManager.getInstance(activity)

        binding.apply {
            clArticleView.visibility = View.VISIBLE
            clArticleAdView.visibility = View.GONE

            getBannerInfo(sharedPrefManager.getBannerDetailsData(), sharedPrefManager.bannerSelect)

            if (validateValue(article.description) == "null")
                descArticle.visibility = View.GONE
            else {
                descArticle.text = article.description
                descArticle.visibility = View.VISIBLE
            }

            if (validateValue(article.title) == "null")
                titleArticle.visibility = View.GONE
            else {
                titleArticle.visibility = View.VISIBLE
                titleArticle.text = article.title
            }

//            var count = viewModel.getArticleViewCount(article.newsId)

//            likes.text = count.toString()

            likes.text = article.likes.toString()

            shares.text = article.share.toString()

            comments.text = article.comments.size.toString()

            article.scheduleDate?.let {
                publishedAt.text = "Posted date : ${changeDateFormat(it)}"
            }

            titleArticle.setOnClickListener {
                onClickListener.clickListener(article, articleImage)
            }

            article.websiteUrl?.let {
                readMoreArticle.visibility = if (article.websiteUrl.isValidUrl())
                    View.VISIBLE else View.GONE
            }

            readMoreArticle.setOnClickListener {
                onClickListener.readMoreClickListener(article, articleImage)
            }

            heartButton.setOnLikeListener(object : OnLikeListener {
                override fun liked(likeButton: LikeButton?) {
//                    Toast.makeText(activity, "Liked!", Toast.LENGTH_SHORT).show();
                }

                override fun unLiked(likeButton: LikeButton?) {
//                    Toast.makeText(activity, "unLiked!", Toast.LENGTH_SHORT).show();
                }
            })
            heartButton.isLiked = article.likeByUser
            heartButton.isEnabled = !article.likeByUser

            heartButton.setOnAnimationEndListener(object : OnAnimationEndListener {
                override fun onAnimationEnd(likeButton: LikeButton?) {
                    onClickListener.likeClickListener(article, articleImage)
//                    updateViewCount(article.newsId, article)
                }
            })

            comment.setOnClickListener {
                onClickListener.commentClickListener(article, articleImage)
            }

//            like.setOnClickListener {
//                count++
//                likes.text = count.toString()
////                onClickListener.likeClickListener(article, articleImage)
//
////                updateViewCount(article.newsId, article)
////                var countData = viewModel.getArticleViewCount(article.newsId)
////                countData++
////                likes.text = countData.toString()
//            }
            val strings = arrayOf("like", "love", "laugh", "wow", "sad", "angry")

            val popup = ReactionPopup(
                activity,
                ReactionsConfigBuilder(activity)
                    .withReactions(
                        intArrayOf(
                            R.drawable.ic_fb_like,
                            R.drawable.ic_fb_love,
                            R.drawable.ic_fb_laugh,
                            R.drawable.ic_fb_wow,
                            R.drawable.ic_fb_sad,
                            R.drawable.ic_fb_angry
                        )
                    )
                    .withPopupAlpha(20)
                    .withReactionTexts { position -> strings[position] }
                    .withTextBackground(ColorDrawable(Color.TRANSPARENT))
                    .withTextColor(Color.BLACK)
                    .withTextHorizontalPadding(0)
                    .withTextVerticalPadding(0)
                    .withTextSize(activity.resources.getDimension(R.dimen.reactions_text_size))
                    .build()
            ) {
                if (it) {
//                    updateViewCount(article.newsId, article)
                }
            }

            like.setOnTouchListener(popup)

//            root.setOnClickListener(object : DoubleClickListener() {
//                override fun onDoubleClick(v: View) {
//                    Log.d("Event", "action response double click")
//                    doubleClickListener(article,articleImage)
//                }
//            })

            root.setOnClickListener() {
                Log.d("Event", "action response double click")
                onClickListener.doubleClickListener(article, articleImage)
            }


//            displayImage(ADS_IMAGE_URL, ivBannerShare)
            setupRecyclerView()
            ivBannerShare.visibility = View.GONE
            ivBannerLogo.visibility = View.GONE

            ivShare.setOnClickListener {
                (activity as MainActivity?)!!.appBarLayoutHandle(true)
                getBannerInfo(
                    sharedPrefManager.getBannerDetailsData(),
                    sharedPrefManager.bannerSelect
                )
                sharedPrefManager.saveBannerSelect()
                youtubePlayerView.visibility = View.GONE
                webView.visibility = View.GONE
                articleImage.visibility = View.VISIBLE
                btnPlay.visibility = View.VISIBLE
                ivBannerShare.visibility = View.VISIBLE
                ivBannerLogo.visibility = View.VISIBLE
                object : CountDownTimer(200, 10) {
                    override fun onFinish() {
                        onClickListener.shareClickListener(article, clArticleView)
                        ivBannerShare.visibility = View.GONE
                        ivBannerLogo.visibility = View.GONE
                    }

                    override fun onTick(millisUntilFinished: Long) {}
                }.start()
            }

            article.pollDetails?.let {
                article.pollDetails?.options?.let { poll ->
                    if (poll.isNotEmpty()) {
                        binding.llPolling.visibility = View.VISIBLE
//                        binding.llPollQuestion.visibility = View.VISIBLE
                        setupSurveyUi(article.pollDetails, article.submittedAnswer ?: "")
                        chipVote.isClickable =
                            article.submittedAnswer.isNullOrEmpty() && article.submittedAnswer == ""
                        chipVote.isClickable = article.submittedAnswer.isNullOrEmpty()
                        chipVote.isEnabled = article.submittedAnswer.isNullOrEmpty()
                    } else binding.llPolling.visibility = View.GONE
                }
            }

            binding.chipVote.setOnCheckedStateChangeListener { group, checkedIds ->
                if(article.submittedAnswer.isNullOrEmpty() && article.submittedAnswer == null) {
                    Log.d("TAG", "onCheckedChanged")
                    val id = checkedIds[0]
                    val chip = group.findViewById(id) as Chip
                    onClickListener.voteClickListener(article, chip.text.toString())
                } else
                    Toast.makeText(activity, "You Voted Thank you", Toast.LENGTH_SHORT).show()
            }

            binding.radioGroup.setOnCheckedChangeListener { radioGroup, i ->
                Log.d("TAG", "onCheckedChanged")
                val chip = radioGroup.findViewById(i) as RadioButton
                if (chip.text == "Yes") {
                    binding.llPolling.visibility = View.VISIBLE
                    setupSurveyUi(article.pollDetails, article.submittedAnswer)
                } else binding.llPolling.visibility = View.GONE
            }

            if (validateUrlValue(article.videoPath) != "null"
                && validateUrlValue(article.videoUrl) != "null"
            ) {
                if (article.videoUrl.isValidUrl())
                    playVideoUrlData(article.videoUrl)
                else if (article.videoPath?.isValidUrl()!!)
                    playVideoUrlData(article.videoPath!!)
            } else if (validateUrlValue(article.videoPath) == "null"
                && validateUrlValue(article.videoUrl) != "null"
            ) {
                if (article.videoUrl.isValidUrl())
                    playVideoUrlData(article.videoUrl)
                else {
                    if (validateUrlValue(article.imagePath) != "null"
                        && validateUrlValue(article.imageUrl) != "null"
                    ) {
                        clVideoPlayer.visibility = View.GONE
                        articleImage.visibility = View.VISIBLE
                        displayImage(article.imageUrl, articleImage)
                    } else if (validateUrlValue(article.imagePath) != "null"
                        && validateUrlValue(article.imageUrl) == "null"
                    ) {
                        clVideoPlayer.visibility = View.GONE
                        articleImage.visibility = View.VISIBLE
                        displayImage(article.imagePath, articleImage)
                    } else if (validateUrlValue(article.imagePath) == "null"
                        && validateUrlValue(article.imageUrl) != "null"
                    ) {
                        clVideoPlayer.visibility = View.GONE
                        articleImage.visibility = View.VISIBLE
                        displayImage(article.imageUrl, articleImage)
                    } else {
                        clVideoPlayer.visibility = View.GONE
                        articleImage.visibility = View.VISIBLE
                        displayImage(article.imageUrl, articleImage)
                    }
                }
            } else if (validateUrlValue(article.videoPath) != "null"
                && validateUrlValue(article.videoUrl) == "null"
            ) {
                if (article.videoPath.isValidUrl()!!)
                    playPathUrlVideo(article.videoPath!!)
            } else {
                if (validateUrlValue(article.imagePath) != "null"
                    && validateUrlValue(article.imageUrl) != "null"
                ) {
                    clVideoPlayer.visibility = View.GONE
                    articleImage.visibility = View.VISIBLE
                    displayImage(article.imageUrl, articleImage)
                } else if (validateUrlValue(article.imagePath) != "null"
                    && validateUrlValue(article.imageUrl) == "null"
                ) {
                    clVideoPlayer.visibility = View.GONE
                    articleImage.visibility = View.VISIBLE
                    displayImage(article.imagePath, articleImage)
                } else if (validateUrlValue(article.imagePath) == "null"
                    && validateUrlValue(article.imageUrl) != "null"
                ) {
                    clVideoPlayer.visibility = View.GONE
                    articleImage.visibility = View.VISIBLE
                    displayImage(article.imageUrl, articleImage)
                } else {
                    clVideoPlayer.visibility = View.GONE
                    articleImage.visibility = View.VISIBLE
                    displayImage(article.imageUrl, articleImage)
                }
            }
        }
    }

    fun bindAdView(
        onClickListener: OnNewsClickListener,
    ) {
        val sharedPrefManager: SharedPrefManager = SharedPrefManager.getInstance(activity)

        binding.apply {
            clArticleAdView.visibility = View.VISIBLE
            clArticleView.visibility = View.GONE

            getBannerAdsInfo(
                sharedPrefManager.getPublicAdsDetailsData(),
                sharedPrefManager.publicAdsAdsSelect
            )

            root.setOnClickListener() {
                Log.d("Event", "action response double click")
                onClickListener.doubleClickListener(null, articleImage)
            }

            sharedPrefManager.savePublicAdsAdsSelect()

        }
    }

    private fun getBannerInfo(banners: BannerItem?, bannerSelect: Int) {
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            if (banners != null)
                activity.runOnUiThread(Runnable {
                    binding.apply {
                        var count = bannerSelect + 1
                        if (banners.size == 0) {
                            displayImage(ADS_IMAGE_URL, ivBannerShare)
                        } else if (count < banners.size) {
                            val updatedData = banners[count].link
                            displayImage(updatedData, ivBannerShare)
                        } else if (count > banners.size) {
                            val num = count % banners.size
                            val updatedData = banners[num].link
                            displayImage(updatedData, ivBannerShare)
                        } else if (banners.isNotEmpty()) {
                            val updatedData = banners[0].link
                            displayImage(updatedData, ivBannerShare)
                        } else {
                            displayImage(ADS_IMAGE_URL, ivBannerShare)
                        }
                    }
                })
//            database.close()
        }
    }

    private fun setupSurveyUi(survey: PollDetails, submittedAnswer: String) {
        binding.apply {
            binding.tvPoleTitle.text = survey.title
            if (survey.noOfPolling > 0)
                binding.tvNoVotesPole.text = "No of votes Polled: ${survey.noOfPolling}"

            val options = mutableListOf<ItemOption>()
            if (submittedAnswer.isNotEmpty()) {
                binding.chipVote.visibility = View.GONE
                binding.rvList.visibility = View.VISIBLE
                if (survey.optionDetails != null) {
                    val totalVotes = survey.optionDetails.sumOf { it.count }
                    survey.optionDetails.filter { it.option != null && it.option.isNotEmpty() }
                        .forEach { option ->
                            val percentage =
                                if (totalVotes > 0) (option.count * 100) / totalVotes else 0
                            options.add(
                                ItemOption(
                                    title = option.option,
                                    percentage = percentage,
                                    vote = option.count,
                                    voted = option.option == submittedAnswer
                                )
                            )
                        }
                    optionAdapter.submitList(options)
                }
            } else {
                binding.chipVote.visibility = View.VISIBLE
                binding.rvList.visibility = View.GONE
                optionAdapter.submitList(options)
            }
            binding.chipVote.removeAllViews()
            binding.chipVote.isSingleSelection = true

            survey.options.forEach { option ->
                if (option != null && option.isNotEmpty()) {
                    MaterialRadioButton(activity)
                        .apply { text = option }
                        .also {
//                        binding.radioGroup.addView(it)
                        }
                    binding.chipVote.addView(
                        createTagChip(
                            activity,
                            option,
                            submittedAnswer == option
                        )
                    )
                }
            }
        }
    }

    private fun setupRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(
            /* context */ activity,
            /* orientation */ RecyclerView.VERTICAL,
            /* reverseLayout */ false
        )
        binding.rvList.apply {
            adapter = optionAdapter
            layoutManager = linearLayoutManager
        }
    }

    private fun createTagChip(context: Context, chipName: String, checkedData: Boolean): Chip {
        return Chip(context).apply {
            text = chipName
            isCloseIconVisible = false
            isCheckable = true
            isCheckedIconVisible = true
            if (checkedData)
                isChecked = checkedData
            chipIcon = ContextCompat.getDrawable(context, R.drawable.chip_selector)
            setChipBackgroundColorResource(R.color.bg_chip_state_list)
            setChipIconResource(R.drawable.chip_selector)
            setTextColor(ContextCompat.getColor(context, R.color.black))
        }
    }

    private fun getBannerAdsInfo(banners: PublicAdsDetails?, bannerSelect: Int) {
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {

            if (banners != null)
                activity.runOnUiThread(Runnable {
                    binding.apply {
                        var count = bannerSelect + 1
                        if (banners.size == 0) {
                            displayImage(ADS_IMAGE_URL, ivBannerShare)
                        } else if (count < banners.size) {
                            val updatedData = banners[count].filePath
                            displayImage(updatedData, ivBannerAds)
                        } else if (count > banners.size) {
                            val num = count % banners.size
                            val updatedData = banners[num].filePath
                            displayImage(updatedData, ivBannerAds)
                        } else if (banners.isNotEmpty()) {
                            val updatedData = banners[0].filePath
                            displayImage(updatedData, ivBannerAds)
                        } else {
                            displayImage(ADS_IMAGE_URL, ivBannerAds)
                        }
                    }
                })
//            database.close()
        }
    }

    private fun playVideoUrlData(videoUrl: String) {
        binding.apply {
            articleImage.visibility = View.GONE
            clVideoPlayer.visibility = View.VISIBLE

            btnPlay.visibility = View.GONE
            displayImage(videoUrl.getThumbnail(), articleImage)
//            btnPlay.setOnClickListener {
            if (videoUrl.isYoutubeUrl()) initPlayUrlPlayerView(videoUrl)
            else initPlayPathUrlPlayerView(videoUrl)
//                articleImage.visibility = View.GONE
//                btnPlay.visibility = View.GONE
//            }
        }
    }

    private fun playPathUrlVideo(videoUrl: String) {
        binding.apply {
            articleImage.visibility = View.VISIBLE
            clVideoPlayer.visibility = View.VISIBLE
            btnPlay.visibility = View.VISIBLE
            displayImage(videoUrl, articleImage)

            btnPlay.setOnClickListener { view ->
                if (videoUrl.isYoutubeUrl()) initPlayUrlPlayerView(videoUrl)
                else
                    initPlayPathUrlPlayerView(videoUrl)
            }
        }
    }

    private fun initPlayPathUrlPlayerView(videoUrl: String) {
        binding.apply {
            youtubePlayerView.visibility = View.GONE
            webView.visibility = View.VISIBLE
            val htmlContent =
                "<meta name=\"viewport\" content='width=device-width, initial-scale=1.0,text/html,charset=utf-8' ><html><body><video id='myVideo' style='border: none; overflow: hidden; height: 100%; width: 100%; ' controls autoplay><source src=${videoUrl} type='video/mp4'></video></body></html>"

            binding.webView.settings.javaScriptEnabled = true
            binding.webView.webChromeClient = object : WebChromeClient() {
            }

            binding.webView.addJavascriptInterface(object : Any() {
                @JavascriptInterface
                fun performClick(value: String) {
//                    Toast.makeText(activity, "clicked $value", Toast.LENGTH_SHORT).show()
                }
            }, "ok")

            binding.webView.loadDataWithBaseURL(
                null, htmlContent,
                "text/html", "UTF-8", null
            )
            binding.webView.settings.mediaPlaybackRequiresUserGesture = false
            binding.webView.settings.useWideViewPort = true
            binding.webView.settings.domStorageEnabled = true
            binding.webView.settings.builtInZoomControls = false
            binding.webView.setInitialScale(1)

            binding.webView.settings.loadWithOverviewMode = true
            binding.webView.scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
            binding.webView.isScrollbarFadingEnabled = false
            articleImage.visibility = View.GONE
            btnPlay.visibility = View.GONE
        }
    }

    private fun initPlayUrlPlayerView(videoUrl: String) {
        binding.apply {
            youtubePlayerView.visibility = View.GONE
            webView.visibility = View.VISIBLE
            val htmlContent =
                "<meta name=\"viewport\" content='width=device-width, initial-scale=1.0,text/html,charset=utf-8' ><html><body><iframe _ngcontent-nak-c16='' frameborder='0' allowtransparency='true' allow='fullscreen' style='border: none; overflow: hidden; height: 100%; width: 100%;' src=${videoUrl}></iframe></body></html>"

            binding.webView.settings.javaScriptEnabled = true
            binding.webView.webChromeClient = object : WebChromeClient() {
            }

            binding.webView.addJavascriptInterface(object : Any() {
                @JavascriptInterface
                fun performClick(value: String) {
//                    Toast.makeText(activity, "clicked $value", Toast.LENGTH_SHORT).show()
                }
            }, "ok")

            binding.webView.loadDataWithBaseURL(
                null, htmlContent,
                "text/html", "UTF-8", null
            )
            binding.webView.settings.mediaPlaybackRequiresUserGesture = false
            binding.webView.settings.useWideViewPort = true
            binding.webView.settings.domStorageEnabled = true
            binding.webView.settings.builtInZoomControls = false
            binding.webView.setInitialScale(1)

            binding.webView.settings.loadWithOverviewMode = true
            binding.webView.scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
            binding.webView.isScrollbarFadingEnabled = false
            articleImage.visibility = View.GONE
            btnPlay.visibility = View.GONE
        }
    }


//        if (binding.youtubePlayerView != null) {
//            binding.youtubePlayerView?.initialize(object :
//                AbstractYouTubePlayerListener() {
//                override fun onReady(youTubePlayer: YouTubePlayer) {
//                    binding.articleImage.visibility = View.GONE
//                    binding.btnPlay.visibility = View.GONE
//                    binding.youtubePlayerView.visibility = View.VISIBLE
//                    binding.webView.visibility = View.GONE
//                    youTubePlayer.loadVideo(videoUrl.getVideoId()!!, 0F)
//                }
//
//                override fun onError(
//                    youTubePlayer: YouTubePlayer,
//                    error: PlayerConstants.PlayerError
//                ) {
//                    youTubePlayer.loadVideo(videoUrl.getVideoId()!!, 0F)
//                }
//            })
//        } else {
//            binding.youtubePlayerView.getYouTubePlayerWhenReady(object : YouTubePlayerCallback {
//                override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
//                    binding.articleImage.visibility = View.GONE
//                    binding.btnPlay.visibility = View.GONE
//                    binding.youtubePlayerView.visibility = View.VISIBLE
//                    binding.webView.visibility = View.GONE
//                    youTubePlayer.loadVideo(videoUrl.getVideoId()!!, 0F)
//                }
//            })
//        }
//    }

//    private fun initPlayPathUrlPlayerView(videoUrl: String) {
//        binding.apply {
//            youtubePlayerView.visibility = View.GONE
//            webView.visibility = View.VISIBLE
//            val htmlContent =
//                "<meta name=\"viewport\" content='width=device-width, initial-scale=1.0,text/html,charset=utf-8' ><html><body><video id='myVideo' width='auto' height='auto' controls autoplay><source src=${videoUrl} type='video/mp4'></video></body></html>"
//
//            binding.webView.settings.javaScriptEnabled = true
//            binding.webView.webChromeClient = object : WebChromeClient() {
//            }
//
//            binding.webView.addJavascriptInterface(object : Any() {
//                @JavascriptInterface
//                fun performClick(value: String) {
//                    Toast.makeText(activity, "clicked $value", Toast.LENGTH_SHORT).show()
//                }
//            }, "ok")
//
//            binding.webView.loadDataWithBaseURL(
//                null, htmlContent,
//                "text/html", "UTF-8", null
//            )
//            binding.webView.settings.mediaPlaybackRequiresUserGesture = false
//            binding.webView.settings.useWideViewPort = true
//            binding.webView.settings.domStorageEnabled = true
//            binding.webView.settings.builtInZoomControls = false
//            binding.webView.setInitialScale(1)
//
//            binding.webView.settings.loadWithOverviewMode = true
//            binding.webView.scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
//            binding.webView.isScrollbarFadingEnabled = false
//            articleImage.visibility = View.GONE
//            btnPlay.visibility = View.GONE
//        }
//    }
//
//    private fun initYouTubePlayerView(videoUrl: String) {
//        if (binding.youtubePlayerView != null) {
//            binding.youtubePlayerView?.initialize(object :
//                AbstractYouTubePlayerListener() {
//                override fun onReady(youTubePlayer: YouTubePlayer) {
//                    binding.articleImage.visibility = View.GONE
//                    binding.btnPlay.visibility = View.GONE
//                    binding.youtubePlayerView.visibility = View.VISIBLE
//                    binding.webView.visibility = View.GONE
//                    youTubePlayer.loadVideo(videoUrl.getVideoId()!!, 0F)
//                }
//
//                override fun onError(
//                    youTubePlayer: YouTubePlayer,
//                    error: PlayerConstants.PlayerError
//                ) {
//                    youTubePlayer.loadVideo(videoUrl.getVideoId()!!, 0F)
//                }
//            })
//        } else {
//            binding.youtubePlayerView.getYouTubePlayerWhenReady(object : YouTubePlayerCallback {
//                override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
//                    binding.articleImage.visibility = View.GONE
//                    binding.btnPlay.visibility = View.GONE
//                    binding.youtubePlayerView.visibility = View.VISIBLE
//                    binding.webView.visibility = View.GONE
//                    youTubePlayer.loadVideo(videoUrl.getVideoId()!!, 0F)
//                }
//            })
//        }
//    }

    fun displayImage(videoUrl: String?, view: ImageView?) {
//        Glide.with(activity)
//            .asBitmap()
//            .load(videoUrl)
//            .error(R.drawable.place_holder)
//            .into(view!!)
        AppUtil.loadGlideImage(Glide.with(activity), videoUrl!!, view)
    }

    private fun validateUrlValue(first: String?): String {
        return when {
            !first.isNullOrEmpty() && first != "null" && first.isValidUrl() -> first
            else -> "null"
        }
    }

    private fun validateValue(first: String?): String {
        return when {
            !first.isNullOrEmpty() && first != "null" -> first
            else -> "null"
        }
    }

//    private fun validateOneValue(first: String?, second: String?): String {
//        return when {
//            !first.isNullOrEmpty() && first.isValidUrl() -> first.isValidURL()
//            !second.isNullOrEmpty() && second.isValidUrl() -> second.isValidURL()
//            else -> "null"
//        }
//    }

//    private fun validateOneString(first: String?, second: String?): String {
//        return first ?: second ?: ""
//    }

    companion object {

    }
}