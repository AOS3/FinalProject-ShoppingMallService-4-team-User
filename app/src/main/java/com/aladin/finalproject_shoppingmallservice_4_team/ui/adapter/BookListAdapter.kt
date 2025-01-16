package com.aladin.finalproject_shoppingmallservice_4_team.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.ItemNoticeBinding

class BookListAdapter( var bookDataList: List<String>) :
    RecyclerView.Adapter<BookListAdapter.BookListViewHolder>() {

    fun updateBookList(bookList: List<String>) {
        bookDataList = bookList
        notifyDataSetChanged()
    }

    inner class BookListViewHolder(private val itemNoticeBinding: ItemNoticeBinding) :
        RecyclerView.ViewHolder(itemNoticeBinding.root) {
        fun bind(data: String) {
            itemNoticeBinding.textViewNoticeItemTitle
            itemNoticeBinding.textViewNoticeItemContent
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookListViewHolder {
        val itemNoticeBinding =
            ItemNoticeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookListViewHolder(itemNoticeBinding)
    }

    override fun getItemCount(): Int {
        return bookDataList.size
    }

    override fun onBindViewHolder(holder: BookListViewHolder, position: Int) {
        val data = bookDataList[position]
        holder.bind(data)
    }
}