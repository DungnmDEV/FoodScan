package com.base.app.baseapp.ui.intro

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.base.app.baseapp.databinding.ItemIntroBinding
import com.base.app.baseapp.model.IntroItem

class IntroAdapter(
    private val listItem: MutableList<IntroItem>
) :
    RecyclerView.Adapter<IntroAdapter.PagerViewHolder>() {

    inner class PagerViewHolder(private val binding: ItemIntroBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            val item = listItem[position]
            binding.img.setImageResource(item.img)
            binding.tvTitle.text = item.title
            binding.tvContent.text = item.content
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        val binding = ItemIntroBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PagerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = listItem.size
}
