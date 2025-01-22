package com.aladin.finalproject_shoppingmallservice_4_team.ui.sellingcart

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentMainMenuBinding
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentSellingCartBinding
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.RowSellingCartBinding
import com.aladin.finalproject_shoppingmallservice_4_team.ui.barcodescanner.BarcodeScannerFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.booksellinginquiry.BookSellingInquiryFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.sellinglastpage.SellingLastPageFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.sellingsearch.SellingSearchFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceMainFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceSubFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.divider.MaterialDividerItemDecoration

class SellingCartFragment : Fragment() {

    data class Book(
        val title: String,
        val author: String,
        val price: Int,
        var estimatedPrice: Int,
        var selectedQuality: String?,
        val imageResId: Int
    )

    // 현재는 데이터가 비어있음
     val tempData1 = emptyList<Book>()
//
//
//     val tempData1 = listOf(
//        Book(
//            title = "깡샘의 안드로이드 앱 프로그래밍",
//            author = "강성윤",
//            price = 40000,
//            estimatedPrice = 0,
//            selectedQuality = null,
//            imageResId = R.drawable.test_book_icon
//        ),
//        Book(
//            title = "무인도에서 살아남기",
//            author = "김철수",
//            price = 35000,
//            estimatedPrice = 0,
//            selectedQuality = null,
//            imageResId = R.drawable.test_book_icon
//        ),
//        Book(
//            title = "Why",
//            author = "박철수",
//            price = 30000,
//            estimatedPrice = 0,
//            selectedQuality = null,
//            imageResId = R.drawable.test_book_icon
//        )
//     )

    private lateinit var fragmentSellingCartBinding: FragmentSellingCartBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentSellingCartBinding = FragmentSellingCartBinding.inflate(layoutInflater, container, false)

        // ISBN 번호를 가져와 Toast로 표시
        handleReceivedISBN()

        // toolbar 설정 메서드 호출
        settingToolbar()

        // RecyclerView를 구성하는 메서드 호출
        settingRecyclerView()

        // 버튼 클릭 메서드 호출
        buttonSellingCartOnClick()

