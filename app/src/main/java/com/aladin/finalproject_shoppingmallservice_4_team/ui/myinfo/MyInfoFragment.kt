package com.aladin.finalproject_shoppingmallservice_4_team.ui.myinfo

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.content.edit
import androidx.core.view.isEmpty
import androidx.core.view.isNotEmpty
import androidx.fragment.app.viewModels
import com.aladin.finalproject_shoppingmallservice_4_team.BookApplication
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentMyInfoBinding
import com.aladin.finalproject_shoppingmallservice_4_team.model.UserModel
import com.aladin.finalproject_shoppingmallservice_4_team.ui.ask.AskFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.custom.CustomDialog
import com.aladin.finalproject_shoppingmallservice_4_team.ui.home.HomeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.login.LoginFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.login.SharedPreferencesHelper
import com.aladin.finalproject_shoppingmallservice_4_team.ui.login.UserRepository
import com.aladin.finalproject_shoppingmallservice_4_team.ui.main.MainFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.clearAllBackStack
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceMainFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyInfoFragment : Fragment() {

    lateinit var fragmentMyInfoBinding: FragmentMyInfoBinding

    private val myInfoViewModel: MyInfoViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        fragmentMyInfoBinding = FragmentMyInfoBinding.inflate(inflater, container, false)

        // 로그인 상태 체크
        if (!isUserLoggedIn()) {
            showNotLoggedInDialog()
            return null // 화면 렌더링 중단
        }

        settingUI()
        observeViewModel()

        return fragmentMyInfoBinding.root
    }

