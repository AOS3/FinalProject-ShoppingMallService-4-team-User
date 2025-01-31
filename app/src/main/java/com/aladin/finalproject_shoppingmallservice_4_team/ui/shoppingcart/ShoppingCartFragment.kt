package com.aladin.finalproject_shoppingmallservice_4_team.ui.shoppingcart

import android.app.Dialog
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.aladin.finalproject_shoppingmallservice_4_team.BookApplication
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentShoppingCartBinding
import com.aladin.finalproject_shoppingmallservice_4_team.ui.adapter.ShoppingCartAdapter
import com.aladin.finalproject_shoppingmallservice_4_team.ui.bookOrder.BookOrderFragment1
import com.aladin.finalproject_shoppingmallservice_4_team.ui.custom.CustomDialog
import com.aladin.finalproject_shoppingmallservice_4_team.ui.custom.CustomDialogProgressbar
import com.aladin.finalproject_shoppingmallservice_4_team.ui.login.LoginFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.mainMenu.MainMenuFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.notice.NoticeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.search.SearchFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.clearAllBackStack
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceMainFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.toCommaString
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShoppingCartFragment : Fragment() {
    private lateinit var fragmentShoppingCartBinding: FragmentShoppingCartBinding
    private lateinit var shoppingCartAdapter: ShoppingCartAdapter
    private lateinit var bookApplication: BookApplication
    private lateinit var progressBarDialog: Dialog

    private val shoppingCartViewModel: ShoppingCartViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentShoppingCartBinding =
            FragmentShoppingCartBinding.inflate(layoutInflater, container, false)

        // 화면 입장 시 다이얼로그
        progressBarDialog = CustomDialogProgressbar(requireContext())

        // bookApplication 초기화
        bookApplication = requireActivity().application as BookApplication

        // 로그인 체크 메서드 세팅
        checkLoginProcess()

        // 툴바 세팅
        settingToolbar()

        // 다이얼로그
        observeProgressDialog()

        // 리싸이클러뷰
        setupRecyclerView()

        // 옵저버
        observeBookListData()

        // 체크박스
        settingCheckBox()

        // 검색창 클릭 이벤트
        onClickSearchButton()

        // 전체 삭제 클릭 이벤트
        onClickDeleteAllButton()

        // 구매하기 버튼 클릭
        onClickBuyShoppingCartButton()

        return fragmentShoppingCartBinding.root
    }

    // onClick buyShoppingCartList
    private fun onClickBuyShoppingCartButton() {
        fragmentShoppingCartBinding.apply {
            buttonShoppingCartBuyBook.setOnClickListener {
                val buyDataList = shoppingCartAdapter.getCheckedItemsWithUserTokenAndIsbn()
                if (buyDataList.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "현재 체크된 리스트가 없습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    shoppingCartViewModel.updateShoppingCartState(buyDataList) {
                        replaceMainFragment(
                            BookOrderFragment1(),
                            true,
                            backStackTag = "BookOrderFragment1"
                        )
                    }
                }
            }
        }
    }

    // Check Login
    private fun checkLoginProcess() {
        try {
            if (::bookApplication.isInitialized && bookApplication.loginUserModel != null) {
                shoppingCartViewModel.gettingShoppingCartBookData(bookApplication.loginUserModel.userToken)
            } else {
                shoppingCartViewModel.gettingShoppingCartBookData(bookApplication.loginUserModel.userToken)
            }
        } catch (e: Exception) {
            shoppingCartViewModel.dismissProgressDialog()
            val loginDialog = CustomDialog(
                requireContext(),
                // 리스트 삭제 진행
                onPositiveClick = {
                    removeFragment()
                },
                contentText = "로그인을 먼저 진행해주세요.",
                icon = R.drawable.error_24px
            )
            loginDialog.showCustomDialog()
        }
    }

    // delete All ShoppingCartData
    private fun onClickDeleteAllButton() {
        fragmentShoppingCartBinding.apply {
            buttonShoppingCartDeleteList.setOnClickListener {
                val twoButtonDialog = CustomDialog(
                    requireContext(),
                    onPositiveClick = {
                        val deleteList = shoppingCartAdapter.getCheckedItemsWithUserTokenAndIsbn()
                        if (deleteList.isEmpty()) {
                            Toast.makeText(
                                requireContext(),
                                "현재 체크된 리스트가 없습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            // 리스트 삭제
                            //shoppingCartViewModel.deleteCheckedShoppingCartBookList(deleteList)
                            shoppingCartViewModel.deleteCheckedShoppingCartBookList(deleteList) { success ->
                                if (success) {
                                    // 삭제 성공
                                    // 리스트 업데이트
                                    shoppingCartAdapter.updateShoppingCartDataList(shoppingCartViewModel.shoppingCartBookList.value!!)
                                } else {
                                    // 삭제 실패
                                }
                            }
                        }
                    },
                    onNegativeClick = {

                    },
                    positiveText = "삭제",
                    negativeText = "취소",
                    contentText = "체크한 장바구니 리스트를 전부 삭제하시겠습니까?",
                    icon = R.drawable.error_24px
                )
                twoButtonDialog.showCustomDialog()
            }
        }
    }

    // onClickSearch
    private fun onClickSearchButton() {
        fragmentShoppingCartBinding.apply {
            buttonShoppingCartSearch.setOnClickListener {
                replaceMainFragment(SearchFragment(), true)
            }
        }
    }

    private fun settingCheckBox() {
        fragmentShoppingCartBinding.apply {
            // 전체 선택 체크박스 리스너
            checkBoxShoppingCartCheckAllList.setOnCheckedChangeListener { _, isChecked ->
                // 전체 선택/해제
                shoppingCartAdapter.selectAllItems(isChecked)
                calculateTotals()
            }
        }
    }

    // 아이템 상태 변경 시 전체 체크박스 상태 업데이트
    fun onItemCheckedChange() {
        val allSelected =
            shoppingCartAdapter.shoppingCartDataList.size == shoppingCartAdapter.getSelectedItems().size
        if (fragmentShoppingCartBinding.checkBoxShoppingCartCheckAllList.isChecked != allSelected) {
            fragmentShoppingCartBinding.checkBoxShoppingCartCheckAllList.setOnCheckedChangeListener(
                null
            )
            fragmentShoppingCartBinding.checkBoxShoppingCartCheckAllList.isChecked = allSelected
            settingCheckBox() // 리스너 재등록
        }
        calculateTotals()
    }

    // 총 구매 가격과 수량 계산
    private fun calculateTotals() {
        val selectedItems = shoppingCartAdapter.getSelectedItems()
        val totalSize = selectedItems.sumOf { it.shoppingCartBookQualityCount }
        val totalPrice = selectedItems.sumOf { it.shoppingCartSellingPrice }
        fragmentShoppingCartBinding.apply {
            recyclerViewShoppingCartTotalListSize.text = "총수량 : ${totalSize}개"
            recyclerViewShoppingCartTotalListPrice.text = "총 구매 가격 : ${totalPrice.toCommaString()}원"
        }
    }

    // Toolbar
    private fun settingToolbar() {
        fragmentShoppingCartBinding.apply {
            materialToolbarShoppingCart.setNavigationOnClickListener {
                removeFragment()
            }
            materialToolbarShoppingCart.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.item_shoppingCart_notification -> {
                        replaceMainFragment(NoticeFragment(), true)
                        true
                    }

                    else -> {
                        replaceMainFragment(MainMenuFragment(), true)
                        true
                    }
                }
            }
        }
    }

    // Dialog
    private fun observeProgressDialog() {
        // 화면 입장 시 공지사항 로딩을 위한 Dialog
        shoppingCartViewModel.isLoadShoppingCartList.observe(viewLifecycleOwner) {
            if (it) {
                progressBarDialog.dismiss()
            } else {
                progressBarDialog.show()
            }
        }
    }

    // RecyclerView
    private fun setupRecyclerView() {
        fragmentShoppingCartBinding.apply {
            shoppingCartAdapter =
                ShoppingCartAdapter(
                    emptyList(),
                    this@ShoppingCartFragment,
                    shoppingCartViewModel
                ) { onItemCheckedChange() } // 초기값으로 빈 리스트
            recyclerViewShoppingCartShoppingCartList.run {
                layoutManager = LinearLayoutManager(context)
                adapter = shoppingCartAdapter
            }

        }
    }

    // Observer
    private fun observeBookListData() {
        fragmentShoppingCartBinding.apply {
            // LiveData에서 데이터를 받아와서 RecyclerView 업데이트
            shoppingCartViewModel.shoppingCartBookList.observe(viewLifecycleOwner) { usedBookList ->
                if (usedBookList.isNotEmpty()) {
                    // 데이터를 받아왔을 때, RecyclerView에 전달
                    linearLayoutShoppingCartEmptyShoppingCartList.visibility = View.GONE
                    recyclerViewShoppingCartTotalListSize.visibility = View.VISIBLE
                    recyclerViewShoppingCartTotalListPrice.visibility = View.VISIBLE
                    buttonShoppingCartDeleteList.visibility = View.VISIBLE
                    linearLayoutShoppingCartSelectAll.visibility = View.VISIBLE
                    linearLayoutShoppingCartSelectAll.visibility = View.VISIBLE
                    textViewShoppingCartListInfo.visibility = View.VISIBLE
                    shoppingCartAdapter.updateData(usedBookList.sortedByDescending { it.shoppingCartTime })
                } else {
                    shoppingCartAdapter.updateData(emptyList())
                    // 데이터가 없을 시
                    linearLayoutShoppingCartEmptyShoppingCartList.visibility = View.VISIBLE
                    recyclerViewShoppingCartTotalListSize.visibility = View.GONE
                    recyclerViewShoppingCartTotalListPrice.visibility = View.GONE
                    buttonShoppingCartDeleteList.visibility = View.GONE
                    recyclerViewShoppingCartShoppingCartList.width
                    linearLayoutShoppingCartSelectAll.visibility = View.INVISIBLE
                    textViewShoppingCartListInfo.visibility = View.INVISIBLE

                    // 데이터가 없을 시 최소 heigt 설정
                    val heightInPx = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        300f, // 300dp
                        resources.displayMetrics
                    ).toInt()

                    // RecyclerView에 접근하고 너비를 설정
                    recyclerViewShoppingCartShoppingCartList.layoutParams.height = heightInPx
                }
            }
        }
    }
}