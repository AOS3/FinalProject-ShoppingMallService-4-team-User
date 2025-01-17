package com.aladin.finalproject_shoppingmallservice_4_team.ui.myinfo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentAccountDeleteBinding
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment

class AccountDeleteFragment : Fragment() {

    lateinit var fragmentAccountDeleteBinding: FragmentAccountDeleteBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        fragmentAccountDeleteBinding = FragmentAccountDeleteBinding.inflate(inflater, container, false)

        settingAccountDeleteToolbar()

        settingTextViewAccountDeleteTermsOfUse()

        settingButtonAccountDelete()

        settingFooterButton()

        return fragmentAccountDeleteBinding.root
    }

    // 툴바를 구성하는 메서드
    fun settingAccountDeleteToolbar() {
        fragmentAccountDeleteBinding.apply {
            toolbarAccountDelete.setTitle("탈퇴하기")
            toolbarAccountDelete.setNavigationIcon(R.drawable.arrow_back_ios_24px)
            toolbarAccountDelete.setNavigationOnClickListener {
                removeFragment()
            }
        }
    }

    // 탈퇴약관 설정하는 메서드
    fun settingTextViewAccountDeleteTermsOfUse(){
        fragmentAccountDeleteBinding.apply {
            val termsOfUse = """
                책방마켓의 회원을 탈퇴하려면 몇가지 규칙이 있습니다
                
                규칙 1.
                
                규칙 2.
                
                규칙 3.
                 
                규칙 4.
                
                규칙 5.
                
                규칙 6.
                
                규칙 7.
                
                규칙 8.
                
                규칙 9.
                
                규칙 10.
                
            """.trimIndent()
            textViewAccountDeleteTermsOfUse.text = termsOfUse
        }
    }

    // 탈퇴하기 버튼 메서드
    fun settingButtonAccountDelete() {
        fragmentAccountDeleteBinding.apply {
            buttonAccountDeleteDeleteAccount.setOnClickListener {
                // 비밀번호를 입력 안했다면
                if(textFieldAccountDeletePassword.editText?.text.toString().isEmpty()) {
                    textFieldAccountDeletePassword.error = "비밀번호를 입력해주세요"
                } else {
                    // 비밀번호가 같지않다면 메서드 구현해야하고...

                    // 다이얼로그 띄워야함
                }
            }
        }
    }

    // Footer 버튼들 메서드
    fun settingFooterButton() {
        fragmentAccountDeleteBinding.apply {
            // 1:1 문의 버튼
            buttonAccountDeleteAsk.setOnClickListener {

            }

            // FAQ 버튼
            buttonAccountDeleteFAQ.setOnClickListener {

            }
        }
    }

}