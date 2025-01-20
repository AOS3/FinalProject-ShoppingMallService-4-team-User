package com.aladin.finalproject_shoppingmallservice_4_team.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aladin.apiTestApplication.dto.RecommendBookItem
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.ItemBookListBinding
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.ItemNoticeBinding
import com.aladin.finalproject_shoppingmallservice_4_team.model.BookListModel
import com.aladin.finalproject_shoppingmallservice_4_team.ui.booklist.BookListFragment
import com.bumptech.glide.Glide

class UsedBookListAdapter(
    private var usedBookDataList: List<BookListModel>,
    private val bookListFragment: BookListFragment
) :
    RecyclerView.Adapter<UsedBookListAdapter.UsedBookListViewHolder>() {


    inner class UsedBookListViewHolder(private val itemBookListItem: ItemBookListBinding) :
        RecyclerView.ViewHolder(itemBookListItem.root) {
        fun bind(data: BookListModel) {
            Glide.with(itemBookListItem.imageViewBookListBookIcon)
                .load(data.cover)
                .into(itemBookListItem.imageViewBookListBookIcon)
            itemBookListItem.textViewLikeListBookQuality.text = when(data.usedBookQuality) {
                0 -> "상"
                1 -> "중"
                else -> "하"
            }
            itemBookListItem.textViewBookListBookTitle.text = data.usedBookTitle
            itemBookListItem.textViewBookListBookAuthor.text = data.usedBookAuthor
            itemBookListItem.textViewBookListBookSellPrice.text = "판매가 : ${formatNumberWithCommas(data.usedBookSellingPrice)}원"
        }
    }

    fun formatNumberWithCommas(number: Int): String {
        return "%,d".format(number)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsedBookListViewHolder {
        val itemBookListItem =
            ItemBookListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UsedBookListViewHolder(itemBookListItem)
    }

    override fun getItemCount(): Int {
        return usedBookDataList.size
    }

    override fun onBindViewHolder(holder: UsedBookListViewHolder, position: Int) {
        val data = usedBookDataList[position]
        holder.bind(data)
    }

    // 이름순으로 정렬
    fun sortByName() {
        usedBookDataList = usedBookDataList.sortedBy { it.usedBookTitle }
        notifyDataSetChanged()
    }

    // 최저가 순으로 정렬
    fun sortByLowestPrice() {
        usedBookDataList = usedBookDataList.sortedBy { it.usedBookSellingPrice }
        notifyDataSetChanged()
    }

    // 최고가 순으로 정렬
    fun sortByHighestPrice() {
        usedBookDataList = usedBookDataList.sortedByDescending { it.usedBookSellingPrice }
        notifyDataSetChanged()
    }
}