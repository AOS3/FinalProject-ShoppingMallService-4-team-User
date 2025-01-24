package com.aladin.finalproject_shoppingmallservice_4_team.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aladin.apiTestApplication.dto.BookItem
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.ItemHomeListBinding
import com.aladin.finalproject_shoppingmallservice_4_team.util.loadImage
import com.aladin.finalproject_shoppingmallservice_4_team.util.toCommaString

class SearchAdapter(
    private val listener: SearchOnClickListener,
): RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {
    private val items = mutableListOf<BookItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return SearchViewHolder(
            ItemHomeListBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            itemClickListener = { position -> listener.itemClickListener(items[position]) }
        )
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun updateList(list: MutableList<BookItem>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    class SearchViewHolder(
        private val binding: ItemHomeListBinding,
        private val itemClickListener: (Int) -> Unit
    ): RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                itemClickListener(adapterPosition)
            }
        }

        fun bind(item: BookItem) {
            binding.apply {
                textViewHomeListName.text = item.title
                textViewHomeListWriter.text = item.author
                textviewHomeListPrice.text = "판매가 : ${item.priceStandard.toCommaString()}원"
                imageViewHomeList.loadImage(item.cover)
            }
        }
    }

    // 이름순으로 정렬
    fun sortByName() {
        val itemsFilter = items.sortedBy { it.title }
        items.clear()
        items.addAll(itemsFilter)
        notifyDataSetChanged()
    }

    // 최저가 순으로 정렬
    fun sortByLowestPrice() {
        val itemsFilter = items.sortedBy { it.priceStandard }
        items.clear()
        items.addAll(itemsFilter)
        notifyDataSetChanged()
    }

    // 최고가 순으로 정렬
    fun sortByHighestPrice() {
        val itemsFilter = items.sortedByDescending { it.priceStandard }
        items.clear()
        items.addAll(itemsFilter)
        notifyDataSetChanged()
    }
}

interface SearchOnClickListener {
    fun itemClickListener(item: BookItem)
}