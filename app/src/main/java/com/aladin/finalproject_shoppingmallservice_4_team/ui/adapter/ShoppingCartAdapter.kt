package com.aladin.finalproject_shoppingmallservice_4_team.ui.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.ItemShoppingCartListBinding
import com.aladin.finalproject_shoppingmallservice_4_team.model.ShoppingCartModel
import com.aladin.finalproject_shoppingmallservice_4_team.ui.bookdetail.BookDetailFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.custom.CustomDialog
import com.aladin.finalproject_shoppingmallservice_4_team.ui.shoppingcart.ShoppingCartFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.shoppingcart.ShoppingCartViewModel
import com.aladin.finalproject_shoppingmallservice_4_team.util.loadImage
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceMainFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceSubFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.toCommaString

class ShoppingCartAdapter(
    private var shoppingCartDataList: List<ShoppingCartModel>,
    private val shoppingCartFragment: ShoppingCartFragment,
    private val shoppingCartViewModel: ShoppingCartViewModel,
    private val onItemCheckedChange: () -> Unit
) : RecyclerView.Adapter<ShoppingCartAdapter.ShoppingCartViewHolder>() {

    private val selectedItems = mutableSetOf<ShoppingCartModel>()

    inner class ShoppingCartViewHolder(private val binding: ItemShoppingCartListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ShoppingCartModel) {
            binding.apply {
                // 제목
                textViewShoppingCartBookTitle.text = data.shoppingCartBookTitle
                // 저자
                textViewShoppingCartBookAuthor.text = data.shoppingCartBookAuthor
                // 판매가
                textViewShoppingCartBookSellPrice.text =
                    "판매가 : ${data.shoppingCartSellingPrice.toCommaString()}원"
                // 이미지
                imageViewShoppingCartBookIcon.loadImage(data.shoppingCartBookCoverImage)
                // 수량
                textViewShoppingCartBookCount.text = "수량 : ${data.shoppingCartBookQualityCount}개"
                // 품질
                textViewShoppingCartBookQuality.text = "품질 : " + when (data.shoppingCartQuality) {
                    0 -> "상"
                    1 -> "중"
                    else -> "하"
                }

                // 체크박스 클릭
                checkBoxShoppingCartChoice.isChecked = selectedItems.contains(data)
                checkBoxShoppingCartChoice.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        selectedItems.add(data)
                    } else {
                        selectedItems.remove(data)
                    }
                    onItemCheckedChange()
                }

                // 삭제 버튼 클릭
                imageButtonShoppingCartDeleteList.setOnClickListener { item ->
                    val twoButtonDialog = CustomDialog(
                        shoppingCartFragment.requireContext(),
                        // 리스트 삭제 진행
                        onPositiveClick = {
                            shoppingCartViewModel.deleteCheckedShoppingCartBookList(
                                getClickItemData(
                                    data
                                )
                            )
                            // 삭제된 항목을 반영하여 shoppingCartDataList 갱신
                            shoppingCartDataList = shoppingCartDataList.filterNot { it.shoppingCartISBN == data.shoppingCartISBN }
                            // 선택된 항목에서 삭제된 항목도 제거
                            selectedItems.remove(data)
                            // 어댑터에 변경 사항 알리기
                            notifyDataSetChanged()
                        },
                        onNegativeClick = {

                        },
                        positiveText = "삭제",
                        negativeText = "취소",
                        contentText = "정말 이 장바구니를 삭제 하시겠습니까?",
                        icon = R.drawable.error_24px
                    )
                    twoButtonDialog.showCustomDialog()
                }

                // 아이템 클릭 리스너
                itemView.setOnClickListener {
                    val dataBundle = Bundle()
                    // ISBN
                    dataBundle.putString("bookIsbn", data.shoppingCartISBN)

                    shoppingCartFragment.replaceMainFragment(
                        BookDetailFragment(),
                        true,
                        dataBundle = dataBundle
                    )
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingCartViewHolder {
        val binding = ItemShoppingCartListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ShoppingCartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShoppingCartViewHolder, position: Int) {
        holder.bind(shoppingCartDataList[position])
    }

    override fun getItemCount(): Int = shoppingCartDataList.size

    fun selectAllItems(isSelected: Boolean) {
        selectedItems.clear()
        if (isSelected) {
            selectedItems.addAll(shoppingCartDataList)
        }
        notifyDataSetChanged()
    }

    fun getCheckedItemsWithUserTokenAndIsbn(): List<Pair<String, String>> {
        // 체크된 항목만 필터링하여 userToken과 isbn을 Pair로 묶어서 반환
        return selectedItems.map { Pair(it.shoppingCartUserToken, it.shoppingCartISBN) }
    }

    fun getClickItemData(data: ShoppingCartModel): List<Pair<String, String>> {
        // 클릭한 항목을 Pair로 묶어서 List로 반환 (하나의 Pair를 포함하는 리스트 반환)
        return listOf(Pair(data.shoppingCartUserToken, data.shoppingCartISBN))
    }

    fun updateShoppingCartDataList(dataList: List<ShoppingCartModel>) {
        shoppingCartDataList = dataList
        notifyDataSetChanged()
    }

    fun getSelectedItems(): List<ShoppingCartModel> = selectedItems.toList()
}