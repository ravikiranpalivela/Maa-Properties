package com.tekskills.sampleapp.ui.poster.recyleradapter

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.tekskills.sampleapp.R
import com.tekskills.sampleapp.ui.poster.helper.GalleryCollection

class SlidingImage_Adapter(
    private val context: Context,
    private val IMAGES: ArrayList<GalleryCollection>
) : PagerAdapter() {
    private val inflater: LayoutInflater

    init {
        inflater = LayoutInflater.from(context)
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount(): Int {
        return IMAGES.size
    }

    override fun instantiateItem(view: ViewGroup, position: Int): Any {
        val imageLayout = inflater.inflate(
            R.layout.slidingimages_layout,
            view,
            false
        )!!
        val imageView = imageLayout.findViewById<ImageView>(R.id.full_imgae_for_gallery_pager)
        imageView.setImageBitmap(BitmapFactory.decodeFile(IMAGES[position].imagePath))
        view.addView(imageLayout, 0)
        return imageLayout
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {}
    override fun saveState(): Parcelable? {
        return null
    }
}