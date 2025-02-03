package com.aladin.finalproject_shoppingmallservice_4_team.ui.changePw

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import com.aladin.finalproject_shoppingmallservice_4_team.BookApplication
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentChangePwBinding
import com.aladin.finalproject_shoppingmallservice_4_team.ui.custom.CustomDialog
import com.aladin.finalproject_shoppingmallservice_4_team.ui.login.LoginFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceMainFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.showSoftInput
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ChangePwFragment : Fragment() {
    private lateinit var fragmentChangePwBinding: FragmentChangePwBinding
    private val changePwViewModel: ChangePwViewModel by viewModels()
    private lateinit var bookApplication: BookApplication

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentChangePwBinding = FragmentChangePwBinding.inflate(layoutInflater, container, false)

        // bookApplication 초기화
        bookApplication = requireActivity().application as BookApplication

        // 로그인 검사
        checkLoginProcess()

        // 화면들어왔을 때 Focus 설정
        fragmentChangePwBinding.textFieldChangePwCurrentPassword.editText?.showSoftInput() // 키보드 띄우기

        // 툴바 세팅
        settingToolbar()

        // 비밀번호 변경 버튼
        onClickChangePwButton()

        // 값 변경 시 viewModel에 값을 담아준다.
        setTextFieldChangeListener()

        // 옵저버
        settingObserver()

        return fragmentChangePwBinding.root
    }

    // Check Login
    private fun checkLoginProcess() {
        try {
            if (::bookApplication.isInitialized && bookApplication.loginUserModel != null) {

            }
        } catch (e: Exception) {
            val loginDialog = CustomDialog(
                requireContext(),
                onPositiveClick = {
                    removeFragment()
                    replaceMainFragment(LoginFragment(), true)
                },
                contentText = "로그인을 먼저 진행해주세요.",
                icon = R.drawable.error_24px
            )
            loginDialog.showCustomDialog()
        }
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
                val currentPassword = bookApplication.loginUserModel.userPw.trim()
                if (textFieldChangePwCurrentPassword.editText?.text.toString() != currentPassword) {
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
                changePwViewModel.changeUserPassword(bookApplication.loginUserModel.userToken)
                    //removeFragment()

            }
        }
    }

    // Observer
    private fun settingObserver() {
        changePwViewModel.isSuccessChangePw.observe(viewLifecycleOwner) {
            if (it) {
                val twoButtonDialog = CustomDialog(
                    requireContext(),
                    onPositiveClick = {
                        removeFragment()
                    },
                    positiveText = "확인",
                    contentText = "비밀번호가 정상적으로 변경되었습니다.",
                    icon = R.drawable.error_24px
                )
                twoButtonDialog.showCustomDialog()
            }
        }
    }
}