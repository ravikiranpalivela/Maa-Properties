package com.tekskills.sampleapp.ui.comment

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tekskills.sampleapp.R
import com.tekskills.sampleapp.data.local.ArticlesDatabase
import com.tekskills.sampleapp.data.local.CommentDetails
import com.tekskills.sampleapp.data.local.CommentItem
import com.tekskills.sampleapp.databinding.BottomSheetFeedbackBinding
import com.tekskills.sampleapp.ui.adapter.CommentsListAdapter
import java.util.concurrent.Executors

class CommentBottomSheet : BottomSheetDialogFragment() {

    private val binding by viewBinding(BottomSheetFeedbackBinding::bind)

    private var article_id = 0
    private lateinit var adapter: CommentsListAdapter
    private val genresList = CommentDetails()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.setOnShowListener {
            val d = it as BottomSheetDialog
            val bottomSheetInternal = d.findViewById<View>(R.id.design_bottom_sheet)
            if (bottomSheetInternal != null) {
                BottomSheetBehavior.from(bottomSheetInternal).state =
                    BottomSheetBehavior.STATE_EXPANDED
            }
        }
        article_id = arguments?.getInt("article_id", 0)!!
        Log.d("TAG", "article id ${article_id}")
        return inflater.inflate(R.layout.bottom_sheet_feedback, container, false)
    }

    private fun setUpGenresAdapter() {
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {


            // Update the view count in the Room database
            val database: ArticlesDatabase =
                ArticlesDatabase.getInstance(context = requireContext())
            val dao = database.commentDao

            adapter =
                CommentsListAdapter(requireContext()) { genresDateModel: CommentItem, position: Int ->

                }
            binding.rvComments.adapter = adapter

            requireActivity().runOnUiThread(Runnable {
                setUpGenresData(dao.getCommentsForItem(article_id))
            })
        }
    }

    private fun setUpGenresData(genresDetails: List<CommentItem>) {
        genresList.clear()
        genresList.addAll(genresDetails)
        adapter.setData(genresList)
        Log.d("TAG", "comments data ${genresList.toString()}")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            article_id = savedInstanceState.getInt("article_id")
            Log.d("TAG", "article id ${article_id}")
        }
        setUpGenresAdapter()
        binding.userInput.setOnEditorActionListener { _, i, _ ->
            if (i == EditorInfo.IME_ACTION_DONE) {
                dismiss()
            }
            false
        }
        binding.doneButton.setOnClickListener {
            if (!binding.userInput.text.isNullOrEmpty()) {
                if (article_id != 0)
                    updateViewCount(article_id, binding.userInput.text.toString())
            }
            dismiss()
        }
        binding.userInput.imeOptions = EditorInfo.IME_ACTION_DONE
        binding.userInput.setRawInputType(InputType.TYPE_CLASS_TEXT)

    }

    fun updateViewCount(articleId: Int, article: String) {

        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            // Update the view count in the Room database
            val database: ArticlesDatabase =
                ArticlesDatabase.getInstance(context = requireContext())
            val dao = database.commentDao

            dao.insertComment(
                CommentItem(
                    itemId = articleId,
                    text = article,
                    lastUpdate = System.currentTimeMillis()
                )
            )

//            setUpGenresData(dao.getCommentsForItem(article_id))

            requireActivity().runOnUiThread(Runnable {
                setUpGenresData(dao.getCommentsForItem(articleId))
//                binding.apply {
//                    likes.text = updatedData?.view_count.toString()
//                }
            })
//            database.close()
        }
    }

    override fun getTheme(): Int {
        return R.style.FeedbackBottomSheetDialog
    }

    companion object {
        val TAG = CommentBottomSheet::class.java.simpleName
    }
}