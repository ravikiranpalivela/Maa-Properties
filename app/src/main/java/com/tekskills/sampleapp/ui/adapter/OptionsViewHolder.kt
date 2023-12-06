package com.tekskills.sampleapp.ui.adapter

import android.app.Activity
import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.tekskills.sampleapp.R
import com.tekskills.sampleapp.databinding.ItemOptionBinding
import com.tekskills.sampleapp.model.ItemOption

/**
 * Created by Ravi Kiran Palivela on 11/20/2023.
 * Description: Voting Options ViewHolder$
 */
class OptionsViewHolder(val ctx: Activity,
    private val itemBinding: ItemOptionBinding
) : RecyclerView.ViewHolder(itemBinding.root) {

    fun bind(item: ItemOption) {
        val votes = if (item.vote > 1) {
            "${item.vote} votes"
        } else "${item.vote} vote"

        val percentageText = "${item.percentage}%"

        itemBinding.apply {
            tvOption.text = item.title
            chipVote.text = votes
            percentage.progress = item.percentage
            llPollOptions.setBackgroundColor(if(!item.voted) ctx.getColor(R.color.white) else ctx.getColor(R.color.colorAccent))
            this.percentageText.text = percentageText
        }
    }

}