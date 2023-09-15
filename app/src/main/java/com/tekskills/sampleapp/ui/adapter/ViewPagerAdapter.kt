package com.tekskills.sampleapp.ui.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.tekskills.sampleapp.R
import com.tekskills.sampleapp.ui.articledetails.ArticlesFragment
import com.tekskills.sampleapp.ui.articledetails.ShortsFragment
import com.tekskills.sampleapp.ui.articledetails.PosterEditorFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity, val context: Context) :
    FragmentStateAdapter(fragmentActivity) {

    val tabTitles: Array<String> = context.resources.getStringArray(R.array.intro_title_list)

    override fun getItemCount(): Int = tabTitles.size

    override fun createFragment(position: Int): Fragment {
        return when {
            tabTitles[position] == "Posters" -> PosterEditorFragment()
            tabTitles[position] == "All" -> ArticlesFragment()
            tabTitles[position] == "News" -> ArticlesFragment()
            tabTitles[position] == "Wishes" -> ArticlesFragment()
            else -> ShortsFragment()
        }
    }
}