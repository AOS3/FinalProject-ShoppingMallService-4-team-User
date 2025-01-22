package com.aladin.finalproject_shoppingmallservice_4_team.ui.findid

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentFindIdBinding
import com.aladin.finalproject_shoppingmallservice_4_team.ui.findpw.FindPwFragment1
import com.aladin.finalproject_shoppingmallservice_4_team.ui.login.LoginFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceSubFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class FindIdFragment : Fragment() {

    lateinit var fragmentFindIdBinding: FragmentFindIdBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        fragmentFindIdBinding = FragmentFindIdBinding.inflate(inflater, container, false)

        settingToolbarFindId()

        buttonConfirmListener()

        return fragmentFindIdBinding.root
    }

    // 툴바를 구성하는 메서드
    fun settingToolbarFindId(){
        fragmentFindIdBinding.apply {
            toolbarFindId.setNavigationIcon(R.drawable.arrow_back_ios_24px)
            toolbarFindId.setNavigationOnClickListener {
                removeFragment()
            }
            toolbarFindId.setTitle("아이디 찾기")
        }
    }

    // 확인 누르면 나오는 메서드
    fun buttonConfirmListener(){
        fragmentFindIdBinding.apply {
            buttonFindIdConfirm.setOnClickListener {
                showFindIdDialog("홍길동", "abc123")
            }
        }
    }

    // 다이얼로그를 표시하는 메서드
     fun showFindIdDialog(userName: String, userId: String) {
        val dialogBuilder = MaterialAlertDialogBuilder(requireContext())
        dialogBuilder.setTitle("아이디 찾기")
        dialogBuilder.setMessage("${userName}님의 아이디는 $userId 입니다.")
        dialogBuilder.setPositiveButton("로그인 하기") { dialog, _ ->
            // 로그인 화면으로 이동
            replaceSubFragment(LoginFragment(),false)
        }
        dialogBuilder.setNegativeButton("비밀번호 찾기") { dialog, _ ->
            // 비밀번호 찾기 화면으로 이동
            replaceSubFragment(FindPwFragment1(),false)
        }
        dialogBuilder.create().show()
    }
}