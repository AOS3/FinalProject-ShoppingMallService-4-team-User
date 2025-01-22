package com.aladin.finalproject_shoppingmallservice_4_team.ui.bookOrder

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.aladin.finalproject_shoppingmallservice_4_team.BookApplication
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentBookOrder1Binding
import com.aladin.finalproject_shoppingmallservice_4_team.ui.adapter.BookOrderAdapter
import com.aladin.finalproject_shoppingmallservice_4_team.ui.custom.CustomDialog
import com.aladin.finalproject_shoppingmallservice_4_team.ui.custom.CustomDialogProgressbar
import com.aladin.finalproject_shoppingmallservice_4_team.ui.home.HomeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.mainMenu.MainMenuFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.notice.NoticeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceMainFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceSubFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.showSoftInput
import com.aladin.finalproject_shoppingmallservice_4_team.util.toCommaString
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookOrderFragment1 : Fragment() {

    private lateinit var fragmentBookOrder1Binding: FragmentBookOrder1Binding
    private lateinit var bookOrderAdapter: BookOrderAdapter
    private lateinit var bookApplication: BookApplication
    private val bookOrderViewModel: BookOrderViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBookOrder1Binding =
            FragmentBookOrder1Binding.inflate(layoutInflater, container, false)

        // bookApplication 초기화
        bookApplication = requireActivity().application as BookApplication

        // 로그인 검사
        checkLoginProcess()

        // 유저 정보 가져오기
        settingUserData()

        // 로딩 다이얼 로그
        observeProgressDialog()

        // 리싸이클러뷰
        setupRecyclerView()

        // 옵저버
        settingObserver()

        // 툴바 설정
        settingToolbar()

        // 구매하기 버튼 클릭
        onClickOrderButton()

        // 도로명 검색 버튼 클릭
        settingKakaoPost()

        formatPhoneNumberRealTime(fragmentBookOrder1Binding.textFieldBookOrderPhoneNumber.editText!!)
        return fragmentBookOrder1Binding.root
    }

    private fun settingKakaoPost() {
        fragmentBookOrder1Binding.apply {
            buttonBookOrderPostCode.setOnClickListener {
                webViewBookOrderFindAddress.bringToFront()
                webViewBookOrderFindAddress.visibility = View.VISIBLE
                webViewBookOrderFindAddress.settings.javaScriptEnabled = true // JavaScript 허용
                webViewBookOrderFindAddress.settings.javaScriptCanOpenWindowsAutomatically =
                    true // 새로운 창 자동 열기 허용
                webViewBookOrderFindAddress.settings.allowUniversalAccessFromFileURLs = true

                // JavaScript와 안드로이드 앱 간의 인터페이스 설정
                webViewBookOrderFindAddress.addJavascriptInterface(object {
                    @android.webkit.JavascriptInterface
                    // 지역 클릭 시 호출되는 함수
                    fun setAddress(zoneCode: String, fullRoadAddr: String) {
                        requireActivity().runOnUiThread {
                            Toast.makeText(
                                requireContext(),
                                "우편번호: $zoneCode\n지번 주소: $fullRoadAddr",
                                Toast.LENGTH_LONG
                            ).show()

                            // WebView 숨김 처리
                            webViewBookOrderFindAddress.visibility = View.GONE

                            // 선택한 주소를 입력 필드에 반영
                            textFieldBookOrderPostCode.editText?.setText(zoneCode)
                            textFieldBookOrderRoadNameAddress.editText?.setText(fullRoadAddr)
                        }
                    }
                }, "Android")

                webViewBookOrderFindAddress.webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        // 페이지가 완전히 로드되면, JavaScript 함수 호출
                        view?.loadUrl("javascript:sample2_execDaumPostcode();")
                    }
                }

                // assets 폴더에 저장된 daum.html 파일을 로드
                webViewBookOrderFindAddress.loadUrl("file:///android_asset/daum.html")
            }
        }
    }

    // setUserData
    private fun settingUserData() {
        fragmentBookOrder1Binding.apply {
            val userAddress = bookApplication.loginUserModel.userAddress.split("/")
            textFieldBookOrderName.editText?.setText(bookApplication.loginUserModel.userName)
            val formattedNumber = bookApplication.loginUserModel.userPhoneNumber.replace(
                "(\\d{3})(\\d{4})(\\d{4})".toRegex(),
                "$1-$2-$3"
            )
            textFieldBookOrderPhoneNumber.editText?.setText(formattedNumber)
            textFieldBookOrderPostCode.editText?.setText(userAddress[0])
            textFieldBookOrderRoadNameAddress.editText?.setText(userAddress[1])
            textFieldBookOrderDetailAddress.editText?.setText(userAddress[2])
        }
    }

    // RecyclerView
    private fun setupRecyclerView() {
        fragmentBookOrder1Binding.apply {
            bookOrderAdapter =
                BookOrderAdapter(
                    emptyList()
                )
            recyclerViewBookOrderBookList.run {
                layoutManager = LinearLayoutManager(context)
                adapter = bookOrderAdapter
            }

        }
    }

    // Observer
    private fun settingObserver() {
        fragmentBookOrder1Binding.apply {
            bookOrderViewModel.orderBookList.observe(viewLifecycleOwner) { orderBookList ->
                if (orderBookList.isNotEmpty()) {
                    // 데이터를 받았을 때
                    bookOrderAdapter =
                        BookOrderAdapter(orderBookList)
                    recyclerViewBookOrderBookList.adapter = bookOrderAdapter
                }
            }

            bookOrderViewModel.totalSellingPrice.observe(viewLifecycleOwner) { totalPrice ->
                fragmentBookOrder1Binding.textViewBookOrderTotalPrice.text =
                    "총 구매 가격 : ${totalPrice.toCommaString()}원"
            }

            bookOrderViewModel.totalBookQualityCount.observe(viewLifecycleOwner) { totalCount ->
                fragmentBookOrder1Binding.textViewBookOrderTotalSize.text = "총 수량 : ${totalCount}개"
            }
        }
    }

    // Progress Dialog
    private fun observeProgressDialog() {
        // 화면 입장 시 공지사항 로딩을 위한 Dialog
        val progressBarDialog = CustomDialogProgressbar(requireContext())
        progressBarDialog.show()

        bookOrderViewModel.isLoadOrderDataList.observe(viewLifecycleOwner) {
            if (it) {
                progressBarDialog.dismiss()
            }
        }
    }

    // Check Login
    private fun checkLoginProcess() {
        try {
            if (::bookApplication.isInitialized && bookApplication.loginUserModel != null) {
                bookOrderViewModel.gettingUserOrderListBookData(bookApplication.loginUserModel.userToken)
            } else {
                bookOrderViewModel.gettingUserOrderListBookData(bookApplication.loginUserModel.userToken)
            }
        } catch (e: Exception) {
            bookOrderViewModel.dismissProgressDialog()
            val loginDialog = CustomDialog(
                requireContext(),
                onPositiveClick = {
                    replaceSubFragment(HomeFragment(), false)
                },
                contentText = "로그인을 먼저 진행해주세요.",
                icon = R.drawable.error_24px
            )
            loginDialog.showCustomDialog()
        }
    }

    // Toolbar
    private fun settingToolbar() {
        fragmentBookOrder1Binding.apply {
            materialToolbarBookOrder.setNavigationOnClickListener {
                removeFragment()
            }
            materialToolbarBookOrder.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.item_shoppingCart_notification -> {
                        replaceSubFragment(NoticeFragment(), true)
                        true
                    }

                    else -> {
                        replaceSubFragment(MainMenuFragment(), true)
                        true
                    }
                }
            }
        }
    }

    // TextWatcher For PhoneNumber
    private fun formatPhoneNumberRealTime(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            private var isEditing = false // 중복 호출 방지

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isEditing) return

                val input = s.toString().replace("-", "") // 하이픈 제거
                if (input.length > 11) return // 전화번호는 최대 11자리

                isEditing = true
                val formatted = when {
                    input.length >= 8 -> input.replace(Regex("(\\d{3})(\\d{4})(\\d+)"), "$1-$2-$3")
                    input.length >= 4 -> input.replace(Regex("(\\d{3})(\\d+)"), "$1-$2")
                    else -> input
                }
                editText.setText(formatted)
                editText.setSelection(formatted.length) // 커서를 끝으로 이동
                isEditing = false
            }
        })
    }

    // ClickOrderButton
    private fun onClickOrderButton() {
        fragmentBookOrder1Binding.apply {
            buttonBookOrderBuyBook.setOnClickListener {
                // 구매자 정보는 기존 유저 정보랑 다를수 있기 때문에 비어있는지만 검사
                // 이름 검사
                if (textFieldBookOrderName.editText?.text?.isEmpty()!!) {
                    textFieldBookOrderName.error = "이름을 다시 확인해주세요."
                    textFieldBookOrderName.editText?.showSoftInput()
                    return@setOnClickListener
                } else {
                    textFieldBookOrderName.error = null
                }

                // 전화번호 검사
                if (textFieldBookOrderPhoneNumber.editText?.text?.isEmpty()!!) {
                    textFieldBookOrderPhoneNumber.error = "전화번호를 다시 확인해주세요."
                    textFieldBookOrderPhoneNumber.editText?.showSoftInput()
                    return@setOnClickListener
                } else {
                    textFieldBookOrderPhoneNumber.error = null
                }

                // 우편번호
                if (textFieldBookOrderPostCode.editText?.text?.isEmpty()!!) {
                    textFieldBookOrderPostCode.error = "우편번호를 다시 확인해주세요."
                    textFieldBookOrderPostCode.editText?.showSoftInput()
                    return@setOnClickListener
                } else {
                    textFieldBookOrderPostCode.error = null
                }

                // 도로명 주소
                if (textFieldBookOrderRoadNameAddress.editText?.text?.isEmpty()!!) {
                    textFieldBookOrderRoadNameAddress.error = "도로명 주소를 다시 확인해주세요."
                    textFieldBookOrderRoadNameAddress.editText?.showSoftInput()
                    return@setOnClickListener
                } else {
                    textFieldBookOrderRoadNameAddress.error = null
                }

                // 상세 주소
                if (textFieldBookOrderDetailAddress.editText?.text?.isEmpty()!!) {
                    textFieldBookOrderDetailAddress.error = "상세 주소를 다시 확인해주세요."
                    textFieldBookOrderDetailAddress.editText?.showSoftInput()
                    return@setOnClickListener
                } else {
                    textFieldBookOrderDetailAddress.error = null
                }

                // 사용자 주소 1/2/3로 정렬
                val userAddress =
                    "${textFieldBookOrderPostCode.editText?.text}/${textFieldBookOrderRoadNameAddress.editText?.text}/${textFieldBookOrderDetailAddress.editText?.text}"
                // 사용자 전화번호 하이픈(-) 빠지게 설정
                val phoneNumber =
                    textFieldBookOrderPhoneNumber.editText?.text.toString().replace("-", "")

                // 사용자 데이터 저장
                bookOrderViewModel.getUserInfoData(
                    bookApplication.loginUserModel.userToken,
                    textFieldBookOrderName.editText?.text!!.trim().toString(),
                    phoneNumber,
                    userAddress
                )
                bookOrderViewModel.saveOrderDataToOrderInquiryTable()

                // 저장 완료시 띄울 ProgressDialog
                val successProgressBarDialog = CustomDialogProgressbar(requireContext())
                successProgressBarDialog.show()

                bookOrderViewModel.isSuccessOrder.observe(viewLifecycleOwner) { (first, second) ->
                    if (first) {
                        when (second) {
                            // 성공할 경우
                            0 -> {
                                successProgressBarDialog.dismiss()
                                val dataBundle = Bundle()
                                dataBundle.putLong(
                                    "orderInquiryTime",
                                    bookOrderViewModel.todayCurrentTime)
                                dataBundle.putStringArrayList(
                                    "userCoverList",
                                    ArrayList(bookOrderViewModel.inquiryBookCoverList) // List를 ArrayList로 변환
                                )
                                dataBundle.putStringArrayList(
                                    "userCountList",
                                    ArrayList(bookOrderViewModel.inquiryBookCountList) // List를 ArrayList로 변환
                                )

                                replaceMainFragment(BookOrderFragment2(), false, dataBundle)
                            }
                            // 실패할 경우
                            else -> {
                                successProgressBarDialog.dismiss()
                                val loginDialog = CustomDialog(
                                    requireContext(),
                                    // 리스트 삭제 진행
                                    onPositiveClick = {
                                        removeFragment()
                                    },
                                    contentText = "일시적인 오류로 인해 구매를 실패하였습니다. 다시 구매해주세요.",
                                    icon = R.drawable.error_24px
                                )
                                loginDialog.showCustomDialog()
                            }
                        }
                    }
                }
            }
        }
    }
}