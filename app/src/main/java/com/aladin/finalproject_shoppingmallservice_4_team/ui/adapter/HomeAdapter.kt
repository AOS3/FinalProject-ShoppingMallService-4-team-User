package com.aladin.finalproject_shoppingmallservice_4_team.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aladin.apiTestApplication.dto.RecommendBookItem
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.ItemHomeListBinding
import com.aladin.finalproject_shoppingmallservice_4_team.ui.home.HomeOnClickListener
import com.aladin.finalproject_shoppingmallservice_4_team.util.loadImage
import com.aladin.finalproject_shoppingmallservice_4_team.util.toCommaString

class HomeAdapter(
    private val listener: HomeOnClickListener,
): RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {
    private val items = mutableListOf<RecommendBookItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        return HomeViewHolder(
            ItemHomeListBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            itemClickListener = { position -> listener.itemClickListener(items[position]) }
        )
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun updateList(list: MutableList<RecommendBookItem>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    class HomeViewHolder(
        private val binding: ItemHomeListBinding,
        private val itemClickListener: (Int) -> Unit
    ): RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                itemClickListener(adapterPosition)
            }
        }

        fun bind(item: RecommendBookItem) {
            binding.apply {
                textViewHomeListName.text = item.title
                textViewHomeListWriter.text = item.author
                textviewHomeListPrice.text = "판매가 : ${item.priceStandard.toCommaString()}원"
                imageViewHomeList.loadImage(item.cover)
            }
        }
    }
}