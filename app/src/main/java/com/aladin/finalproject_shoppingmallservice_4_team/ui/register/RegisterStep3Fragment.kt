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
                
                책방마켓에 오신 것을 환영합니다!
                
                중고서적 거래의 새로운 경험을 시작하세요.
                
                책방마켓은 개인 간 거래가 아닌, 우리 어플이 중간에서 책을 매입하고 판매하는 방식을 사용합니다.
                판매 가능한 책의 기준은 깨끗한 상태와 정품 여부입니다.
                
                알라딘 API를 통해 원하는 책을 검색하세요.
                판매 등록 시 책 상태와 세부 정보를 정확히 기입해주세요.
                
                매입가는 책 상태에 따라 책방마켓이 자동 산정합니다.
                책방마켓 재고 내의 책은 사용자들이 적정가로 구매할 수 있습니다.
                
                위조/복제본 등록 금지
                허위 정보 제공 금지
                기타 불법 행위 금지
                
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