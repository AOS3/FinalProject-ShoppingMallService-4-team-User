package com.aladin.finalproject_shoppingmallservice_4_team.ui.myinfo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isEmpty
import androidx.core.view.isNotEmpty
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentMyInfoBinding
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceSubFragment

class MyInfoFragment : Fragment() {

    lateinit var fragmentMyInfoBinding: FragmentMyInfoBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        fragmentMyInfoBinding = FragmentMyInfoBinding.inflate(inflater, container, false)

        settingToolbarMyInfo()

        settingButtonAccountDelete()

        settingFooterButton()

        settingButtonChangeMyInfo()

        return fragmentMyInfoBinding.root
    }

    // 툴바를 세팅하는 메서드
    fun settingToolbarMyInfo() {
        fragmentMyInfoBinding.apply {
            toolbarMyInfo.title = "내 정보"
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