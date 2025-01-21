package com.aladin.finalproject_shoppingmallservice_4_team.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.ItemNotificationListBinding


class NotificationAdapter(
    private val listener: NotificationOnClickListener,
): RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {
    private val items = mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder(
            ItemNotificationListBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            itemClickListener = { position -> listener.itemClickListener(items[position]) }
        )
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun updateList(list: MutableList<String>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    class NotificationViewHolder(
        private val binding: ItemNotificationListBinding,
        private val itemClickListener: (Int) -> Unit
    ): RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                itemClickListener(adapterPosition)
            }
        }

        fun bind(item: String) {
            binding.apply {
                textViewNotificationListTitle.text = item
                textViewNotificationListDate.text = item
            }
        }
    }
}

interface NotificationOnClickListener {
    fun itemClickListener(item: String)
}