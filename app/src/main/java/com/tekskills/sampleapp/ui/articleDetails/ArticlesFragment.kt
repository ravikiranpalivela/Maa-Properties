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
import com.tekskills.sampleapp.ui.adapter.NewsAdapter
import com.tekskills.sampleapp.ui.comment.CommentBottomSheet
import com.tekskills.sampleapp.ui.main.MainActivity
import com.tekskills.sampleapp.ui.main.MainViewModel
import com.tekskills.sampleapp.ui.main.MainViewModelFactory
import com.tekskills.sampleapp.utils.ObjectSerializer
import com.tekskills.sampleapp.utils.ShareLayout


class ArticlesFragment : Fragment() {

    lateinit var viewModel: MainViewModel
    lateinit var binding: FragmentArticlesBinding
    private lateinit var newsListAdapter: NewsAdapter
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
        val bannerDatabase: BannersDatabase =
            BannersDatabase.getInstance(context = requireContext())

        val dao = database.dao
        val repository = BookmarksRepository(dao)
        val articleDao = bannerDatabase.bannerDao
        val bannerRepo = BannerItemRepository(articleDao)
        val factory = MainViewModelFactory(repository, bannerRepo, preferences)
        viewModel = ViewModelProvider(requireActivity(), factory)[MainViewModel::class.java]

        binding.viewModel = viewModel

        defineViews()
        enterTransition = MaterialFadeThrough()
        return binding.root
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // handle result of pick image chooser
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("TAG", "onActivityResult response ${resultCode} ${data.toString()}")
        val TEXT_FONT_COLOR = 856

//        if (resultCode == AppCompatActivity.RESULT_OK) {
//            if (requestCode == TEXT_FONT_COLOR) {
//                try {
//                    val del = DocumentsContract.deleteDocument(
//                        requireContext().contentResolver,
//                        data?.data!!
//                    )
//                    Log.d("myLogs", "del = $del")
//                } catch (e: FileNotFoundException) {
//                    e.printStackTrace()
//                }
//            }
//        }
    }


    private fun goToArticleDetailActivity(article: AllNewsItem, imageView: ImageView) {
        val intent = Intent(requireContext(), ArticleDetailsActivity::class.java)
        intent.putExtra("article", ObjectSerializer.serialize(article))
        val activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
            requireActivity(),
            imageView,
            "article_image"
        )
        intent.putExtra("fab_visibility", View.VISIBLE)
        startActivity(intent, activityOptions.toBundle())
    }

    private fun initAdapter() {
        newsListAdapter =
            NewsAdapter(
                requireActivity(),
                viewModel,
                lifecycle,
                object : NewsAdapter.OnClickListener {
                    override fun clickListener(allNewsItem: AllNewsItem, imageView: ImageView) {
                        goToArticleDetailActivity(allNewsItem, imageView)
                    }

                    override fun doubleClickListener(
                        allNewsItem: AllNewsItem,
                        imageView: ImageView
                    ) {
                        (activity as MainActivity?)!!.appBarLayoutHandle(true)
                    }

                    override fun readMoreClickListener(
                        allNewsItem: AllNewsItem,
                        imageView: ImageView
                    ) {
                        if (!allNewsItem.websiteUrl.isNullOrEmpty()) {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(allNewsItem.websiteUrl)
                            )

                            intent.putExtra("article", ObjectSerializer.serialize(allNewsItem))
                            val activityOptions =
                                ActivityOptionsCompat.makeSceneTransitionAnimation(
                                    requireActivity(),
                                    imageView,
                                    "article_image"
                                )
                            intent.putExtra("fab_visibility", View.VISIBLE)
                            val chooseIntent = Intent.createChooser(intent, "Choose from below")
                            startActivity(chooseIntent, activityOptions.toBundle())
                        }
                    }

                    override fun shareClickListener(allNewsItem: AllNewsItem, imageView: View) {
                        val activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            requireActivity(),
                            imageView,
                            "article_image"
                        )
                        if (!allNewsItem.websiteUrl.isNullOrEmpty() && allNewsItem.websiteUrl != "null") {
                            val html =
                                "${allNewsItem.title} \n\nFull article at : ${allNewsItem.websiteUrl}>${allNewsItem.websiteUrl}"
                            ShareLayout.simpleLayoutShare(
                                requireActivity(),
                                imageView,
                                html,
                                activityOptions
                            )
                        } else
                            ShareLayout.simpleLayoutShare(
                                requireActivity(),
                                imageView,
                                allNewsItem.title,
                                activityOptions
                            )
                    }

                    override fun likeClickListener(allNewsItem: AllNewsItem, imageView: View) {
//                        viewModel.addABookmark(news_id = allNewsItem.newsId, article = allNewsItem)
                    }

                    override fun commentClickListener(allNewsItem: AllNewsItem, imageView: View) {
                        val bottomSheetFragment = CommentBottomSheet()
                        val bundle = Bundle()
                        bundle.putInt("article_id", allNewsItem.newsId)
                        bottomSheetFragment.arguments = bundle

                        bottomSheetFragment.show(
                            parentFragmentManager,
                            CommentBottomSheet.TAG
                        )
                    }
                })

        binding.pager.orientation = ViewPager2.ORIENTATION_VERTICAL
        binding.pager.registerOnPageChangeCallback(onPageChangeCallback)

//        viewModel.appPreferences.viewtype.asLiveData()
//            .observe(viewLifecycleOwner, Observer { viewType ->
        binding.pager.adapter = newsListAdapter
//            })

        binding.pager.setPageTransformer(CardTransformer(1.2f))

//        binding.pager.setOnClickListener(object : DoubleClickListener() {
//            override fun onDoubleClick(v: View) {
//                Log.d("Event", "action response double click")
//                (activity as MainActivity?)!!.appBarLayoutHandle(true)
////                Toast.makeText(context,"Double Clicked Attempts", Toast.LENGTH_SHORT).show()
//            }
//        })

//        binding.root.setOnClickListener(object : DoubleClickListener() {
//            override fun onDoubleClick(v: View) {
//                Log.d("Event", "action response root double click")
//                (activity as MainActivity?)!!.appBarLayoutHandle(true)
////                Toast.makeText(context,"Double Clicked Attempts", Toast.LENGTH_SHORT).show()
//            }
//        })

//        binding.pager.setOnScrollChangeListener { view, i, i2, i3, i4 ->
//            (activity as MainActivity?)!!.appBarLayoutHandle()
//
//        }

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

