package com.aladin.finalproject_shoppingmallservice_4_team.ui.bookOrder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.aladin.finalproject_shoppingmallservice_4_team.BookApplication
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentBookOrder2Binding
import com.aladin.finalproject_shoppingmallservice_4_team.ui.adapter.BookOrderStatementAdapter
import com.aladin.finalproject_shoppingmallservice_4_team.ui.custom.CustomDialog
import com.aladin.finalproject_shoppingmallservice_4_team.ui.home.HomeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.login.LoginFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.clearAllBackStack
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceMainFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceSubFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookOrderFragment2 : Fragment() {
    private lateinit var fragmentBookOrder2Binding: FragmentBookOrder2Binding
    private val bookOrderViewModel: BookOrderViewModel by viewModels()
    private lateinit var bookApplication: BookApplication
    private lateinit var bookOrderStatementAdapter: BookOrderStatementAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBookOrder2Binding =
            FragmentBookOrder2Binding.inflate(layoutInflater, container, false)
        // bookApplication 초기화
        bookApplication = requireActivity().application as BookApplication

        // 로그인 체크
        checkLoginProcess()

        // 옵저버
        settingObserver()

        // 리싸이클러뷰
        setupRecyclerView()

        // 계속 쇼핑하기 버튼
        onClickKeepShopping()

        return fragmentBookOrder2Binding.root
    }

    // onClickKeepShopping
    private fun onClickKeepShopping() {
        fragmentBookOrder2Binding.apply {
            buttonBookOrderBuyBook.setOnClickListener {
                clearAllBackStack()
                replaceSubFragment(HomeFragment(),false)
            }
        }
    }

    // RecyclerView
    private fun setupRecyclerView() {
        fragmentBookOrder2Binding.apply {
            bookOrderStatementAdapter =
                BookOrderStatementAdapter(
                    emptyList()
                )
            recyclerViewBookOrder2BookList.run {
                layoutManager = LinearLayoutManager(context)
                adapter = bookOrderStatementAdapter
            }

        }
    }

    // Check Login
    private fun checkLoginProcess() {
        try {
            if (::bookApplication.isInitialized && bookApplication.loginUserModel != null) {
                if (arguments != null) {
                    val userToken = bookApplication.loginUserModel.userToken
                    val userBuyTime = arguments?.getLong("orderInquiryTime")!!
                    bookOrderViewModel.gettingUserInquiryDataList(userToken, userBuyTime)
                }
            } else {
                if (arguments != null) {
                    val userToken = bookApplication.loginUserModel.userToken
                    val userBuyTime = arguments?.getLong("orderInquiryTime")!!
                    bookOrderViewModel.gettingUserInquiryDataList(userToken, userBuyTime)
                }
            }
        } catch (e: Exception) {
            bookOrderViewModel.dismissProgressDialog()
            val loginDialog = CustomDialog(
                requireContext(),
                onPositiveClick = {
                    removeFragment()
                },
                contentText = "로그인을 먼저 진행해주세요.",
                icon = R.drawable.error_24px
            )
            loginDialog.showCustomDialog()
        }
    }

    // Observer
    private fun settingObserver() {
        fragmentBookOrder2Binding.apply {
            bookOrderViewModel.userInquiryBookList.observe(viewLifecycleOwner) { orderBookList ->
                if (orderBookList.isNotEmpty()) {
                    fragmentBookOrder2Binding.apply {
                        // 사용자 정보 Set
                        val userAddress = bookApplication.loginUserModel.userAddress.split("/")
                        textFieldBookOrder2Name.editText?.setText(bookApplication.loginUserModel.userName)
                        textFieldBookOrder2PostCode.editText?.setText(userAddress[0])
                        textFieldBookOrder2RoadNameAddress.editText?.setText(userAddress[1])
                        textFieldBookOrder2DetailAddress.editText?.setText(userAddress[2])
                        val formattedNumber =
                            bookApplication.loginUserModel.userPhoneNumber.replace(
                                "(\\d{3})(\\d{4})(\\d{4})".toRegex(),
                                "$1-$2-$3"
                            )
                        textFieldBookOrder2PhoneNumber.editText?.setText(formattedNumber)
                    }

                    // 예시로 argument로 받은 데이터 ("책 제목/책 링크" 형식)
                    val bookCoverList = arguments?.getStringArrayList("userCoverList")

                    // 데이터 가공 처리 진행
                    val processedBooks =
                        bookOrderViewModel.processBookDataFromArguments(bookCoverList)

                    // 데이터를 받았을 때
                    bookOrderStatementAdapter =
                        BookOrderStatementAdapter(orderBookList, processedBooks)
                    recyclerViewBookOrder2BookList.adapter = bookOrderStatementAdapter
                    // 전체 수량 및 구매 가격 Text Set
                    textViewBookOrder2TotalSize.setText("총 수량: ${bookOrderViewModel.userInquiryBookTotalCount.sum()}개")
                    textViewBookOrder2TotalPrice.setText("총 구매 가격: ${bookOrderViewModel.userInquiryBookTotalPrice.sum()}원")
                }
            }
        }
    }
}