        return fragmentSellingCartBinding.root
    }

    // ISBN 번호 처리 메서드
    private fun handleReceivedISBN() {
        arguments?.getString("ISBN")?.let { isbn ->
            Toast.makeText(requireContext(), "받은 ISBN: $isbn", Toast.LENGTH_LONG).show()
            // API 호출
        }
    }

    // Toolbar를 구성하는 메서드
    private fun settingToolbar() {
        fragmentSellingCartBinding.apply {
            materialToolbarSellingCart.title = "중고 서적 팔기"
            // 네비게이션 아이콘을 설정하고 누를 경우 NavigationView가 나타나도록 한다.
            materialToolbarSellingCart.setNavigationIcon(R.drawable.arrow_back_ios_24px)
            materialToolbarSellingCart.setNavigationOnClickListener {
                // 전 화면으로 이동
            }

            // 툴바에 메뉴 아이템 동적으로 추가
            materialToolbarSellingCart.menu.add(Menu.NONE, 1, Menu.NONE, "판매 조회")
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

            materialToolbarSellingCart.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    // toolbar_mainMenu_goSettings
                    1 -> {
                        // 판매 조회로 이동
                        replaceSubFragment(BookSellingInquiryFragment(), true)
                    }
                }
                true
            }
        }
    }

    // 버튼을 클릭했을 때 메서드
    private fun buttonSellingCartOnClick() {
        fragmentSellingCartBinding.apply {
            buttonSellingCartSearch.setOnClickListener {
                // 도서 팔기 검색 화면으로 이동
                replaceSubFragment(SellingSearchFragment(), true)
            }

            buttonSellingCartBarcodeScanner.setOnClickListener {
                // 바코드 찍는 화면으로 이동
                val dataBundle = Bundle()
                dataBundle.putString("FragmentQuery", "SellingCart")
                replaceSubFragment(BarcodeScannerFragment(), true, dataBundle = dataBundle)
            }

            buttonSellingCartAddBookForSelling.setOnClickListener {
                // 중고 도서 팔기 마지막 화면으로 이동
                replaceSubFragment(SellingLastPageFragment(), true)
            }
        }
    }

    // RecyclerView를 구성하는 메서드
    private fun settingRecyclerView() {
        fragmentSellingCartBinding.apply {
            // 데이터가 비어 있는지 확인
            val isDataEmpty = tempData1.isEmpty()

            // Empty View와 RecyclerView의 가시성을 설정
            includeSellingCartEmpty.root.visibility = if (isDataEmpty) View.VISIBLE else View.GONE
            recyclerViewSellingCartInfo.visibility = if (isDataEmpty) View.GONE else View.VISIBLE

            // RecyclerView 설정 (데이터가 있을 경우만 초기화)
            if (!isDataEmpty) {
                recyclerViewSellingCartInfo.apply {
                    adapter = RecyclerSellingCartAdapter()
                    layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    val deco = MaterialDividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
                    addItemDecoration(deco)
                }
            }
        }
    }

    // RecyclerView의 어댑터
    private inner class RecyclerSellingCartAdapter :
        RecyclerView.Adapter<RecyclerSellingCartAdapter.ViewHolderSellingCartAdapter>() {
        // ViewHolder
        inner class ViewHolderSellingCartAdapter(val rowSellingCartBinding: RowSellingCartBinding) :
            RecyclerView.ViewHolder(rowSellingCartBinding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderSellingCartAdapter {
            val rowSellingCartBinding = RowSellingCartBinding.inflate(layoutInflater, parent, false)
            return ViewHolderSellingCartAdapter(rowSellingCartBinding)
        }

        override fun getItemCount(): Int {
            return tempData1.size
        }

        override fun onBindViewHolder(holder: ViewHolderSellingCartAdapter, position: Int) {
            val book = tempData1[position]

            // 기본 UI 설정
            holder.rowSellingCartBinding.imageViewSellingCartBook.setImageResource(book.imageResId)
            holder.rowSellingCartBinding.textViewSellingCartBookTitle.text = book.title
            holder.rowSellingCartBinding.textViewSellingCartBookAuthor.text = book.author
            holder.rowSellingCartBinding.textViewSellingCartBookPrice.text = "정가: ${book.price}원"
            holder.rowSellingCartBinding.textViewSellingCartEstimatedPrice.text =
                "예상 판매가: ${(book.price * 0.9).toInt()}원"

            // 버튼 클릭 리스너 설정
            holder.rowSellingCartBinding.toggleGroupQuality.addOnButtonCheckedListener { _, checkedId, isChecked ->
                if (isChecked) {
                    book.selectedQuality = when (checkedId) {
                        R.id.button_sellingCart_High -> "상"
                        R.id.button_sellingCart_Medium -> "중"
                        R.id.button_sellingCart_Low -> "하"
                        else -> book.selectedQuality
                    }

                    // 품질에 따른 예상 판매가
                    book.estimatedPrice = when (book.selectedQuality) {
                        "상" -> (book.price * 0.9).toInt()
                        "중" -> (book.price * 0.7).toInt()
                        "하" -> (book.price * 0.5).toInt()
                        else -> book.price
                    }
                    // 예상 판매가 업데이트
                    holder.rowSellingCartBinding.textViewSellingCartEstimatedPrice.text =
                        "예상 판매가: ${book.estimatedPrice}원"

                    // 버튼 상태 업데이트
                    when (checkedId) {
                        R.id.button_sellingCart_High -> setButtonState(
                            holder.rowSellingCartBinding.buttonSellingCartHigh,
                            holder.rowSellingCartBinding.buttonSellingCartMedium,
                            holder.rowSellingCartBinding.buttonSellingCartLow
                        )
                        R.id.button_sellingCart_Medium -> setButtonState(
                            holder.rowSellingCartBinding.buttonSellingCartMedium,
                            holder.rowSellingCartBinding.buttonSellingCartHigh,
                            holder.rowSellingCartBinding.buttonSellingCartLow
                        )
                        R.id.button_sellingCart_Low -> setButtonState(
                            holder.rowSellingCartBinding.buttonSellingCartLow,
                            holder.rowSellingCartBinding.buttonSellingCartHigh,
                            holder.rowSellingCartBinding.buttonSellingCartMedium
                        )
                    }
                }
            }
        }

        private fun setButtonState(
            selectedButton: MaterialButton, vararg unselectedButtons: MaterialButton
        ) {
            // 선택된 버튼 스타일
            selectedButton.setBackgroundColor(
                ContextCompat.getColor(
                    selectedButton.context,
                    R.color.main_color
                )
            )
            selectedButton.setTextColor(ContextCompat.getColor(selectedButton.context, android.R.color.white))

            // 선택되지 않은 버튼 스타일
            unselectedButtons.forEach { button ->
                button.setBackgroundColor(ContextCompat.getColor(button.context, android.R.color.white))
                button.setTextColor(ContextCompat.getColor(button.context, R.color.black))
            }
        }

    }
}
