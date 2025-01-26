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
                서비스 이용 약관
                
                모든 회원은 제공되는 서비스 이용 시 관련 법규를 준수해야 합니다.
                위반 시 서비스 이용이 제한될 수 있습니다.

                개인정보 보호
                회원의 개인정보는 서비스 제공 목적으로만 사용됩니다.
                법적 요건에 따라 동의 없이 외부에 제공되지 않습니다.
                
                회원 책임
                회원 계정 및 비밀번호 관리 책임은 회원 본인에게 있습니다.
                계정 도용 및 부주의로 인한 피해는 책임지지 않습니다.
                
                콘텐츠 이용 제한
                서비스 내 제공된 콘텐츠의 무단 복제 및 배포를 금지합니다.
                위반 시 법적 조치가 취해질 수 있습니다.
                
                서비스 변경
                회사는 필요 시 사전 공지 후 서비스 내용을 변경할 수 있습니다.
                
                계약 해지
                회원은 언제든 서비스 탈퇴를 요청할 수 있습니다.
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