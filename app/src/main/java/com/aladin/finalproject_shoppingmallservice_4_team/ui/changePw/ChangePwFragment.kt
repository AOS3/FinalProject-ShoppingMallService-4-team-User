package com.aladin.finalproject_shoppingmallservice_4_team.ui.changePw

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentChangePwBinding
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.showSoftInput
import com.google.android.material.textfield.TextInputLayout


class ChangePwFragment : Fragment() {
    private lateinit var fragmentChangePwBinding: FragmentChangePwBinding
    private lateinit var changePwViewModel: ChangePwViewModel
    private lateinit var changePwRepository: ChangePwRepository
    // 화면 입장 시 비동기로 원래 비밀번호 받아와서 변수에 저장 후 실시간 비밀번호 비교
    // 비밀번호 변경 버튼 클릭시에만 유효성 검사 진행 예정

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentChangePwBinding = FragmentChangePwBinding.inflate(layoutInflater, container, false)

        // 초기화
        initialize()

        // 화면들어왔을 때 Focus 설정
        fragmentChangePwBinding.textFieldChangePwCurrentPassword.editText?.showSoftInput() // 키보드 띄우기

        // 툴바 세팅
        settingToolbar()

        // 비밀번호 변경 버튼
        onClickChangePwButton()

        // 값 변경 시 viewModel에 값을 담아준다.
        setTextFieldChangeListener()

        return fragmentChangePwBinding.root
    }

    // initialize
    private fun initialize() {
        changePwRepository = ChangePwRepository()
        changePwViewModel = ChangePwViewModel(changePwRepository)
    }

    // Toolbar
    private fun settingToolbar() {
        fragmentChangePwBinding.apply {
            materialToolbarChangePw.setNavigationOnClickListener {
                removeFragment()
            }
        }
    }

    // TextChangeListener
    private fun setTextFieldChangeListener() {
        fragmentChangePwBinding.apply {
            textFieldChangePwCurrentPassword.editText?.addTextChangedListener {
                changePwViewModel.currentUserPassword.value = it.toString()
                textFieldChangePwCurrentPassword.error = null
            }

            textFieldChangePwNewPassword.editText?.addTextChangedListener {
                changePwViewModel.newUserPassword.value = it.toString()
                textFieldChangePwNewPassword.error = null
            }

            textFieldChangePwCheckNewPassword.editText?.addTextChangedListener {
                changePwViewModel.newCheckUserPassword.value = it.toString()
                textFieldChangePwCheckNewPassword.error = null
            }
        }
    }


    // ChangePw
    private fun onClickChangePwButton() {
        fragmentChangePwBinding.buttonChangePw.setOnClickListener {

            fragmentChangePwBinding.apply {
                // 현재 비밀번호 검사
                if (!changePwViewModel.checkCurrentPassword(textFieldChangePwCurrentPassword.editText?.text.toString())) {
                    textFieldChangePwCurrentPassword.error = "잘못된 비밀번호 입니다."
                    textFieldChangePwCurrentPassword.editText?.showSoftInput() // 키보드 띄우기
                    return@setOnClickListener
                }

                // 새로운 비밀번호 검사
                // 문자와 숫자를 포함하고 8글자 이상 16글자 이하 인지 검사
                if (!textFieldChangePwNewPassword.editText?.text.toString()
                        .matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,16}$".toRegex())
                ) {
                    textFieldChangePwNewPassword.error = "문자,숫자를 활용한 8글자 이상 16글자 이하를 입력해주세요."
                    textFieldChangePwNewPassword.editText?.showSoftInput() // 키보드 띄우기
                    return@setOnClickListener
                }

                // 새로운 비밀번호 재입력 검사
                if (textFieldChangePwCheckNewPassword.editText?.text.toString() != textFieldChangePwNewPassword.editText?.text.toString()) {
                    textFieldChangePwCheckNewPassword.error = "두 비밀번호가 다릅니다."
                    textFieldChangePwCheckNewPassword.editText?.showSoftInput() // 키보드 띄우기
                    return@setOnClickListener
                }

                // 모든 조건 통과시 비밀번호 변경 메서드 실행
                changePwViewModel.changeUserPw(changePwViewModel.newUserPassword.value.toString())
            }
        }
    }
}