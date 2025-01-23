package com.aladin.finalproject_shoppingmallservice_4_team.ui.myinfo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isEmpty
import androidx.core.view.isNotEmpty
import com.aladin.finalproject_shoppingmallservice_4_team.BookApplication
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentMyInfoBinding
import com.aladin.finalproject_shoppingmallservice_4_team.model.UserModel
import com.aladin.finalproject_shoppingmallservice_4_team.ui.home.HomeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.login.SharedPreferencesHelper
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceSubFragment

class MyInfoFragment : Fragment() {

    lateinit var fragmentMyInfoBinding: FragmentMyInfoBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        fragmentMyInfoBinding = FragmentMyInfoBinding.inflate(inflater, container, false)

        settingToolbarMyInfo()

        populateUserInfo()

        settingLogoutButton()

        settingButtonAccountDelete()

        settingFooterButton()

        settingButtonChangeMyInfo()

        return fragmentMyInfoBinding.root
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

    // 툴바를 세팅하는 메서드
    fun settingToolbarMyInfo() {
        fragmentMyInfoBinding.apply {
            toolbarMyInfo.title = "내 정보"
            toolbarMyInfo.setNavigationIcon(R.drawable.arrow_back_ios_24px)
            toolbarMyInfo.setNavigationOnClickListener {
                removeFragment()
            }
        }
    }

    // 회원탈퇴 화면으로 가는 메서드
    fun settingButtonAccountDelete(){
        fragmentMyInfoBinding.apply {
            buttonMyInfoAccountDelete.setOnClickListener {
                replaceSubFragment(AccountDeleteFragment(),true)
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

    // 로그아웃
    private fun handleLogout() {
        val sharedPreferencesHelper = SharedPreferencesHelper(requireContext())
        val bookApplication = requireContext().applicationContext as BookApplication

        // 자동 로그인 토큰 제거
        sharedPreferencesHelper.clearAutoLoginToken()

        // 사용자 로그인 상태 초기화 (null 이 안되어서 직접 초기화)
        bookApplication.loginUserModel = UserModel()

        // 로그인 화면으로 이동
        replaceSubFragment(HomeFragment(), false)

        // 알림
        Toast.makeText(requireContext(), "로그아웃 되었습니다", Toast.LENGTH_SHORT).show()
    }

    // 내정보 수정 버튼 세팅
    fun settingButtonChangeMyInfo(){
        fragmentMyInfoBinding.apply {
            buttonMyInfoChangeMyInfo.setOnClickListener {
                // 유효성 검사

                // 이름이 비어져있다면
                if(textFieldMyInfoUserName.editText?.text.toString().isEmpty()){
                    textFieldMyInfoUserName.error = "아이디를 입력해주세요"
                } else if (textFieldMyInfoUserName.editText?.text.toString().isNotEmpty()){
                    textFieldMyInfoUserName.helperText = " "
                }

                // 상세 주소가 비어져있다면
                if(textFieldMyInfoAddress2.editText?.text.toString().isEmpty()){
                    textFieldMyInfoAddress2.error = "상세 주소를 입력해주세요"
                } else if (textFieldMyInfoAddress2.editText?.text.toString().isNotEmpty()){
                    textFieldMyInfoAddress2.helperText = " "
                }

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

            }
        }
    }

}