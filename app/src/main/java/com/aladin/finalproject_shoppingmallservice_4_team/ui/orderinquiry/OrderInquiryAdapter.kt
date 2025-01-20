package com.aladin.finalproject_shoppingmallservice_4_team.ui.orderinquiry

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.OrderInquiryListItemBinding

class OrderInquiryAdapter(
    private val listener: OrderOnClickListener
): RecyclerView.Adapter<OrderInquiryAdapter.OrderInquiryViewHolder>() {

    private val items = mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderInquiryViewHolder {
        return OrderInquiryViewHolder(
            OrderInquiryListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            itemClickListener = {position -> listener.itemClickListener(items[position])}
        )
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: OrderInquiryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun updateItemList(item: MutableList<String>) {
        items.clear()
        items.addAll(item)
        notifyDataSetChanged()
    }

    class OrderInquiryViewHolder(
        private val binding: OrderInquiryListItemBinding,
        private val itemClickListener: (Int) -> Unit
    ): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                itemClickListener(adapterPosition)
            }
        }

        fun bind(item: String) {
            binding.apply {
                textViewOrderInquiryListName.text = item
                textViewOrderInquiryListWriter.text = item
                textViewOrderInquiryListQuality.text = item
                textViewOrderInquiryListOrderDate.text = item
                textViewOrderInquiryListDelivery.text = item
                textViewOrderInquiryListPrice.text = item
                textViewOrderInquiryListOrderNumber.text = item
            }
        }
    }
}

interface OrderOnClickListener {
    fun itemClickListener(item: String)
}