package com.tekskills.sampleapp.ui.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.tekskills.sampleapp.R
import com.squareup.picasso.Picasso
import com.tekskills.sampleapp.model.PosterItemDetails

class ShortAdapter(
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<ShortAdapter.MyViewHolder>() {

    private val list = ArrayList<PosterItemDetails>()

    interface OnItemClickListener {
        fun onItemClick(itemView: View, item: PosterItemDetails)
        fun onUploadClick(itemView: View, item: PosterItemDetails)
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imageView: ImageView
        var btnUpload: Button
        var clPoster: ConstraintLayout

        init {
            imageView = view.findViewById(R.id.editor_recycler_image)
            btnUpload = view.findViewById(R.id.btn_upload)
            clPoster = view.findViewById(R.id.cl_poster)
        }

        fun bind(item: PosterItemDetails, position: Int, listener: OnItemClickListener) {
            Picasso.get().load(item.posterImagePath)
//                .resize(75, 75)
//                .onlyScaleDown()
                .placeholder(R.drawable.place_holder).into(imageView)
            //            imageView.setImageResource(item.getImageID());
//            itemView.setOnClickListener { listener.onItemClick(position,item) }

            btnUpload.setOnClickListener {
                listener.onUploadClick(clPoster,item)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.editor_recycler_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(list!![position], position, listener)
    }

    fun setEditorList(articles: ArrayList<PosterItemDetails>) {
        this.list!!.clear()
        this.list!!.addAll(articles)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return list!!.size
    }




    fun createImageWithWhiteBackground(frameImage: Bitmap): Bitmap {
        // Create a new bitmap with the same dimensions as the frame image
        val resultBitmap =
            Bitmap.createBitmap(frameImage.width, frameImage.height, Bitmap.Config.ARGB_8888)

        // Create a canvas to draw on the new bitmap
        val canvas = Canvas(resultBitmap)

        // Fill the canvas with a white background
        val paint = Paint()
        paint.color = Color.WHITE
        canvas.drawRect(0f, 0f, resultBitmap.width.toFloat(), resultBitmap.height.toFloat(), paint)

        // Draw the frame image on top of the white background
        canvas.drawBitmap(frameImage, 0f, 0f, null)

        return resultBitmap
    }
}