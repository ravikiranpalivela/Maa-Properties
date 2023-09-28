package com.tekskills.sampleapp.ui.base

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import com.tekskills.sampleapp.data.local.ArticlesAllNews
import com.tekskills.sampleapp.data.local.ArticlesDatabase
import com.tekskills.sampleapp.data.prefrences.SharedPrefManager
import com.tekskills.sampleapp.databinding.ItemArticleViewtypeListBinding
import com.tekskills.sampleapp.model.BannerItem
import com.tekskills.sampleapp.model.NewsItem
import com.tekskills.sampleapp.ui.adapter.NewsAdapter
import com.tekskills.sampleapp.ui.main.MainViewModel
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

    fun bind(
        article: NewsItem,
        onClickListener: NewsAdapter.OnClickListener,
    ) {
        // Get the view count using the ViewModel
        val sharedPrefManager: SharedPrefManager = SharedPrefManager.getInstance(activity)

        binding.apply {
            clArticleView.visibility = View.VISIBLE
            clArticleAdView.visibility = View.GONE

            getBannerInfo(sharedPrefManager.getBannerDetailsData(),sharedPrefManager.bannerSelect)
            getArticleInfo(article.newsId)

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

            comments.text = article.comments.size.toString()

            article.scheduleDate?.let {
                publishedAt.text = "Posted date : ${changeDateFormat(it)}"
            }

            titleArticle.setOnClickListener {
                onClickListener.clickListener(article, articleImage)
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

//            val BANNER_SAMPLE =
//                "https://news.maaproperties.com/assets/img/ads-img/Maproperty_Banner.gif"
//            displayImage(BANNER_SAMPLE, ivBannerShare)

            ivBannerShare.visibility = View.GONE
            ivBannerLogo.visibility = View.GONE

            ivShare.setOnClickListener {
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
                        updateShareCount(article.newsId, article)
                    }

                    override fun onTick(millisUntilFinished: Long) {}
                }.start()
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
        onClickListener: NewsAdapter.OnClickListener,
    ) {
        val sharedPrefManager: SharedPrefManager = SharedPrefManager.getInstance(activity)

        binding.apply {
            clArticleAdView.visibility = View.VISIBLE
            clArticleView.visibility = View.GONE

            getBannerAdsInfo(sharedPrefManager.getBannerDetailsData(),sharedPrefManager.bannerAdsSelect)

            sharedPrefManager.saveBannerAdsSelect()

        }
    }

    fun updateViewCount(articleId: Int, article: NewsItem) {

        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            // Update the view count in the Room database
            val database: ArticlesDatabase = ArticlesDatabase.getInstance(context = activity)
            val dao = database.dao
            val commentsDao = database.commentDao
            val existingArticle = dao.getArticlesById(articleId)
            if (existingArticle != null) {
                // Update the existing article
                var count = existingArticle.view_count + 1
                dao.incrementViewCount(existingArticle.news_id, count)
            } else {
                val insertArticle = ArticlesAllNews(news_id = articleId, view_count = 1)
                // Insert a new article
                dao.insertArticles(insertArticle)
            }

            // Fetch the updated data from the database
            activity.runOnUiThread(Runnable {
                val commentData = commentsDao.getCommentsForItem(article.newsId)
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

    fun updateShareCount(articleId: Int, article: NewsItem) {

        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            // Update the view count in the Room database
            val database: ArticlesDatabase = ArticlesDatabase.getInstance(context = activity)
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
            activity.runOnUiThread(Runnable {
                val commentData = commentsDao.getCommentsForItem(article.newsId)
                val updatedData = dao.getArticlesById(articleId)
                binding.apply {
                    likes.text = updatedData?.view_count.toString()
                    shares.text = updatedData?.share_count.toString()
                    Log.d("TAG", "comments data ${commentData.toString()}")
//                    comments.text =
//                        if (commentData.isEmpty()) "0" else commentData.size.toString()
                }
            })
//            database.close()
        }

    }

    private fun getArticleInfo(newsId: Int) {

        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            // Update the view count in the Room database
            val database: ArticlesDatabase = ArticlesDatabase.getInstance(context = activity)
            val dao = database.dao
            val commentsDao = database.commentDao

            // Fetch the updated data from the database
            activity.runOnUiThread(Runnable {
                val commentData = commentsDao.getCommentsForItem(newsId)
                val updatedData = dao.getArticlesById(newsId)
                binding.apply {
//                    comments.text =
//                        if (commentData.isEmpty()) "0" else commentData.size.toString()
                    if (updatedData != null) {
//                        likes.text = updatedData.view_count.toString()
                        shares.text = updatedData.share_count.toString()
                    }
                    Log.d("TAG", "comments data ${commentData.toString()}")
                }
            })
//            database.close()
        }
    }

    private fun getBannerInfo(banners: BannerItem?, bannerSelect: Int) {
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {

            // Fetch the updated data from the database
            activity.runOnUiThread(Runnable {
                if(banners != null)
                binding.apply {
                    var count = bannerSelect + 1
                    if (count < banners.size) {
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
                        val BANNER_SAMPLE =
                            "https://news.maaproperties.com/assets/img/ads-img/Maproperty_Banner.gif"
                        displayImage(BANNER_SAMPLE, ivBannerShare)
                    }
                }
            })
//            database.close()
        }
    }

    private fun getBannerAdsInfo(banners: BannerItem?,bannerSelect: Int) {
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            // Update the view count in the Room database

            // Fetch the updated data from the database
            activity.runOnUiThread(Runnable {
                if(banners != null)
                binding.apply {
                    var count = bannerSelect + 1
                    if (count < banners.size) {
                        val updatedData = banners[count].link
                        displayImage(updatedData, ivBannerAds)
                    } else if (count > banners.size) {
                        val num = count % banners.size
                        val updatedData = banners[num].link
                        displayImage(updatedData, ivBannerAds)
                    } else if (banners.isNotEmpty()) {
                        val updatedData = banners[0].link
                        displayImage(updatedData, ivBannerAds)
                    } else {
                        val BANNER_SAMPLE =
                            "https://news.maaproperties.com/assets/img/ads-img/Maproperty_Banner.gif"
                        displayImage(BANNER_SAMPLE, ivBannerAds)
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
        Glide.with(activity)
            .asBitmap()
            .load(videoUrl)
            .error(R.drawable.place_holder)
            .into(view!!)
    }

    private fun validateUrlValue(first: String?): String {
        return when {
            !first.isNullOrEmpty() && first != "null" && first.isValidUrl() -> first.isValidURL()
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