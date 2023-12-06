package com.tekskills.sampleapp.ui.base

import android.app.Activity
import android.os.CountDownTimer
import android.util.Log
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
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.tekskills.sampleapp.R
import com.tekskills.sampleapp.data.prefrences.SharedPrefManager
import com.tekskills.sampleapp.databinding.ItemArticleViewtypeListBinding
import com.tekskills.sampleapp.model.BannerItem
import com.tekskills.sampleapp.model.NewsItem
import com.tekskills.sampleapp.model.PublicAdsDetails
import com.tekskills.sampleapp.ui.adapter.ShortsAdapter
import com.tekskills.sampleapp.ui.main.MainActivity
import com.tekskills.sampleapp.utils.AppConstant.ADS_IMAGE_URL
import com.tekskills.sampleapp.utils.AppUtil
import com.tekskills.sampleapp.utils.like.LikeButton
import com.tekskills.sampleapp.utils.like.OnAnimationEndListener
import com.tekskills.sampleapp.utils.like.OnLikeListener
import com.tekskills.sampleapp.utils.video.changeDateFormat
import com.tekskills.sampleapp.utils.video.getThumbnail
import com.tekskills.sampleapp.utils.video.isValidURL
import com.tekskills.sampleapp.utils.video.isValidUrl
import com.tekskills.sampleapp.utils.video.isYoutubeUrl
import java.util.concurrent.Executors

abstract class ShortsBaseViewHolder<viewDataBinding : ViewDataBinding>(
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
        onClickListener: ShortsAdapter.OnClickListener
    ) {
        val sharedPrefManager: SharedPrefManager = SharedPrefManager.getInstance(activity)

        binding.apply {
            getBannerInfo(sharedPrefManager.getBannerDetailsData(), sharedPrefManager.bannerSelect)

            if (validateValue(article.videoDescription) == "null")
                descArticle.visibility = View.GONE
            else {
                descArticle.text = article.videoDescription
                descArticle.visibility = View.VISIBLE
            }

            if (validateValue(article.title) == "null")
                titleArticle.visibility = View.GONE
            else {
                titleArticle.visibility = View.VISIBLE
                titleArticle.text = article.title
            }

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
                true
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
//                    updateViewCount(article.id, article)
                }
            })

            comment.setOnClickListener {
                onClickListener.commentClickListener(article, articleImage)
            }

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
                object : CountDownTimer(200, 100) {
                    override fun onFinish() {
                        onClickListener.shareClickListener(article, clArticleView)
                        ivBannerShare.visibility = View.GONE
                        ivBannerLogo.visibility = View.GONE
                    }

                    override fun onTick(millisUntilFinished: Long) {}
                }.start()
            }

//            if (validateUrlValue(article.videoFilePath) != "null"
//                && validateUrlValue(article.videoUrl) != "null"
//            ) {
//                if (article.videoUrl.isValidUrl())
//                    playVideoUrlData(article.videoUrl)
//                else if (article.videoFilePath?.isValidUrl()!!)
//                    playVideoUrlData(article.videoFilePath!!)
//            } else if (validateUrlValue(article.videoFilePath) == "null"
//                && validateUrlValue(article.videoUrl) != "null"
//            ) {
//                if (article.videoUrl.isValidUrl())
//                    playVideoUrlData(article.videoUrl)
//            } else if (validateUrlValue(article.videoFilePath) != "null" && validateUrlValue(article.videoUrl) == "null") {
//                if (article.videoFilePath?.isValidUrl()!!)
//                    playPathUrlVideo(article.videoFilePath!!)
//            }


            if (validateUrlValue(article.videoPath) != "null"
                && validateUrlValue(article.videoPath) != "null"
                && validateUrlValue(article.videoUrl) != "null"
            ) {
                if (article.videoUrl.isValidUrl())
                    playVideoUrlData(article.videoUrl)
                else if (article.videoPath?.isValidUrl()!!)
                    playVideoUrlData(article.videoPath!!)
                else if (article.videoFilePath?.isValidUrl()!!)
                    playVideoUrlData(article.videoFilePath!!)
            } else if (validateUrlValue(article.videoPath) == "null"
                && validateUrlValue(article.videoFilePath) == "null"
                && validateUrlValue(article.videoUrl) != "null"
            ) {
                if (article.videoUrl.isValidUrl())
                    playVideoUrlData(article.videoUrl)
            }
            else if (validateUrlValue(article.videoPath) != "null"
                && validateUrlValue(article.videoFilePath) == "null"
                && validateUrlValue(article.videoUrl) == "null"
            ) {
                if (article.videoPath.isValidUrl()!!)
                    playPathUrlVideo(article.videoPath!!)
            }
            else if (validateUrlValue(article.videoFilePath) != "null"
                && validateUrlValue(article.videoPath) == "null"
                && validateUrlValue(article.videoUrl) == "null"
            ) {
                if (article.videoFilePath.isValidUrl()!!)
                    playPathUrlVideo(article.videoFilePath!!)
            }
        }
    }

    fun bindAdView(
        onClickListener: ShortsAdapter.OnClickListener,
    ) {
        val sharedPrefManager: SharedPrefManager = SharedPrefManager.getInstance(activity)

        binding.apply {
            clArticleAdView.visibility = View.VISIBLE
            clArticleView.visibility = View.GONE

            getBannerAdsInfo(
                sharedPrefManager.getPublicAdsDetailsData(),
                sharedPrefManager.publicAdsAdsSelect
            )

            sharedPrefManager.savePublicAdsAdsSelect()

        }
    }

    private fun getBannerAdsInfo(banners: PublicAdsDetails?, bannerSelect: Int) {
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            // Fetch the updated data from the database
            SharedPrefManager

            if (banners != null)
                activity.runOnUiThread(Runnable {
                    binding.apply {
                        var count = bannerSelect + 1
                        if (count < banners.size) {
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

    private fun getBannerInfo(banners: BannerItem?, bannerSelect: Int) {
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {

            if (banners != null)
                activity.runOnUiThread(Runnable {
                    binding.apply {
                        var count = bannerSelect + 1
                        if(banners.size == 0)
                        {
                            displayImage(ADS_IMAGE_URL, ivBannerShare)
                        }else if (count < banners.size) {
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


    private fun playVideoUrlData(videoUrl: String) {
        binding.apply {
            articleImage.visibility = View.GONE
            clVideoPlayer.visibility = View.VISIBLE

            btnPlay.visibility = View.GONE
            displayImage(videoUrl.getThumbnail(), articleImage)
//            btnPlay.setOnClickListener {
            if (videoUrl.isYoutubeUrl()) initYouTubePlayerView(videoUrl)
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
//            binding.webView.setInitialScale(1)

            binding.webView.settings.loadWithOverviewMode = true
            binding.webView.scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
            binding.webView.isScrollbarFadingEnabled = false
            articleImage.visibility = View.GONE
            btnPlay.visibility = View.GONE
        }
    }


    private fun initYouTubePlayerView(videoUrl: String) {
        initPlayUrlPlayerView(videoUrl)
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

    fun displayImage(videoUrl: String?, view: ImageView?) {
//        Glide.with(activity)
//            .asBitmap()
//            .load(videoUrl)
//            .error(R.drawable.place_holder)
//            .into(view!!)
        AppUtil.loadGlideImage(Glide.with(activity), videoUrl!!, view!!)
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


    companion object {

    }
}