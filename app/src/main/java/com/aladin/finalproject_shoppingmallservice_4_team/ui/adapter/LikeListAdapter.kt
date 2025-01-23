package com.aladin.finalproject_shoppingmallservice_4_team.ui.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.ItemLikeListBinding
import com.aladin.finalproject_shoppingmallservice_4_team.model.LikeListModel
import com.aladin.finalproject_shoppingmallservice_4_team.ui.bookdetail.BookDetailFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.custom.CustomDialog
import com.aladin.finalproject_shoppingmallservice_4_team.ui.likeList.LikeListFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.likeList.LikeListViewModel
import com.aladin.finalproject_shoppingmallservice_4_team.util.loadImage
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceSubFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.toCommaString

class LikeListAdapter(
    private val likeListDataList: List<LikeListModel>,
    private val likeListFragment: LikeListFragment,
    private val likeListViewModel: LikeListViewModel,
    private val context: Context
) :
    RecyclerView.Adapter<LikeListAdapter.LikeListViewHolder>() {

    inner class LikeListViewHolder(private val itemLikeListBinding: ItemLikeListBinding) :
        RecyclerView.ViewHolder(itemLikeListBinding.root) {
        fun bind(data: LikeListModel) {
            itemLikeListBinding.apply {
                textViewLikeListBookTitle.text =
                    data.likeListBookTitle
                textViewLikeListBookAuthor.text =
                    data.likeListBookAuthor

                imageViewLikeListBookIcon.loadImage(data.likeListBookCoverImage)

                textViewLikeListBookSellPrice.text =
                    "판매가 : ${((data.likeListBookPrice * 0.3).toInt()).toCommaString()}원 ~ ${((data.likeListBookPrice * 0.7).toInt()).toCommaString()}원"

                // 삭제하기 버튼 클릭 시
                buttonLikeListDelete.setOnClickListener {
                    val loginDialog = CustomDialog(
                        context,
                        onPositiveClick = {
                            if(likeListViewModel.deleteLikeListData(data.likeListUserToken,data.likeListISBN)) {
                                notifyDataSetChanged()
                            }
                        },
                        contentText = "정말 이 찜목록을 삭제하시겠습니까?",
                        icon = R.drawable.error_24px,
                        negativeText = "취소",
                        positiveText = "삭제"
                    )
                    loginDialog.showCustomDialog()
                }

                // 장바구니에 담기 버튼 클릭 시
                buttonLikeListInLikeList.setOnClickListener {
                    val dataBundle = Bundle()
                    dataBundle.putString("bookIsbn",data.likeListISBN)
                    likeListFragment.replaceSubFragment(BookDetailFragment(),true,dataBundle)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LikeListViewHolder {
        val itemLikeListBinding =
            ItemLikeListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LikeListViewHolder(itemLikeListBinding)
    }

    override fun getItemCount(): Int {
        return likeListDataList.size
    }

    override fun onBindViewHolder(holder: LikeListViewHolder, position: Int) {
        val data = likeListDataList[position]
        holder.bind(data)
    }
}
