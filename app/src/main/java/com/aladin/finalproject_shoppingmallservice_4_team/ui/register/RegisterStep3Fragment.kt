package com.aladin.finalproject_shoppingmallservice_4_team.ui.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentRegisterStep3Binding
import com.aladin.finalproject_shoppingmallservice_4_team.ui.login.LoginFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceMainFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceSubFragment

class RegisterStep3Fragment : Fragment() {

    lateinit var fragmentRegisterStep3Binding: FragmentRegisterStep3Binding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        fragmentRegisterStep3Binding = FragmentRegisterStep3Binding.inflate(inflater,container,false)

        settingTextViewRegister3Welcome()

        settingButtonRegister3Login()

        return fragmentRegisterStep3Binding.root
    }

    // 이용약관을 설정하는 메서드
    fun settingTextViewRegister3Welcome(){
        fragmentRegisterStep3Binding.apply {
            val welcomeText = """
                책방마켓의 회원이 되신것을 환영합니다
                
                우리 책방 마켓은 어쩌고 저쩌고..
                
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
            textViewRegisterStep3Welcome.text = welcomeText
        }
    }

    // 로그인하러가기 버튼 리스너
    fun settingButtonRegister3Login(){
        fragmentRegisterStep3Binding.apply {
            buttonRegisterStep3Login.setOnClickListener {
                removeFragment()
                removeFragment()
                removeFragment()
            }
        }
    }

}