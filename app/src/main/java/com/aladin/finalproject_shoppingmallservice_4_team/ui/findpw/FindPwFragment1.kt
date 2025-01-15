package com.aladin.finalproject_shoppingmallservice_4_team.ui.findpw

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentFindPw1Binding
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceMainFragment

class FindPwFragment1 : Fragment() {

    lateinit var fragmentFindPw1Binding: FragmentFindPw1Binding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        fragmentFindPw1Binding = FragmentFindPw1Binding.inflate(inflater,container,false)

        settingToolbarFindPw1()

        settingConfirmButton()

        return fragmentFindPw1Binding.root
    }

    // 툴바를 설정하는 메서드
    fun settingToolbarFindPw1(){
        fragmentFindPw1Binding.apply {
            toolbarFindPw1.title = "비밀번호 찾기"
            toolbarFindPw1.setNavigationIcon(R.drawable.arrow_back_ios_24px)
            toolbarFindPw1.setNavigationOnClickListener {
                removeFragment()
            }
        }
    }

    // 확인 버튼 메서드
    fun settingConfirmButton(){
        fragmentFindPw1Binding.apply {
            buttonFindPw1Confirm.setOnClickListener {
                replaceMainFragment(FindPwFragment2(),true)
            }
        }
    }
}