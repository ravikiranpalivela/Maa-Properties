package com.tekskills.sampleapp.ui.poster.recyleradapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.tekskills.sampleapp.R
import java.io.IOException
import java.io.InputStream

class EditorAdapterString(
    private val mContext: Context,
    private val list: ArrayList<String?>?,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<EditorAdapterString.MyViewHolder>() {
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imageView: ImageView

        init {
            imageView = view.findViewById(R.id.editor_recycler_image)
        }

        fun bind(item: String?, position: Int, listener: OnItemClickListener) {
            var inputstream: InputStream? = null
            try {
                inputstream = mContext.assets.open("stickers/" + list!![position])
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val drawable = Drawable.createFromStream(inputstream, null)
            imageView.setImageDrawable(drawable)
            itemView.setOnClickListener { listener.onItemClick(position) }
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

    override fun getItemCount(): Int {
        return list!!.size
    }
}