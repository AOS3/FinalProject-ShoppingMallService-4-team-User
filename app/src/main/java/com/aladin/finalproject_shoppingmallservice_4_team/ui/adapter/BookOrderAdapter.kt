package com.aladin.finalproject_shoppingmallservice_4_team.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.ItemBookOrderListBinding
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.ItemShoppingCartListBinding
import com.aladin.finalproject_shoppingmallservice_4_team.model.ShoppingCartModel
import com.aladin.finalproject_shoppingmallservice_4_team.ui.bookOrder.BookOrderFragment1
import com.aladin.finalproject_shoppingmallservice_4_team.ui.bookOrder.BookOrderViewModel
import com.aladin.finalproject_shoppingmallservice_4_team.ui.shoppingcart.ShoppingCartViewModel
import com.aladin.finalproject_shoppingmallservice_4_team.util.loadImage
import com.aladin.finalproject_shoppingmallservice_4_team.util.toCommaString

class BookOrderAdapter(
    private var shoppingCartDataList: List<ShoppingCartModel>
) : RecyclerView.Adapter<BookOrderAdapter.BookOrderViewHolder>() {

    inner class BookOrderViewHolder(private val binding: ItemBookOrderListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ShoppingCartModel) {
            binding.apply {
                textViewOrderListListCountNumber.text = "(${adapterPosition + 1})"
                textViewOrderListBookTitle.text = data.shoppingCartBookTitle
                textViewOrderListBookAuthor.text = data.shoppingCartBookAuthor
                textViewOrderListBookSellPrice.text = "판매가 : ${data.shoppingCartSellingPrice.toCommaString()}원"
                imageViewOrderListBookIcon.loadImage(data.shoppingCartBookCoverImage)
                textViewLikeListBookCount.text = "수량 : ${data.shoppingCartBookQualityCount}개"
                textViewLikeListBookQuality.text = "품질 : " + when (data.shoppingCartQuality) {
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

    override fun getItemCount(): Int = shoppingCartDataList.size

    override fun onBindViewHolder(holder: BookOrderViewHolder, position: Int) {
        holder.bind(shoppingCartDataList[position])
    }
}