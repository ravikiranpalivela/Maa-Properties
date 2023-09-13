package com.tekskills.sampleapp.ui.articledetails

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Html
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.tekskills.sampleapp.R
import com.tekskills.sampleapp.databinding.ActivityArticleDetailsBinding
import com.tekskills.sampleapp.model.AllNewsItem
import com.tekskills.sampleapp.ui.base.BaseActivity
import com.tekskills.sampleapp.ui.main.MainViewModel
import com.tekskills.sampleapp.utils.ObjectSerializer
import com.tekskills.sampleapp.utils.ShareLayout
import com.tekskills.sampleapp.utils.video.changeDateFormat
import com.tekskills.sampleapp.utils.video.getThumbnail
import com.tekskills.sampleapp.utils.video.getVideoId
import com.tekskills.sampleapp.utils.video.isValidURL
import com.tekskills.sampleapp.utils.video.isValidUrl
import com.tekskills.sampleapp.utils.video.isYoutubeUrl

class ArticleDetailsActivity :
    BaseActivity<ActivityArticleDetailsBinding, MainViewModel, ArticleDetailsActivity>() {

    private lateinit var article: AllNewsItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.elevation = 0f

        article = ObjectSerializer.deserialize(intent.getStringExtra("article")) as AllNewsItem

        setUpLayout()
    }

    fun setUpLayout() {
        binding.includedLayout.apply {
            if (article.description.isNullOrEmpty() && article.videoDescription.isNullOrEmpty()) {
                articleContent.visibility = View.GONE
            }
            else {
                articleContent.visibility = View.VISIBLE
                val result: Spanned
                val html =
                    "${article.description} \n\nFull article at : <a href=${article.websiteUrl}>${article.websiteUrl}</a> link"
                result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
                } else {
                    Html.fromHtml(html)
                }
                articleContent.text = result
                articleContent.movementMethod = LinkMovementMethod.getInstance()
            }
            if (article.title.isNullOrEmpty())
                articleTitle.visibility = View.GONE
            else
                articleTitle.visibility = View.VISIBLE

            articleTitle.text = article.title
            articleContent.text = validateOneString(article.description, article.videoDescription)

            article.scheduleDate.let {
                publishedAt.text = "Posted date : ${changeDateFormat(it)}"
            }

            val BANNER_SAMPLE =
                "https://news.maaproperties.com/assets/img/ads-img/Maproperty_Banner.gif"
            displayImage(BANNER_SAMPLE,ivBannerShare)

            ivBannerShare.visibility = View.GONE
            ivBannerLogo.visibility = View.GONE

            ivShare.setOnClickListener {
                val youtube = youtubePlayerView.visibility
                val webviewDisplay = webView.visibility
                youtubePlayerView.visibility = View.GONE
                webView.visibility = View.GONE
                articleImage.visibility = View.VISIBLE
                btnPlay.visibility = View.VISIBLE
                ivBannerShare.visibility = View.VISIBLE
                ivBannerLogo.visibility = View.VISIBLE
                object : CountDownTimer(200, 100) {
                    override fun onFinish() {
                        ShareLayout.simpleLayoutShare(
                            getContext(),
                            clArticalView,
                            " ${article.title} \n ${articleContent.text}",
                            null
                        )
                        ivBannerShare.visibility = View.GONE
                        ivBannerLogo.visibility = View.GONE
//                        youtubePlayerView.visibility = youtube
//                        webView.visibility = webviewDisplay
                    }

                    override fun onTick(millisUntilFinished: Long) {}
                }.start()
            }

            binding.ivLogo.setOnClickListener {
                onBackPressed()
            }

            binding.appBar.addOnOffsetChangedListener(object :
                AppBarLayout.OnOffsetChangedListener {
                var isShow = false
                var scrollRange = -1
                override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                    if (scrollRange == -1) {
                        scrollRange = appBarLayout.totalScrollRange
                    }
                    if (scrollRange + verticalOffset == 0) {
                        isShow = true
                    } else if (isShow) {
                        isShow = false
                    }
                }
            })

            if (!validateOneValue(article.videoPath, article.videoFilePath)
                    .equals("null") && !article.videoUrl.isNullOrEmpty()
            ) {
                articleImage.visibility = View.GONE
                clVideoPlayer.visibility = View.VISIBLE
                if (article.videoUrl.isValidUrl())
                    playVideoUrlData(article.videoUrl)
                else if (validateOneValue(article.videoPath, article.videoFilePath)?.isValidUrl()!!)
                    playVideoUrlData(validateOneValue(article.videoPath, article.videoFilePath)!!)
            } else if (validateOneValue(article.videoPath, article.videoFilePath)
                    .equals("null") && !article.videoUrl.isNullOrEmpty()
            ) {
                articleImage.visibility = View.GONE
                clVideoPlayer.visibility = View.VISIBLE
                if (article.videoUrl.isValidUrl())
                    playVideoUrlData(article.videoUrl)
                else {
                    if (!validateOneValue(
                            article.imageFilePath,
                            article.imagePath
                        ).equals("null") && !article.imageUrl.isNullOrEmpty()
                    ) {
                        clVideoPlayer.visibility = View.GONE
                        articleImage.visibility = View.VISIBLE
                        displayImage(article.imageUrl,articleImage)
                    } else if (!validateOneValue(
                            article.imageFilePath,
                            article.imagePath
                        ).equals("null") && article.imageUrl.isNullOrEmpty()
                    ) {
                        clVideoPlayer.visibility = View.GONE
                        articleImage.visibility = View.VISIBLE
                        displayImage(validateOneValue(article.imageFilePath, article.imagePath),articleImage)
                    }
                }
            } else if (!validateOneValue(
                    article.videoPath,
                    article.videoFilePath
                ).equals("null") && article.videoUrl.isNullOrEmpty()
            ) {
                articleImage.visibility = View.GONE
                clVideoPlayer.visibility = View.VISIBLE
                if (validateOneValue(article.videoPath, article.videoFilePath)?.isValidUrl()!!)
                    playPathUrlVideo(validateOneValue(article.videoPath, article.videoFilePath)!!)
            } else {
                if (!validateOneValue(
                        article.imageFilePath,
                        article.imagePath
                    ).equals("null") && !article.imageUrl.isNullOrEmpty()
                ) {
                    clVideoPlayer.visibility = View.GONE
                    articleImage.visibility = View.VISIBLE
                    displayImage(article.imageUrl,articleImage)
                } else if (!validateOneValue(
                        article.imageFilePath,
                        article.imagePath
                    ).equals("null") && article.imageUrl.isNullOrEmpty()
                ) {
                    clVideoPlayer.visibility = View.GONE
                    articleImage.visibility = View.VISIBLE
                    displayImage(validateOneValue(article.imageFilePath, article.imagePath),articleImage)
                } else {
                    clVideoPlayer.visibility = View.GONE
                    articleImage.visibility = View.VISIBLE
                    displayImage(article.imageUrl,articleImage)
                }
            }
        }
    }
    
    private fun playVideoUrlData(videoUrl: String) {
        binding.includedLayout.apply {
            articleImage.visibility = View.VISIBLE
            clVideoPlayer.visibility = View.VISIBLE

            btnPlay.visibility = View.VISIBLE
            displayImage(videoUrl.getThumbnail(),articleImage)
            btnPlay.setOnClickListener {
                articleImage.visibility = View.GONE
                btnPlay.visibility = View.GONE
                if (videoUrl.isYoutubeUrl()) initYouTubePlayerView(videoUrl)
                else initPlayPathUrlPlayerView(videoUrl)
            }
        }
    }

    private fun playPathUrlVideo(videoUrl: String) {
        binding.includedLayout.apply {
            articleImage.visibility = View.VISIBLE
            clVideoPlayer.visibility = View.VISIBLE
            btnPlay.visibility = View.VISIBLE
            displayImage(videoUrl,articleImage)

            btnPlay.setOnClickListener { view ->
                articleImage.visibility = View.GONE
                btnPlay.visibility = View.GONE
                if (videoUrl.isYoutubeUrl()) initYouTubePlayerView(videoUrl)
                else
                    initPlayPathUrlPlayerView(videoUrl)
            }
        }
    }

    private fun initPlayPathUrlPlayerView(videoUrl: String) {
        binding.includedLayout.apply {

            articleImage.visibility = View.GONE
            btnPlay.visibility = View.GONE
            youtubePlayerView.visibility = View.GONE
            webView.visibility = View.VISIBLE
            val htmlContent =
                "<meta name=\"viewport\" content='width=device-width, initial-scale=1.0,text/html,charset=utf-8' ><html><body><video id='myVideo' width='auto' height='auto' controls autoplay><source src=${videoUrl} type='video/mp4'></video></body></html>"

            webView.settings.javaScriptEnabled = true
            webView.webChromeClient = object : WebChromeClient() {
            }

            webView.addJavascriptInterface(object : Any() {
                @JavascriptInterface
                fun performClick(value: String) {
                    Toast.makeText(this@ArticleDetailsActivity, "clicked $value", Toast.LENGTH_SHORT).show()
                }
            }, "ok")

            webView.loadDataWithBaseURL(
                null, htmlContent,
                "text/html", "UTF-8", null
            )
            webView.settings.mediaPlaybackRequiresUserGesture = false
            webView.settings.useWideViewPort = true
            webView.settings.domStorageEnabled = true
            webView.settings.builtInZoomControls = false
            webView.setInitialScale(1)

            webView.settings.loadWithOverviewMode = true
            webView.scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
            webView.isScrollbarFadingEnabled = false
        }
    }

    private fun initYouTubePlayerView(videoUrl: String) {
        binding.includedLayout.youtubePlayerView.visibility = View.VISIBLE
        binding.includedLayout.webView.visibility = View.GONE
        binding.includedLayout.youtubePlayerView.initialize(object :
            AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                youTubePlayer.loadVideo(videoUrl.getVideoId()!!, 0F)
            }

            override fun onError(
                youTubePlayer: YouTubePlayer,
                error: PlayerConstants.PlayerError
            ) {
                youTubePlayer.loadVideo(videoUrl.getVideoId()!!, 0F)
            }
        })
    }

    fun displayImage(videoUrl: String?,view: ImageView?)
    {
        Glide.with(this@ArticleDetailsActivity)
            .asBitmap()
            .load(videoUrl)
            .error(R.drawable.place_holder)
            .into(view!!)
    }

    private fun validateOneValue(first: String?, second: String?): String {
        return when {
            !first.isNullOrEmpty() && first.isValidUrl() -> first.isValidURL()
            !second.isNullOrEmpty() && second.isValidUrl() -> second.isValidURL()
            else -> "null"
        }
    }

    private fun validateOneString(first: String?, second: String?): String {
        return first ?: second ?: ""
    }

    override fun getViewBinding(): ActivityArticleDetailsBinding =
        ActivityArticleDetailsBinding.inflate(layoutInflater)

    override fun getViewModel(): Class<MainViewModel> = MainViewModel::class.java
    override fun getViewModelStoreOwner(): ArticleDetailsActivity = this
    override fun getContext(): Context = this

}