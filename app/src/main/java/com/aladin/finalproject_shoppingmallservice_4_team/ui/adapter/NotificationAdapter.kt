package com.aladin.finalproject_shoppingmallservice_4_team.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.ItemNotificationListBinding
import com.aladin.finalproject_shoppingmallservice_4_team.model.NotificationModel


class NotificationAdapter(
    private val listener: NotificationOnClickListener,
): RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {
    private val items = mutableListOf<NotificationModel>()

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

    fun updateList(list: MutableList<NotificationModel>) {
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

        fun bind(item: NotificationModel) {
            binding.apply {
                textViewNotificationListTitle.text = item.notificationTitle
                textViewNotificationListDate.text = item.notificationTime
                if (item.notificationSee == 1) {
                    textViewNotificationListTitle.setTextColor(Color.LTGRAY)
                    textViewNotificationListDate.setTextColor(Color.LTGRAY)
                }
            }
        }
    }
}

interface NotificationOnClickListener {
    fun itemClickListener(item: NotificationModel)
}