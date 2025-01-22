package com.aladin.finalproject_shoppingmallservice_4_team.ui.findpw

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentFindPw2Binding
import com.aladin.finalproject_shoppingmallservice_4_team.ui.login.LoginFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceSubFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class FindPwFragment2 : Fragment() {

    lateinit var fragmentFindPw2Binding: FragmentFindPw2Binding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        fragmentFindPw2Binding = FragmentFindPw2Binding.inflate(inflater,container,false)

        settingToolbarFindPw2()

        settingConfirmButton()

        return fragmentFindPw2Binding.root
    }

    // 툴바를 설정하는 메서드
    fun settingToolbarFindPw2(){
        fragmentFindPw2Binding.apply {
            toolbarFindPw2.title = "비밀번호 재설정"
            toolbarFindPw2.setNavigationIcon(R.drawable.arrow_back_ios_24px)
            toolbarFindPw2.setNavigationOnClickListener {
                val dialogBuilder = MaterialAlertDialogBuilder(requireContext())
                dialogBuilder.setTitle("아이디 찾기")
                dialogBuilder.setMessage("정말 뒤로 가시겠습니까?")
                dialogBuilder.setPositiveButton("확인") { dialog, _ ->
                    // 뒤로감
                    removeFragment()
                    dialog.dismiss()
                }
                dialogBuilder.setNegativeButton("취소") { dialog, _ ->
                    // 다이얼로그 내리기
                    dialog.dismiss()
                }
                dialogBuilder.create().show()

            }
        }
    }

    // 확인 버튼 메서드
    fun settingConfirmButton(){
        fragmentFindPw2Binding.apply {
            buttonFindPw2Confirm.setOnClickListener {
                val dialogBuilder = MaterialAlertDialogBuilder(requireContext())
                dialogBuilder.setTitle("비밀번호 재설정")
                dialogBuilder.setMessage("비밀번호 재설정이 완료 되었습니다")
                dialogBuilder.setPositiveButton("확인") { dialog, _ ->
                    // 뒤로감
                    replaceSubFragment(LoginFragment(),false)
                    dialog.dismiss()
                }
                dialogBuilder.create().show()
            }
        }

    }

}