package com.aladin.finalproject_shoppingmallservice_4_team.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.ItemNoticeBinding

class NoticeAdapter(private val noticeDataList: List<String>) :
    RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder>() {

    inner class NoticeViewHolder(private val itemNoticeBinding: ItemNoticeBinding) :
        RecyclerView.ViewHolder(itemNoticeBinding.root) {
        fun bind(data: String) {
            itemNoticeBinding.textViewNoticeItemTitle
            itemNoticeBinding.textViewNoticeItemContent
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        val itemNoticeBinding =
            ItemNoticeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoticeViewHolder(itemNoticeBinding)
    }

    override fun getItemCount(): Int {
        return noticeDataList.size
    }

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        val data = noticeDataList[position]
        holder.bind(data)
    }
}