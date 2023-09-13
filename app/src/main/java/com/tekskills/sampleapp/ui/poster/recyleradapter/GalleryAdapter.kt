package com.tekskills.sampleapp.ui.poster.recyleradapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.tekskills.sampleapp.R
import com.tekskills.sampleapp.ui.poster.helper.GalleryCollection

class GalleryAdapter(
    private val galleryList: ArrayList<GalleryCollection?>?,
    private val listener: (Any) -> Unit
) : RecyclerView.Adapter<GalleryAdapter.MyViewHolder>() {
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imageView: ImageView

        init {
            imageView = view.findViewById(R.id.gallery_recycler_image)
        }

        fun bind(item: GalleryCollection?, position: Int, listener: OnItemClickListener) {
            imageView.setImageBitmap(BitmapFactory.decodeFile(galleryList!![position]!!.imagePath))
            itemView.setOnClickListener { listener.onItemClick(position) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.gallery_recycler_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(galleryList!![position], position, listener as OnItemClickListener)
    }

    override fun getItemCount(): Int {
        return galleryList!!.size
    }
}