//    private fun loadUserData() {
//        val bookApplication = requireActivity().application as BookApplication
//        val userId = bookApplication.loginUserModel.userId
//        myInfoViewModel.loadUserInfo(userId)
//    }

    // 로그인 여부 체크
    private fun isUserLoggedIn(): Boolean {
        return try {
            val bookApplication = requireActivity().application as BookApplication
            bookApplication.loginUserModel.userId.isNotBlank()
        } catch (e: UninitializedPropertyAccessException) {
            false
        }
    }

    private fun showNotLoggedInDialog() {
        val dialog = CustomDialog(
            context = requireContext(),
            contentText = "로그인 후 이용 가능합니다.",
            icon = R.drawable.error_24px,
            positiveText = "확인",
            onPositiveClick = {
                removeFragment()
                replaceMainFragment(LoginFragment(), true)
            }
        )
        dialog.showCustomDialog()
    }

    private fun settingUI() {
        settingToolbarMyInfo()
        populateUserInfo()
        settingLogoutButton()
        settingChangeInfoButton()
        settingKakaoPostCode()
        settingFooterButton()
        settingButtonAccountDelete()
    }

    // 툴바를 세팅하는 메서드
    fun settingToolbarMyInfo() {
        fragmentMyInfoBinding.apply {
            toolbarMyInfo.title = "내 정보"
            toolbarMyInfo.setNavigationIcon(R.drawable.arrow_back_ios_24px)
            toolbarMyInfo.setNavigationOnClickListener {
                if(webViewMyInfoAddress.visibility == View.VISIBLE) {
                    webViewMyInfoAddress.visibility = View.GONE
                } else {
                    removeFragment()
                }
            }
        }
    }

    // 로그아웃 리스너
    fun settingLogoutButton(){
        fragmentMyInfoBinding.apply {
            buttonMyInfoLogout.setOnClickListener {
                handleLogout()
            }
        }
    }

    private fun settingChangeInfoButton() {
        fragmentMyInfoBinding.buttonMyInfoChangeMyInfo.setOnClickListener {
            val newName = fragmentMyInfoBinding.textFieldMyInfoUserName.editText?.text.toString().trim()
            val newAddress = "${fragmentMyInfoBinding.textFieldMyInfoPostCode.editText?.text}/${fragmentMyInfoBinding.textFieldMyInfoAddress1.editText?.text}/${fragmentMyInfoBinding.textFieldMyInfoAddress2.editText?.text}"
            if (validateInput(newName, newAddress)) {
                val bookApplication = requireActivity().application as BookApplication
                val userId = bookApplication.loginUserModel.userId
                myInfoViewModel.updateUserInfo(userId, newName, newAddress)
            }
        }
    }

    fun settingKakaoPostCode(){
        fragmentMyInfoBinding.apply {

            // 도로명 주소 찾기 버튼 눌렀을 때
            buttonMyInfoSearchPostCode.setOnClickListener {

                // webView 투명도 조절
                webViewMyInfoAddress.visibility = View.VISIBLE

                webViewMyInfoAddress.settings.javaScriptEnabled = true
                webViewMyInfoAddress.settings.javaScriptCanOpenWindowsAutomatically = true
                webViewMyInfoAddress.settings.allowUniversalAccessFromFileURLs = true

                webViewMyInfoAddress.addJavascriptInterface(object {
                    @android.webkit.JavascriptInterface
                    fun setAddress(zoneCode: String, fullRoadAddr: String) {
                        // UI 변경은 UI 스레드에서 실행
                        requireActivity().runOnUiThread {
                            Toast.makeText(requireContext(), "우편번호: $zoneCode\n지번 주소: $fullRoadAddr", Toast.LENGTH_LONG).show()

                            // WebView 숨김 처리
                            webViewMyInfoAddress.visibility = View.GONE

                            // 선택한 주소를 입력 필드에 반영
                            textFieldMyInfoPostCode.editText?.setText(zoneCode)
                            textFieldMyInfoAddress1.editText?.setText(fullRoadAddr)
                        }
                    }
                }, "Android")

                webViewMyInfoAddress.webViewClient = object : WebViewClient(){
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        // 페이지가 완전히 로드 되면, JavaScript를 메서드 호출
                        view?.loadUrl("javascript:sample2_execDaumPostcode();")
                    }
                }
                // assets 폴더에 저장된 daum.html 파일을 로드
                webViewMyInfoAddress.loadUrl("file:///android_asset/daum.html")
            }
        }
    }

    // Footer 버튼 관련 메서드
    fun settingFooterButton(){
        fragmentMyInfoBinding.apply {
            // FAQ 버튼
            buttonMyInfoFAQ.setOnClickListener {

            }

            // 1:1 문의 버튼
            buttonMyInfoAsk.setOnClickListener {
                replaceMainFragment(AskFragment(),true)
            }
        }
    }

    //


    private fun observeViewModel() {
        myInfoViewModel.userInfo.observe(viewLifecycleOwner) { user ->
            user?.let {
                val addressParts = it.userAddress.split("/")
                fragmentMyInfoBinding.textFieldMyInfoUserName.editText?.setText(it.userName)
                fragmentMyInfoBinding.textFieldMyInfoPhoneNumber.editText?.setText(it.userPhoneNumber)
                fragmentMyInfoBinding.textFieldMyInfoUserId.editText?.setText(it.userId)
                fragmentMyInfoBinding.textFieldMyInfoPostCode.editText?.setText(addressParts.getOrNull(0) ?: "")
                fragmentMyInfoBinding.textFieldMyInfoAddress1.editText?.setText(addressParts.getOrNull(1) ?: "")
                fragmentMyInfoBinding.textFieldMyInfoAddress2.editText?.setText(addressParts.getOrNull(2) ?: "")
            }
        }

        myInfoViewModel.updateResult.observe(viewLifecycleOwner) { result ->
            val (isUpdated, updatedUser) = result
            if (isUpdated && updatedUser != null) {
                val bookApplication = requireActivity().application as BookApplication
                bookApplication.loginUserModel = updatedUser // BookApplication 객체 업데이트

                val successDialog = CustomDialog(
                    context = requireContext(),
                    contentText = "정보가 성공적으로 업데이트되었습니다.",
                    icon = R.drawable.check_circle_24px, // 성공 아이콘 (사용 가능한 리소스로 변경)
                    positiveText = "확인",
                    onPositiveClick = {
                        // 다이얼로그 닫힘 처리 (필요 시 추가 작업 가능)
                    }
                )
                successDialog.showCustomDialog()
            } else {
                val failureDialog = CustomDialog(
                    context = requireContext(),
                    contentText = "정보 업데이트에 실패했습니다. 다시 시도해주세요.",
                    icon = R.drawable.error_24px, // 실패 아이콘 (사용 가능한 리소스로 변경)
                    positiveText = "확인",
                    onPositiveClick = {
                        // 다이얼로그 닫힘 처리 (필요 시 추가 작업 가능)
                    }
                )
                failureDialog.showCustomDialog()
            }
        }
    }


    private fun validateInput(newName: String, newAddress: String): Boolean {
        fragmentMyInfoBinding.textFieldMyInfoUserName.error = null
        fragmentMyInfoBinding.textFieldMyInfoAddress2.error = null

        var isValid = true
        if (newName.isBlank()) {
            fragmentMyInfoBinding.textFieldMyInfoUserName.error = "이름을 입력해주세요."
            isValid = false
        }
        if (newAddress.split("/").getOrNull(2).isNullOrBlank()) {
            fragmentMyInfoBinding.textFieldMyInfoAddress2.error = "상세 주소를 입력해주세요."
            isValid = false
        }
        return isValid
    }

    // 로그아웃
    private fun handleLogout() {
        val sharedPreferencesHelper = SharedPreferencesHelper(requireContext())
        // 자동 로그인 토큰 제거
        sharedPreferencesHelper.clearAutoLoginToken()

        val bookApplication = requireContext().applicationContext as BookApplication

        // val bookApplication = context as BookApplication
        // 사용자 로그인 상태 초기화 (null 이 안되어서 직접 초기화)
        // bookApplication.loginUserModel = UserModel()

        bookApplication.apply {
            // loginUserModel을 null로 설정하여 완전히 제거
            this::class.java.getDeclaredField("loginUserModel").apply {
                isAccessible = true
                set(bookApplication, null)
            }
        }

        val pref = bookApplication.getSharedPreferences("UserToken", Context.MODE_PRIVATE)
        pref.edit{
            remove("userToken")
            apply()
        }

        // 메인 화면으로 이동
        clearAllBackStack()

        // 알림
        Toast.makeText(requireContext(), "로그아웃 되었습니다", Toast.LENGTH_SHORT).show()
    }


    // 사용자 정보를 텍스트필드에 채우는 메서드
    private fun populateUserInfo(){
        val bookApplication = requireActivity().application as BookApplication
        val user = bookApplication.loginUserModel


        fragmentMyInfoBinding.apply {
            if (user != null) {
                val addressParts = user.userAddress.split("/")
                textFieldMyInfoUserName.editText?.setText(user.userName)
                textFieldMyInfoPhoneNumber.editText?.setText(formatPhoneNumber(user.userPhoneNumber)) // 포맷 적용
                textFieldMyInfoUserId.editText?.setText(user.userId)
                textFieldMyInfoPostCode.editText?.setText(addressParts.getOrNull(0) ?: "") // 우편번호
                textFieldMyInfoAddress1.editText?.setText(addressParts.getOrNull(1) ?: "") // 도로명 주소
                textFieldMyInfoAddress2.editText?.setText(addressParts.getOrNull(2) ?: "") // 상세 주소
            } else {
                // 로그인하지 않은 상태라면
                textFieldMyInfoUserName.editText?.setText("비로그인 사용자")
                textFieldMyInfoPhoneNumber.editText?.setText("-")
                textFieldMyInfoUserId.editText?.setText("-")
                textFieldMyInfoPostCode.editText?.setText("-")
                textFieldMyInfoAddress1.editText?.setText("-")
                textFieldMyInfoAddress2.editText?.setText("-")
            }
        }
    }

    private fun formatPhoneNumber(phoneNumber: String): String {
        val cleanedNumber = phoneNumber.replace("-", "").trim() // 하이픈 제거 및 공백 제거
        return when {
            cleanedNumber.length == 11 -> "${cleanedNumber.substring(0, 3)}-${cleanedNumber.substring(3, 7)}-${cleanedNumber.substring(7)}"
            cleanedNumber.length == 10 -> "${cleanedNumber.substring(0, 3)}-${cleanedNumber.substring(3, 6)}-${cleanedNumber.substring(6)}"
            else -> cleanedNumber // 잘못된 형식이면 그대로 반환
        }
    }


    // 회원탈퇴 화면으로 가는 메서드
    fun settingButtonAccountDelete(){
        fragmentMyInfoBinding.apply {
            buttonMyInfoAccountDelete.setOnClickListener {
                replaceMainFragment(AccountDeleteFragment(),true)
            }
        }
    }

}