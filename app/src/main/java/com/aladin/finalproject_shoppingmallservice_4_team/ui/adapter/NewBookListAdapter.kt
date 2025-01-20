package com.aladin.finalproject_shoppingmallservice_4_team.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aladin.apiTestApplication.dto.RecommendBookItem
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.ItemBookListBinding
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.ItemNoticeBinding
import com.aladin.finalproject_shoppingmallservice_4_team.ui.booklist.BookListFragment
import com.bumptech.glide.Glide

class NewBookListAdapter(
    private var newBookDataList: List<RecommendBookItem>,
    private val bookListFragment: BookListFragment
) :
    RecyclerView.Adapter<NewBookListAdapter.NewBookListViewHolder>() {

    inner class NewBookListViewHolder(private val itemBookListItem: ItemBookListBinding) :
        RecyclerView.ViewHolder(itemBookListItem.root) {
        fun bind(data: RecommendBookItem) {
            itemBookListItem.textViewLikeListBookQuality.visibility = View.GONE
            Glide.with(itemBookListItem.imageViewBookListBookIcon)
                .load(data.cover)
                .into(itemBookListItem.imageViewBookListBookIcon)
            itemBookListItem.textViewBookListBookTitle.text = data.title
            itemBookListItem.textViewBookListBookAuthor.text = data.author
            itemBookListItem.textViewBookListBookSellPrice.text = "판매가 : ${formatNumberWithCommas(data.priceStandard)}원"
        }
    }

    fun formatNumberWithCommas(number: Int): String {
        return "%,d".format(number)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewBookListViewHolder {
        val itemBookListItem =
            ItemBookListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewBookListViewHolder(itemBookListItem)
    }

    override fun getItemCount(): Int {
        return newBookDataList.size
    }

    override fun onBindViewHolder(holder: NewBookListViewHolder, position: Int) {
        val data = newBookDataList[position]
        holder.bind(data)
    }

    // 이름순으로 정렬
    fun sortByName() {
        newBookDataList = newBookDataList.sortedBy { it.title }
        notifyDataSetChanged()
    }

    // 최저가 순으로 정렬
    fun sortByLowestPrice() {
        newBookDataList = newBookDataList.sortedBy { it.priceStandard }
        notifyDataSetChanged()
    }

    // 최고가 순으로 정렬
    fun sortByHighestPrice() {
        newBookDataList = newBookDataList.sortedByDescending { it.priceStandard }
        notifyDataSetChanged()
    }
}