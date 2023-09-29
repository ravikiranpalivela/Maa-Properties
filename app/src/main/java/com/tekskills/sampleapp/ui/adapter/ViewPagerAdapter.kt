package com.tekskills.sampleapp.ui.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.tekskills.sampleapp.R
import com.tekskills.sampleapp.ui.articleDetails.AllNewsDetailsFragment
import com.tekskills.sampleapp.ui.articleDetails.NewsDetailsFragment
import com.tekskills.sampleapp.ui.articleDetails.ShortsFragment
import com.tekskills.sampleapp.ui.articleDetails.PosterEditorFragment
import com.tekskills.sampleapp.ui.articleDetails.WishesDetailsFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity, val context: Context) :
    FragmentStateAdapter(fragmentActivity) {

    val tabTitles: Array<String> = context.resources.getStringArray(R.array.intro_title_list)

    override fun getItemCount(): Int = tabTitles.size

    override fun createFragment(position: Int): Fragment {
        return when {
            tabTitles[position] == "Posters" -> PosterEditorFragment()
            tabTitles[position] == "All" -> AllNewsDetailsFragment()
            tabTitles[position] == "News" -> NewsDetailsFragment()
            tabTitles[position] == "Wishes" -> WishesDetailsFragment()
            else -> ShortsFragment()
        }
    }
}