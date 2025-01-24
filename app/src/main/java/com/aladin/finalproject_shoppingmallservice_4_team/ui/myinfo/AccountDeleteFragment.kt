package com.aladin.finalproject_shoppingmallservice_4_team.ui.myinfo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.aladin.finalproject_shoppingmallservice_4_team.BookApplication
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentAccountDeleteBinding
import com.aladin.finalproject_shoppingmallservice_4_team.ui.custom.CustomDialog
import com.aladin.finalproject_shoppingmallservice_4_team.ui.home.HomeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.login.SharedPreferencesHelper
import com.aladin.finalproject_shoppingmallservice_4_team.util.clearAllBackStack
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceSubFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountDeleteFragment : Fragment() {

    lateinit var fragmentAccountDeleteBinding: FragmentAccountDeleteBinding

    private val accountDeleteViewModel: AccountDeleteViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        fragmentAccountDeleteBinding = FragmentAccountDeleteBinding.inflate(inflater, container, false)

        settingAccountDeleteToolbar()

        settingTextViewAccountDeleteTermsOfUse()

        settingButtonAccountDelete()

        settingFooterButton()

        observeViewModel()

        return fragmentAccountDeleteBinding.root
    }

    // 툴바를 구성하는 메서드
    fun settingAccountDeleteToolbar() {
        fragmentAccountDeleteBinding.apply {
            toolbarAccountDelete.setTitle("탈퇴하기")
            toolbarAccountDelete.setNavigationIcon(R.drawable.arrow_back_ios_24px)
            toolbarAccountDelete.setNavigationOnClickListener {
                removeFragment()
            }
        }
    }

    // 탈퇴약관 설정하는 메서드
    fun settingTextViewAccountDeleteTermsOfUse(){
        fragmentAccountDeleteBinding.apply {
            val termsOfUse = """
                책방마켓의 회원을 탈퇴하려면 몇가지 규칙이 있습니다
                
                규칙 1.
                
                규칙 2.
                
                규칙 3.
                 
                규칙 4.
                
                규칙 5.
                
                규칙 6.
                
                규칙 7.
                
                규칙 8.
                
                규칙 9.
                
                규칙 10.
                
            """.trimIndent()
            textViewAccountDeleteTermsOfUse.text = termsOfUse
        }
    }

    private fun settingButtonAccountDelete() {
        fragmentAccountDeleteBinding.apply {
            buttonAccountDeleteDeleteAccount.setOnClickListener {
                val password = textFieldAccountDeletePassword.editText?.text.toString().trim()

                if (password.isEmpty()) {
                    textFieldAccountDeletePassword.error = "비밀번호를 입력해주세요"
                    return@setOnClickListener
                }

                val bookApplication = requireActivity().application as BookApplication
                val userId = bookApplication.loginUserModel.userId

                // ViewModel에 데이터 전달
                accountDeleteViewModel.validatePassword(userId, password)
            }
        }
    }

    // Footer 버튼들 메서드
    fun settingFooterButton() {
        fragmentAccountDeleteBinding.apply {
            // 1:1 문의 버튼
            buttonAccountDeleteAsk.setOnClickListener {

            }

            // FAQ 버튼
            buttonAccountDeleteFAQ.setOnClickListener {

            }
        }
    }

    private fun observeViewModel() {
        accountDeleteViewModel.passwordValidationResult.observe(viewLifecycleOwner) { isValid ->
            if (isValid) {
                val bookApplication = requireActivity().application as BookApplication
                val userId = bookApplication.loginUserModel.userId

                accountDeleteViewModel.deactivateAccount(userId)
            } else {
                showDialog("비밀번호가 일치하지 않습니다. 다시 입력해주세요.", isSuccess = false)
            }
        }

        accountDeleteViewModel.accountDeactivationResult.observe(viewLifecycleOwner) { isDeactivated ->
            if (isDeactivated) {
                showDialog("회원 탈퇴가 완료되었습니다.") {
                    handleLogout()
                }
            } else {
                showDialog("회원 탈퇴 처리에 실패했습니다. 다시 시도해주세요.", isSuccess = false)
            }
        }
    }

    private fun handleLogout() {
        val sharedPreferencesHelper = SharedPreferencesHelper(requireContext())
        sharedPreferencesHelper.clearAutoLoginToken()

        val bookApplication = requireActivity().application as BookApplication

        bookApplication.apply {
            this::class.java.getDeclaredField("loginUserModel").apply {
                isAccessible = true
                set(bookApplication, null)
            }
        }

        clearAllBackStack()
        replaceSubFragment(HomeFragment(), false)
        // Toast.makeText(requireContext(), "로그아웃되었습니다.", Toast.LENGTH_SHORT).show()
    }

    private fun showDialog(message: String, isSuccess: Boolean = true, onPositiveClick: (() -> Unit)? = null) {
        val iconRes = if (isSuccess) R.drawable.check_circle_24px else R.drawable.error_24px
        val dialog = CustomDialog(
            context = requireContext(),
            contentText = message,
            icon = iconRes,
            positiveText = "확인",
            onPositiveClick = onPositiveClick ?: {}, // 기본값 처리
            negativeText = null
        )
        dialog.showCustomDialog()
    }
}