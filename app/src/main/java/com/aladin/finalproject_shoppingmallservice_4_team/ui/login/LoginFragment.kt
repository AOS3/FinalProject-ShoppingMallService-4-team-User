package com.aladin.finalproject_shoppingmallservice_4_team.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentLoginBinding
import com.aladin.finalproject_shoppingmallservice_4_team.ui.findid.FindIdFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.findpw.FindPwFragment1
import com.aladin.finalproject_shoppingmallservice_4_team.ui.register.RegisterStep1Fragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceMainFragment

class LoginFragment : Fragment() {

    lateinit var fragmentLoginBinding: FragmentLoginBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentLoginBinding = FragmentLoginBinding.inflate(layoutInflater,container,false)

        settingLoginToolbar()

        settingButtons()

        return fragmentLoginBinding.root
    }

    // 툴바를 세팅하는 메서드
    fun settingLoginToolbar(){
        fragmentLoginBinding.apply {
            materialToolbarLogin.title = "로그인"
            materialToolbarLogin.setNavigationIcon(R.drawable.arrow_back_ios_24px)
        }
    }

    // 버튼 리스너들
    fun settingButtons(){
        fragmentLoginBinding.apply {
            // 아이디 찾기 화면으로 이동
            buttonLoginFindId.setOnClickListener {
                replaceMainFragment(FindIdFragment(),true)
            }

            // 비밀번호 찾기 화면으로 이동
            buttonLoginFindPw.setOnClickListener {
                replaceMainFragment(FindPwFragment1(),true)
            }

            // 회원가입 화면으로 이동
            buttonLoginRegister.setOnClickListener {
                replaceMainFragment(RegisterStep1Fragment(),true)
            }
        }
    }



}