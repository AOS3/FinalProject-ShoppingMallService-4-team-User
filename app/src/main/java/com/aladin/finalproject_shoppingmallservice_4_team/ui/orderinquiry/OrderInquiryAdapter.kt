package com.aladin.finalproject_shoppingmallservice_4_team.ui.orderinquiry

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.OrderInquiryListItemBinding
import com.aladin.finalproject_shoppingmallservice_4_team.model.OrderInquiryModel
import java.text.SimpleDateFormat

class OrderInquiryAdapter(
    private val listener: OrderOnClickListener
): RecyclerView.Adapter<OrderInquiryAdapter.OrderInquiryViewHolder>() {

    private val items = mutableListOf<OrderInquiryModel>()

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

    fun updateItemList(item: MutableList<OrderInquiryModel>) {
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

        fun bind(item: OrderInquiryModel) {
            binding.apply {
                textViewOrderInquiryListName.text = item.orderInquiryTitle
                textViewOrderInquiryListWriter.text = item.orderInquiryAuthor
                textViewOrderInquiryListQuality.text = when(item.orderInquiryQuality) {
                    0 -> "품질 : 상"
                    1 -> "품질 : 중"
                    else -> "품질 : 하"
                }
                textViewOrderInquiryListOrderDate.text = "주문 시간 : ${showDateData(item.orderInquiryTime)}"
                when(item.orderInquiryDeliveryResult) {
                    0 -> {
                        textViewOrderInquiryListDelivery.text = "배송 전"
                        textViewOrderInquiryListDelivery.setTextColor(Color.RED)
                    }
                    1 -> {
                        textViewOrderInquiryListDelivery.text = "배송 중"
                        textViewOrderInquiryListDelivery.setTextColor(Color.rgb(255,106,0))
                    }
                    2 -> {
                        textViewOrderInquiryListDelivery.text = "배송 완료"
                        textViewOrderInquiryListDelivery.setTextColor(Color.rgb(50,190,7))
                    }
                }
                textViewOrderInquiryListPrice.text = "주문 금액 : ${item.orderInquiryPrice}원"
                textViewOrderInquiryListOrderNumber.text = "주문번호 : ${item.orderInquiryNumber}"
            }
        }

        // 날짜 보여주는 메서드
        private fun showDateData(timeData: Long): String {
            val dataFormat1 = SimpleDateFormat("yyyy-MM-dd. HH:mm:ss")
            val date = dataFormat1.format(timeData)
            return date
        }
    }
}

interface OrderOnClickListener {
    fun itemClickListener(item: OrderInquiryModel)
}