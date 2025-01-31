package com.aladin.finalproject_shoppingmallservice_4_team.ui.ask

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.aladin.finalproject_shoppingmallservice_4_team.BookApplication
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentAskBinding
import com.aladin.finalproject_shoppingmallservice_4_team.model.AskModel
import com.aladin.finalproject_shoppingmallservice_4_team.ui.custom.CustomDialog
import com.aladin.finalproject_shoppingmallservice_4_team.ui.home.HomeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.login.LoginFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.main.MainFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceMainFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceSubFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AskFragment : Fragment() {

    lateinit var fragmentAskBinding: FragmentAskBinding
    private val askViewModel : AskViewModel by viewModels()
    private var attachmentUri: Uri? = null

    companion object {
        private const val REQUEST_CODE_ATTACHMENT = 1001
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        fragmentAskBinding = FragmentAskBinding.inflate(inflater, container, false)

        // 로그인 상태 체크
        if (!isUserLoggedIn()) {
            showNotLoggedInDialog()
            return null // 화면 렌더링 중단
        }

        observeViewModel()

        settingToolbarAsk()

        settingAttachmentButton()

        buttonAskSend()

        return fragmentAskBinding.root
    }

    // 로그인 여부 체크
    private fun isUserLoggedIn(): Boolean {
        return try {
            val bookApplication = requireActivity().application as BookApplication
            bookApplication.loginUserModel.userId.isNotBlank()
        } catch (e: UninitializedPropertyAccessException) {
            false
        }
    }

    private fun showNotLoggedInDialog() {
        val dialog = CustomDialog(
            context = requireContext(),
            contentText = "로그인 후 이용 가능합니다.",
            icon = R.drawable.error_24px,
            positiveText = "확인",
            onPositiveClick = {
                removeFragment()
                replaceMainFragment(LoginFragment(), true)
            }
        )
        dialog.showCustomDialog()
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

    private fun settingAttachmentButton(){
        fragmentAskBinding.buttonAskAttach.setOnClickListener{
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "*/*"
            }
            startActivityForResult(intent, REQUEST_CODE_ATTACHMENT)
        }
    }

    private fun buttonAskSend() {
        fragmentAskBinding.buttonAskSend.setOnClickListener {
            val bookApplication = requireActivity().application as BookApplication
            val user = bookApplication.loginUserModel

            val title = fragmentAskBinding.textFieldAskTitle.editText?.text.toString()
            val content = fragmentAskBinding.textFieldAskSummary.editText?.text.toString()
            val email = fragmentAskBinding.textFieldAskEmail.editText?.text.toString()

            if (title.isBlank() || content.isBlank() || email.isBlank()) {
                Toast.makeText(requireContext(), "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val askModel = AskModel().apply {
                askTitle = title
                askContent = content
                askUserEmail = email
                askUserToken = (requireActivity().application as BookApplication).loginUserModel.userToken
                askAttach = askViewModel.attachmentUrl.value.orEmpty()
                askState = 0
                askTime = System.currentTimeMillis().toString()
                askUserName = user.userName
            }

            // Custom Dialog 사용
            val dialog = CustomDialog(
                context = requireContext(),
                contentText = """
                    문의 완료
                    작성하신 이메일로 답변 드리겠습니다. 
                """.trimIndent(),
                icon = R.drawable.check_circle_24px,
                positiveText = "확인",
                onPositiveClick = {
                    askViewModel.uploadAsk(askModel)
                    removeFragment()
                }
            )
            dialog.showCustomDialog()
        }
    }

    private fun observeViewModel() {
        askViewModel.attachmentUrl.observe(viewLifecycleOwner) { url ->
            if (url != null) {
                val fileName = attachmentUri?.lastPathSegment.orEmpty()
                fragmentAskBinding.textFieldAskAttach.editText?.setText(fileName)
            } else {
                fragmentAskBinding.textFieldAskAttach.editText?.setText("첨부파일 업로드 실패")
            }
        }

        askViewModel.uploadResult.observe(viewLifecycleOwner) { success ->
            if (!success) {
                Toast.makeText(requireContext(), "문의 접수 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ATTACHMENT && resultCode == Activity.RESULT_OK) {
            attachmentUri = data?.data
            attachmentUri?.let { askViewModel.uploadAttachment(it) }
        }
    }
}