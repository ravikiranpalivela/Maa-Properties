package com.tekskills.sampleapp.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.tekskills.sampleapp.R
import com.tekskills.sampleapp.data.local.CommentItem
import com.tekskills.sampleapp.utils.TimeUtil

class CommentsListAdapter(private val context: Context,private val listener: (genresDateModel: CommentItem, position: Int) -> Unit) :
    RecyclerView.Adapter<CommentsListAdapter.MyViewHolder>() {
    private val list = ArrayList<CommentItem>()

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(genresDateModel: CommentItem) {
            val title = itemView.findViewById<TextView>(R.id.tv_title_comment)
            val cardView = itemView.findViewById<CardView>(R.id.card_calendar)
            val calendarDate = itemView.findViewById<TextView>(R.id.tv_date)

            title.text = genresDateModel.text
            calendarDate.text = TimeUtil.getTimeAgo(context, genresDateModel.lastUpdate)
            cardView.setOnClickListener {
                listener.invoke(genresDateModel, adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_comments_list, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setData(calendarList: ArrayList<CommentItem>) {
        list.clear()
        list.addAll(calendarList)
        notifyDataSetChanged()
    }
}