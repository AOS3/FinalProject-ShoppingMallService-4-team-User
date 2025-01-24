package com.aladin.finalproject_shoppingmallservice_4_team.ui.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentRegisterStep1Binding
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceMainFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceSubFragment


class RegisterStep1Fragment : Fragment() {

    lateinit var fragmentRegisterStep1Binding: FragmentRegisterStep1Binding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        fragmentRegisterStep1Binding = FragmentRegisterStep1Binding.inflate(inflater,container,false)

        settingRegister1Toolbar()

        settingTextViewRegister1Terms()

        gotoRegister2()

        return fragmentRegisterStep1Binding.root
    }

    // 툴바를 세팅하는 메서드
    fun settingRegister1Toolbar(){
        fragmentRegisterStep1Binding.apply {
            toolbarRegisterStep1.setNavigationIcon(R.drawable.arrow_back_ios_24px)
            toolbarRegisterStep1.setNavigationOnClickListener {
                removeFragment()
            }
        }
    }

    // 이용약관을 설정하는 메서드
    fun settingTextViewRegister1Terms(){
        fragmentRegisterStep1Binding.apply {
            val termsOfUse = """
                책방마켓의 회원이 되시려면 몇가지 규칙이 있습니다
                
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
            textViewRegister1Terms.text = termsOfUse
        }
    }

    // 회원가입 2화면으로 이동하는 메서드
    fun gotoRegister2(){
        fragmentRegisterStep1Binding.apply {
            buttonRegister1Agree.setOnClickListener {
                replaceMainFragment(RegisterStep2Fragment(),true)
            }
        }
    }

}