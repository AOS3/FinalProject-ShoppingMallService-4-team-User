package com.aladin.finalproject_shoppingmallservice_4_team.ui.ask

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentAskBinding
import com.aladin.finalproject_shoppingmallservice_4_team.ui.main.MainFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceSubFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AskFragment : Fragment() {

    lateinit var fragmentAskBinding: FragmentAskBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        fragmentAskBinding = FragmentAskBinding.inflate(inflater, container, false)

        settingToolbarAsk()

        buttonAskSend()

        return fragmentAskBinding.root
    }

    // 툴바를 세팅하는 메서드
    fun settingToolbarAsk(){
        fragmentAskBinding.apply {
            toolbarAsk.setTitle("1:1 문의 하기")
            toolbarAsk.setNavigationIcon(R.drawable.arrow_back_ios_24px)
            toolbarAsk.setNavigationOnClickListener {
                removeFragment()
            }
        }
    }

    // 문의하기 버튼 메서드
    fun buttonAskSend(){
        fragmentAskBinding.apply {
            buttonAskSend.setOnClickListener {
                // 유효성 검사부터

                // 제목이 있는지
                if(textFieldAskTitle.editText?.text.toString().isEmpty()){
                    textFieldAskTitle.error = "제목을 입력해주세요"
                } else if(textFieldAskTitle?.editText.toString().isNotEmpty()) {
                    textFieldAskTitle.error = null
                    textFieldAskTitle.helperText = " "
                }

                // 내용이 있는지
                if(textFieldAskSummary.editText?.text.toString().isEmpty()){
                    textFieldAskSummary.error = "내용을 입력해주세요"
                } else if(textFieldAskSummary?.editText.toString().isNotEmpty()) {
                    textFieldAskSummary.error = null
                    textFieldAskSummary.helperText = " "
                }

                // 이메일이 있는지
                if(textFieldAskEmail.editText?.text.toString().isEmpty()){
                    textFieldAskEmail.error = "이메일을 입력해주세요"
                } else if(textFieldAskEmail?.editText.toString().isNotEmpty()) {
                    textFieldAskEmail.error = null
                    textFieldAskEmail.helperText = "답변은 적어주신 이메일로 보내드리겠습니다"
                }

                // 오류가 없다면
                if(textFieldAskTitle.error == null && textFieldAskSummary.error == null && textFieldAskEmail.error == null){
                    val dialogBuilder = MaterialAlertDialogBuilder(requireContext())
                    dialogBuilder.setTitle("문의 하기")
                    dialogBuilder.setMessage("문의 하시겠습니까?")
                    dialogBuilder.setNegativeButton("취소"){ dialogInterface, i ->
                        dialogInterface.dismiss()
                    }
                    dialogBuilder.setPositiveButton("확인"){ dialogInterface, i ->
                        replaceSubFragment(MainFragment(),false)
                    }
                    dialogBuilder.show()
                }
            }
        }
    }
}