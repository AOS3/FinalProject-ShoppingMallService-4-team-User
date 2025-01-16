package com.aladin.finalproject_shoppingmallservice_4_team.ui.myinfo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.aladin.finalproject_shoppingmallservice_4_team.R
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