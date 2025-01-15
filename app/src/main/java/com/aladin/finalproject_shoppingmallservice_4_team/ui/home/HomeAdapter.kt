package com.aladin.finalproject_shoppingmallservice_4_team.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.HomeListItemBinding
import com.aladin.finalproject_shoppingmallservice_4_team.model.HomeBookModel

class HomeAdapter: RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {
    private var items = mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        return HomeViewHolder(HomeListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = 5

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun updateList(list: MutableList<String>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    class HomeViewHolder(private val binding: HomeListItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: String) {
            binding.apply {
                textView.text = item
                textview2.text = item
                textview3.text = "판매가 : ${item}"
            }
        }
    }
}