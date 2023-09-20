package com.tekskills.sampleapp.ui.articleDetails

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityOptionsCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.transition.MaterialFadeThrough
import com.tekskills.sampleapp.R
import com.tekskills.sampleapp.data.local.BannerItemRepository
import com.tekskills.sampleapp.data.local.BannersDatabase
import com.tekskills.sampleapp.data.local.BookmarksDatabase
import com.tekskills.sampleapp.data.local.BookmarksRepository
import com.tekskills.sampleapp.data.prefrences.AppPreferences
import com.tekskills.sampleapp.databinding.FragmentArticlesBinding
import com.tekskills.sampleapp.model.AllNewsItem
import com.tekskills.sampleapp.ui.adapter.ShortsAdapter
import com.tekskills.sampleapp.ui.main.MainActivity
import com.tekskills.sampleapp.ui.main.MainViewModel
import com.tekskills.sampleapp.ui.main.MainViewModelFactory
import com.tekskills.sampleapp.utils.ObjectSerializer
import com.tekskills.sampleapp.utils.ShareLayout

class ShortsFragment : Fragment() {

    lateinit var viewModel: MainViewModel
    lateinit var binding: FragmentArticlesBinding
    private lateinit var newsListAdapter: ShortsAdapter
    private lateinit var preferences: AppPreferences

    var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_articles, container, false)

        preferences =
            AppPreferences(requireContext())

        val database: BookmarksDatabase = BookmarksDatabase.getInstance(context = requireContext())
        val bannerDatabase: BannersDatabase = BannersDatabase.getInstance(context = requireContext())

        val dao = database.dao
        val bannerDao = bannerDatabase.bannerDao
        val repository = BookmarksRepository(dao)
        val bannerRepo = BannerItemRepository(bannerDao)
        val factory = MainViewModelFactory(repository,bannerRepo,preferences)
        viewModel = ViewModelProvider(requireActivity(), factory).get(MainViewModel::class.java)

        binding.viewModel = viewModel

        defineViews()
        enterTransition = MaterialFadeThrough()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handle the back button press
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            showExitConfirmationDialog()
        }
    }

    private fun showExitConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Exit Application")
        builder.setMessage("Are you sure you want to exit the application?")
        builder.setPositiveButton("Yes") { _, _ ->
            // User clicked Yes, so exit the application
            requireActivity().finish()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            // User clicked No, so dismiss the dialog
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    @SuppressLint("ClickableViewAccessibility")
    fun defineViews() {

        initAdapter()

        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            if (!isLoading) {
                if (binding.swipeRefreshLayout.isRefreshing) binding.swipeRefreshLayout.isRefreshing =
                    false

                binding.pager.visibility = View.VISIBLE

                val context = binding.pager.context
                val layoutAnimationController =
                    AnimationUtils.loadLayoutAnimation(context, R.anim.layout_down_to_up)
                binding.pager.layoutAnimation = layoutAnimationController
                displayArticles()

            } else {
                if (!binding.swipeRefreshLayout.isRefreshing) binding.swipeRefreshLayout.isRefreshing =
                    true
                binding.pager.visibility = View.INVISIBLE
            }
        })

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshResponse()
        }
    }

    private fun displayArticles() {

        viewModel.responseLiveData.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                it.body()?.let { news ->
                    news
//                        .filter { it.newsId == 772 }
                        .sortedByDescending { sort -> sort.newsId }
                        .let { articles ->
                            isLoading = true
                            newsListAdapter.setArticleList(articles)
                            isLoading = true
                        }
                }
            }
        })
    }

    private fun goToArticleDetailActivity(article: AllNewsItem, imageView: ImageView) {
        val intent = Intent(requireContext(), ArticleDetailsActivity::class.java)
        intent.putExtra("article", ObjectSerializer.serialize(article))
        val activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
            requireActivity(),
            imageView,
            "article_image"
        )
        intent.putExtra("fab_visiblity", View.VISIBLE)
        startActivity(intent, activityOptions.toBundle())
    }

    private fun initAdapter() {

        newsListAdapter =
            ShortsAdapter(
                requireActivity(),
                lifecycle,
                { article: AllNewsItem, imageView: ImageView ->
                    goToArticleDetailActivity(article, imageView)
                }, { article: AllNewsItem, imageView: ImageView ->
                    (activity as MainActivity?)!!.appBarLayoutHandle(true)
                },
                { article: AllNewsItem, imageView: ImageView ->

                    if (!article.websiteUrl.isNullOrEmpty()) {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(article.websiteUrl)
                        )

                        intent.putExtra("article", ObjectSerializer.serialize(article))
                        val activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            requireActivity(),
                            imageView,
                            "article_image"
                        )
                        intent.putExtra("fab_visiblity", View.VISIBLE)
                        val chooseIntent = Intent.createChooser(intent, "Choose from below")
                        startActivity(chooseIntent, activityOptions.toBundle())
//                        startActivity(intent, activityOptions.toBundle())
                    }

                },
                { article, itemView ->
                    val activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        requireActivity(),
                        itemView,
                        "article_image"
                    )
                    if (!article.websiteUrl.isNullOrEmpty() && article.websiteUrl != "null") {
                        val html =
                            "${article.title} \n\nFull article at : ${article.websiteUrl}>${article.websiteUrl}"
                        ShareLayout.simpleLayoutShare(
                            requireContext(),
                            itemView,
                            html,
                            activityOptions
                        )
                    } else
                        ShareLayout.simpleLayoutShare(
                            requireContext(),
                            itemView,
                            article.title,
                            activityOptions
                        )
                })

        binding.pager.orientation = ViewPager2.ORIENTATION_VERTICAL
        binding.pager.registerOnPageChangeCallback(onPageChangeCallback)

        viewModel.appPreferences.viewtype.asLiveData()
            .observe(viewLifecycleOwner, Observer { viewType ->
                binding.pager.adapter = newsListAdapter
            })

        binding.pager.setPageTransformer(CardTransformer(1.2f))

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


    class CardTransformer(scalingStart: Float) : ViewPager2.PageTransformer {
        private val scalingStart: Float

        init {
            this.scalingStart = 1 - scalingStart
        }

        override fun transformPage(page: View, position: Float) {
            if (position >= 0) {
                val w = page.width
                val scaleFactor = 1 - scalingStart * position
                page.alpha = 1 - position
                page.scaleX = scaleFactor
                page.scaleY = scaleFactor
                page.translationY = w * (1 - position) - w
                page.translationZ = w * (2 - position) - w

            }
        }
    }
}

