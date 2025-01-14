package com.aladin.finalproject_shoppingmallservice_4_team.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    lateinit var fragmentLoginBinding: FragmentLoginBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentLoginBinding = FragmentLoginBinding.inflate(layoutInflater,container,false)

        settingLoginToolbar()

        return fragmentLoginBinding.root
    }

    // 툴바를 세팅하는 메서드
    fun settingLoginToolbar(){
        fragmentLoginBinding.apply {
            materialToolbarLogin.title = "로그인"
            materialToolbarLogin.setNavigationIcon(R.drawable.arrow_back_ios_24px)
        }
    }


}