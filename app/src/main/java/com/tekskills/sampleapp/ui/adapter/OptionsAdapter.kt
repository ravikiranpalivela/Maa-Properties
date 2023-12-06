package com.tekskills.sampleapp.ui.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.tekskills.sampleapp.R
import com.tekskills.sampleapp.databinding.ItemOptionBinding
import com.tekskills.sampleapp.model.ItemOption

class OptionsAdapter(val activity: Activity) : ListAdapter<ItemOption, OptionsViewHolder>(OptionItemDiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): OptionsViewHolder {
        return OptionsViewHolder(ctx = activity,
            itemBinding = ItemOptionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(
        holder: OptionsViewHolder,
        position: Int
    ) {
        val item = getItem(position)
        holder.bind(item)
    }

    override fun getItemViewType(
        position: Int
    ) = R.layout.item_option

}

class OptionItemDiffCallback : DiffUtil.ItemCallback<ItemOption>() {

    override fun areItemsTheSame(
        oldItem: ItemOption,
        newItem: ItemOption
    ): Boolean = oldItem.title == newItem.title

    override fun areContentsTheSame(
        oldItem: ItemOption, newItem: ItemOption
    ) = oldItem == newItem

}
