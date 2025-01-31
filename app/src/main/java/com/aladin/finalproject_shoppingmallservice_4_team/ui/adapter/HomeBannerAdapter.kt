package com.aladin.finalproject_shoppingmallservice_4_team.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.ItemHomeBannerBinding
import com.aladin.finalproject_shoppingmallservice_4_team.util.loadBannerImage
import com.aladin.finalproject_shoppingmallservice_4_team.util.loadImage

class HomeBannerAdapter: RecyclerView.Adapter<HomeBannerAdapter.HomeBannerViewHolder>() {

    private val items = mutableListOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeBannerViewHolder {
        return HomeBannerViewHolder(ItemHomeBannerBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = items.size


    override fun onBindViewHolder(holder: HomeBannerViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun updateItem(item: MutableList<Int>) {
        items.clear()
        items.addAll(item)
        notifyDataSetChanged()
    }

    class HomeBannerViewHolder(
        private val binding: ItemHomeBannerBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(uri: Int) {
            binding.imageViewHomeBanner.loadBannerImage(uri)
        }
    }
}