package com.tekskills.sampleapp.ui.poster.recyleradapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.tekskills.sampleapp.R
import com.squareup.picasso.Picasso
import com.tekskills.sampleapp.model.AllNewsItem

class EditorAdapter(
    private val list: ArrayList<EditorCollection>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<EditorAdapter.MyViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onUploadClick(position: Int)
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imageView: ImageView
        var btnUpload: Button

        init {
            imageView = view.findViewById(R.id.editor_recycler_image)
            btnUpload = view.findViewById(R.id.btn_upload)

        }

        fun bind(item: EditorCollection, position: Int, listener: OnItemClickListener) {
            Picasso.get().load(item.imageID)
//                .resize(75, 75)
//                .onlyScaleDown()
                .placeholder(R.drawable.place_holder).into(imageView)
            //            imageView.setImageResource(item.getImageID());
            itemView.setOnClickListener { listener.onItemClick(position) }

            btnUpload.setOnClickListener {
                listener.onUploadClick(position)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.editor_recycler_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(list[position], position, listener)
    }

    fun setEditorList(articles: ArrayList<EditorCollection>) {
        this.list.clear()
        this.list.addAll(articles)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return list.size
    }
}