package com.aladin.finalproject_shoppingmallservice_4_team.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.ItemLikeListBinding

class LikeListAdapter(private val noticeDataList: List<String>) :
    RecyclerView.Adapter<LikeListAdapter.LikeListViewHolder>() {

    inner class LikeListViewHolder(private val itemLikeListBinding: ItemLikeListBinding) :
        RecyclerView.ViewHolder(itemLikeListBinding.root) {
        fun bind(data: String) {
            itemLikeListBinding.imageViewLikeListBookIcon
            itemLikeListBinding.textViewLikeListBookTitle
            itemLikeListBinding.textViewLikeListBookAuthor
            itemLikeListBinding.textViewLikeListBookSellPrice
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LikeListViewHolder {
        val itemNoticeBinding =
            ItemLikeListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LikeListViewHolder(itemNoticeBinding)
    }

    override fun getItemCount(): Int {
        return noticeDataList.size
    }

    override fun onBindViewHolder(holder: LikeListViewHolder, position: Int) {
        val data = noticeDataList[position]
        holder.bind(data)
    }
}