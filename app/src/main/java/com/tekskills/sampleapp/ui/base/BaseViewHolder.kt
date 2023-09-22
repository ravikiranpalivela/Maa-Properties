package com.tekskills.sampleapp.ui.base

import android.app.Activity
import android.os.CountDownTimer
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import com.tekskills.sampleapp.R
import com.tekskills.sampleapp.databinding.ItemArticleViewtypeListBinding
import com.tekskills.sampleapp.model.NewsItem
import com.tekskills.sampleapp.utils.video.changeDateFormat
import com.tekskills.sampleapp.utils.video.getThumbnail
import com.tekskills.sampleapp.utils.video.getVideoId
import com.tekskills.sampleapp.utils.video.isValidURL
import com.tekskills.sampleapp.utils.video.isValidUrl
import com.tekskills.sampleapp.utils.video.isYoutubeUrl

abstract class BaseViewHolder<viewDataBinding : ViewDataBinding>(
    private val activity: Activity,
    private val bindingView: viewDataBinding,
    private val lifecycle: Lifecycle
) :
    RecyclerView.ViewHolder(bindingView.root) {

    protected abstract val binding: ItemArticleViewtypeListBinding
    private var playbackPosition = 0L
    private var playWhenReady = true
    private var initializedYouTubePlayer: YouTubePlayer? = null

    fun bind(
        article: NewsItem,
        clickListener: (NewsItem, ImageView) -> Unit,
        longClickListener: (NewsItem, ImageView) -> Unit,
        shareClickListener: (NewsItem, View) -> Unit
    ) {

        binding.apply {
            if (article.description.isNullOrEmpty() && article.videoDescription.isNullOrEmpty())
                descArticle.visibility = View.GONE
            else
                descArticle.visibility = View.VISIBLE

            if (article.title.isNullOrEmpty())
                titleArticle.visibility = View.GONE
            else
                titleArticle.visibility = View.VISIBLE

            titleArticle.text = article.title
            descArticle.text = validateOneString(article.description, article.videoDescription)

            article.scheduleDate?.let {
                publishedAt.text = "Posted date : ${changeDateFormat(it)}"
            }

            titleArticle.setOnClickListener {
                clickListener(article, articleImage)
            }

            readMoreArticle.setOnClickListener {
                longClickListener(article, articleImage)
                true
            }

            val BANNER_SAMPLE =
                "https://news.maaproperties.com/assets/img/ads-img/Maproperty_Banner.gif"
            displayImage(BANNER_SAMPLE,ivBannerShare)

            ivBannerShare.visibility = View.GONE
            ivBannerLogo.visibility = View.GONE

            ivShare.setOnClickListener {
                youtubePlayerView.visibility = View.GONE
                webView.visibility = View.GONE
                articleImage.visibility = View.VISIBLE
                btnPlay.visibility = View.VISIBLE
                ivBannerShare.visibility = View.VISIBLE
                ivBannerLogo.visibility = View.VISIBLE
                object : CountDownTimer(200, 100) {
                    override fun onFinish() {
                        shareClickListener(article, clArticleView)
                        ivBannerShare.visibility = View.GONE
                        ivBannerLogo.visibility = View.GONE
                    }

                    override fun onTick(millisUntilFinished: Long) {}
                }.start()
            }

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
        binding.apply {
            articleImage.visibility = View.VISIBLE
            clVideoPlayer.visibility = View.VISIBLE

            btnPlay.visibility = View.VISIBLE
            displayImage(videoUrl.getThumbnail(),articleImage)
            btnPlay.setOnClickListener {
                if (videoUrl.isYoutubeUrl()) initYouTubePlayerView(videoUrl)
                else initPlayPathUrlPlayerView(videoUrl)
//                articleImage.visibility = View.GONE
//                btnPlay.visibility = View.GONE
            }
        }
    }

    private fun playPathUrlVideo(videoUrl: String) {
        binding.apply {
            articleImage.visibility = View.VISIBLE
            clVideoPlayer.visibility = View.VISIBLE
            btnPlay.visibility = View.VISIBLE
            displayImage(videoUrl,articleImage)

            btnPlay.setOnClickListener { view ->
                if (videoUrl.isYoutubeUrl()) initYouTubePlayerView(videoUrl)
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
                "<meta name=\"viewport\" content='width=device-width, initial-scale=1.0,text/html,charset=utf-8' ><html><body><video id='myVideo' width='auto' height='auto' controls autoplay><source src=${videoUrl} type='video/mp4'></video></body></html>"

            binding.webView.settings.javaScriptEnabled = true
            binding.webView.webChromeClient = object : WebChromeClient() {
            }

            binding.webView.addJavascriptInterface(object : Any() {
                @JavascriptInterface
                fun performClick(value: String) {
                    Toast.makeText(activity, "clicked $value", Toast.LENGTH_SHORT).show()
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

    private fun initYouTubePlayerView(videoUrl: String) {
        if(binding.youtubePlayerView != null) {
            binding.youtubePlayerView?.initialize(object :
                AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    binding.articleImage.visibility = View.GONE
                    binding.btnPlay.visibility = View.GONE
                    binding.youtubePlayerView.visibility = View.VISIBLE
                    binding.webView.visibility = View.GONE
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
        else
        {
            binding.youtubePlayerView.getYouTubePlayerWhenReady(object :YouTubePlayerCallback{
                override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
                    binding.articleImage.visibility = View.GONE
                    binding.btnPlay.visibility = View.GONE
                    binding.youtubePlayerView.visibility = View.VISIBLE
                    binding.webView.visibility = View.GONE
                    youTubePlayer.loadVideo(videoUrl.getVideoId()!!, 0F)
                }
            })
        }


    }

    fun displayImage(videoUrl: String?,view: ImageView?)
    {
        Glide.with(activity)
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

    companion object{

    }
}