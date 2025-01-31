package com.aladin.finalproject_shoppingmallservice_4_team.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.ItemBookOrderListBinding
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.ItemShoppingCartListBinding
import com.aladin.finalproject_shoppingmallservice_4_team.model.OrderInquiryModel
import com.aladin.finalproject_shoppingmallservice_4_team.model.ShoppingCartModel
import com.aladin.finalproject_shoppingmallservice_4_team.ui.bookOrder.BookOrderFragment1
import com.aladin.finalproject_shoppingmallservice_4_team.ui.bookOrder.BookOrderViewModel
import com.aladin.finalproject_shoppingmallservice_4_team.ui.shoppingcart.ShoppingCartViewModel
import com.aladin.finalproject_shoppingmallservice_4_team.util.loadImage
import com.aladin.finalproject_shoppingmallservice_4_team.util.toCommaString

class BookOrderStatementAdapter(
    private var orderBookList: List<OrderInquiryModel>,
    private val bookCoverList: Map<String, Pair<String, Int>> = emptyMap() // 책 제목에 대한 이미지 링크와 갯수
) : RecyclerView.Adapter<BookOrderStatementAdapter.BookOrderViewHolder>() {

    inner class BookOrderViewHolder(private val binding: ItemBookOrderListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: OrderInquiryModel) {
            binding.apply {
                textViewOrderListBookTitle.text = data.orderInquiryTitle
                textViewOrderListBookAuthor.text = data.orderInquiryAuthor
                textViewOrderListBookSellPrice.text = "판매가 : ${data.orderInquiryPrice.toCommaString()}원"

                // 책 제목을 비교하여 이미지와 갯수를 업데이트
                val bookDetails = bookCoverList[data.orderInquiryTitle]
                bookDetails?.let {
                    imageViewOrderListBookIcon.loadImage(it.first) // 이미지 로드
                    textViewLikeListBookCount.text = "수량 : ${it.second}개" // 갯수 표시
                }
                textViewLikeListBookQuality.text = "품질 : " + when (data.orderInquiryQuality) {
                    0 -> "상"
                    1 -> "중"
                    else -> "하"
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookOrderViewHolder {
        val binding = ItemBookOrderListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return BookOrderViewHolder(binding)
    }

    override fun getItemCount(): Int = orderBookList.size

    override fun onBindViewHolder(holder: BookOrderViewHolder, position: Int) {
        holder.bind(orderBookList[position])
    }
}