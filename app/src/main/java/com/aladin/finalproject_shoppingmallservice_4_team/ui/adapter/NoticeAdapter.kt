package com.aladin.finalproject_shoppingmallservice_4_team.ui.adapter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.ItemNoticeBinding
import com.aladin.finalproject_shoppingmallservice_4_team.model.NoticeModel
import com.aladin.finalproject_shoppingmallservice_4_team.ui.notice.NoticeDetailFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.notice.NoticeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceSubFragment

class NoticeAdapter(
    private val noticeDataList: List<NoticeModel>,
    private val noticeFragment: NoticeFragment
) :
    RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder>() {

    inner class NoticeViewHolder(private val itemNoticeBinding: ItemNoticeBinding) :
        RecyclerView.ViewHolder(itemNoticeBinding.root) {
        fun bind(data: NoticeModel) {
            // NoticeModel의 필드를 해당 뷰에 바인딩
            itemNoticeBinding.textViewNoticeItemTitle.text =
                data.noticeTitle
            itemNoticeBinding.textViewNoticeItemContent.text =
                data.noticeContent

            // 공지사항 클릭 시
            itemView.setOnClickListener {
                val dataBundle = Bundle()
                // 제목
                dataBundle.putString("noticeTitle", data.noticeTitle)
                // 내용
                dataBundle.putString("noticeContent", data.noticeContent)
                // 날짜
                dataBundle.putString("noticeDate", data.noticeDate)

                // 세부 공지사항으로 이동
                noticeFragment.replaceSubFragment(
                    NoticeDetailFragment(),
                    true,
                    dataBundle = dataBundle
                )
            }
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
