package com.aladin.finalproject_shoppingmallservice_4_team.ui.findpw

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentFindPw2Binding
import com.aladin.finalproject_shoppingmallservice_4_team.ui.custom.CustomDialog
import com.aladin.finalproject_shoppingmallservice_4_team.ui.login.LoginFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.clearAllBackStack
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceSubFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FindPwFragment2 : Fragment() {

    lateinit var fragmentFindPw2Binding: FragmentFindPw2Binding

    private val viewModel : FindPwViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val userId = arguments?.getString("userId") ?: ""
        val phoneNumber = arguments?.getString("phoneNumber") // 혹시 나중에 필요할까봐


        // Inflate the layout for this fragment
        fragmentFindPw2Binding = FragmentFindPw2Binding.inflate(inflater, container, false)

        settingToolbarFindPw2()

        settingConfirmButton(userId)

        observeViewModel()

        return fragmentFindPw2Binding.root
    }

    // 툴바 설정
    private fun settingToolbarFindPw2() {
        fragmentFindPw2Binding.toolbarFindPw2.apply {
            title = "비밀번호 재설정"
            setNavigationIcon(R.drawable.arrow_back_ios_24px)
            setNavigationOnClickListener {
                CustomDialog(
                    context = requireContext(),
                    contentText = "정말 뒤로 가시겠습니까?",
                    icon = R.drawable.cancel_24px,
                    positiveText = "확인",
                    onPositiveClick = { removeFragment() },
                    negativeText = "취소"
                ).showCustomDialog()
            }
        }
    }

    // 확인 버튼 메서드
    fun settingConfirmButton(userId: String) {
        fragmentFindPw2Binding.apply {
            buttonFindPw2Confirm.setOnClickListener {
                val newPassword1 = textFieldFindPw2NewPw1.editText?.text.toString()
                val newPassword2 = textFieldFindPw2NewPw2.editText?.text.toString()

                // 비밀번호 유효성 검사
                if (newPassword1.isBlank()) {
                    textFieldFindPw2NewPw1.error = "비밀번호를 입력해주세요"
                    textFieldFindPw2NewPw2.helperText = " "
                    return@setOnClickListener
                }

                if (newPassword1 != newPassword2) {
                    textFieldFindPw2NewPw2.error = "비밀번호가 일치하지 않습니다"
                    textFieldFindPw2NewPw1.helperText = " "
                    return@setOnClickListener
                }

                if (newPassword1.length < 6) {
                    fragmentFindPw2Binding.textFieldFindPw2NewPw1.error = "비밀번호는 최소 6자리 이상이어야 합니다."
                    fragmentFindPw2Binding.textFieldFindPw2NewPw2.helperText = " "
                    return@setOnClickListener
                }

                // 비밀번호 변경
                viewModel.updatePassword(userId, newPassword1)
            }
        }
    }

    private fun observeViewModel(){
        viewModel.passwordUpdateResult.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                CustomDialog(
                    context = requireContext(),
                    contentText = "비밀번호 재설정이 완료되었습니다.",
                    icon = R.drawable.check_circle_24px,
                    positiveText = "확인",
                    onPositiveClick = {
                        replaceSubFragment(LoginFragment(), false)
                    }
                ).showCustomDialog()
            } else {
                CustomDialog(
                    context = requireContext(),
                    contentText = "비밀번호 재설정에 실패했습니다. 다시 시도해주세요.",
                    icon = R.drawable.cancel_24px,
                    positiveText = "확인",
                    onPositiveClick = {}
                ).showCustomDialog()
            }
        }
    }


}