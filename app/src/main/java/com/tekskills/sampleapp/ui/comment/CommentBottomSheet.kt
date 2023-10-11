package com.tekskills.sampleapp.ui.comment

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tekskills.sampleapp.R
import com.tekskills.sampleapp.data.prefrences.SharedPrefManager
import com.tekskills.sampleapp.data.repo.ArticleProviderRepo
import com.tekskills.sampleapp.databinding.BottomSheetFeedbackBinding
import com.tekskills.sampleapp.model.Comment
import com.tekskills.sampleapp.model.CommentDetails
import com.tekskills.sampleapp.ui.adapter.CommentsListAdapter
import com.tekskills.sampleapp.ui.main.MainViewModel
import com.tekskills.sampleapp.ui.main.MainViewModelFactory
import java.util.concurrent.Executors

class CommentBottomSheet : BottomSheetDialogFragment() {

    private val binding by viewBinding(BottomSheetFeedbackBinding::bind)
    lateinit var viewModel: MainViewModel
    private lateinit var preferences: SharedPrefManager

    private var article_id = 0

    private lateinit var adapter: CommentsListAdapter
    private val genresList = CommentDetails()
    private lateinit var comments: CommentDetails

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
        val args: CommentBottomSheetArgs by navArgs()
        comments = args.comments
        article_id = args.articleId

        preferences =
            SharedPrefManager.getInstance(requireContext())

        val repository = ArticleProviderRepo()

        val factory = MainViewModelFactory(repository, preferences)
        viewModel = ViewModelProvider(requireActivity(), factory)[MainViewModel::class.java]

        Log.d("TAG", "article id ${article_id} and comments ${comments}")
        return inflater.inflate(R.layout.bottom_sheet_feedback, container, false)
    }

    private fun setUpGenresAdapter(comments: CommentDetails) {
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {

            adapter =
                CommentsListAdapter(requireContext()) { genresDateModel: Comment, position: Int ->
                }

            binding.rvComments.adapter = adapter

            requireActivity().runOnUiThread(Runnable {
                setUpGenresData(comments)
            })
        }
    }

    private fun setUpGenresData(genresDetails: CommentDetails) {
        genresList.clear()
        genresList.addAll(genresDetails)
        adapter.setData(comments)

        Log.d("TAG", "comments data ${genresList.toString()}")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            val args: CommentBottomSheetArgs by navArgs()
            comments = args.comments
            article_id = args.articleId
//            article_id = savedInstanceState.getInt("article_id")
            Log.d("TAG", "article id ${article_id}")
        }
        setUpGenresAdapter(comments)

        binding.userInput.setOnEditorActionListener { _, i, _ ->
            if (i == EditorInfo.IME_ACTION_DONE) {
                dismiss()
            }
            false
        }
        binding.doneButton.setOnClickListener {
            if (!binding.userInput.text.isNullOrEmpty()) {
                if (article_id != 0)
                    viewModel.postComments(article_id.toString(), binding.userInput.text.toString())
            }
            dismiss()
        }
        binding.userInput.imeOptions = EditorInfo.IME_ACTION_DONE
        binding.userInput.setRawInputType(InputType.TYPE_CLASS_TEXT)
    }

    override fun getTheme(): Int {
        return R.style.FeedbackBottomSheetDialog
    }

    companion object {
        val TAG = CommentBottomSheet::class.java.simpleName
    }
}