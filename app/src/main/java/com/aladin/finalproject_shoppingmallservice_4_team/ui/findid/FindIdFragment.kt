package com.aladin.finalproject_shoppingmallservice_4_team.ui.findid

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentFindIdBinding
import com.aladin.finalproject_shoppingmallservice_4_team.ui.custom.CustomDialog
import com.aladin.finalproject_shoppingmallservice_4_team.ui.findpw.FindPwFragment1
import com.aladin.finalproject_shoppingmallservice_4_team.ui.login.LoginFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceMainFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceSubFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class FindIdFragment : Fragment() {

    lateinit var fragmentFindIdBinding: FragmentFindIdBinding

    private val findIdViewModel: FindIdViewModel by viewModels()

    private var verificationId: String? = null
    private var countdownTimer: CountDownTimer? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        fragmentFindIdBinding = FragmentFindIdBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()

        settingToolbarFindId()

        settingPhoneNumberFormatting()

        settingSendVerifyCodeButton()

        settingVerifyCodeButton()

        observeViewModel()

        settingFindIdButton()

        return fragmentFindIdBinding.root
    }

    // 옵저버
    private fun observeViewModel() {
        findIdViewModel.userIdLiveData.observe(viewLifecycleOwner) { userId ->
            userId?.let {
                val userName = fragmentFindIdBinding.textFieldFindIdUserName.editText?.text.toString()
                showFindIdDialog(userName, it)
            }
        }

        findIdViewModel.errorMessageLiveData.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                showErrorDialog(it)
            }
        }
    }

    private fun showErrorDialog(message: String) {
        val dialog = CustomDialog(
            context = requireContext(),
            contentText = message,
            icon = R.drawable.error_24px,
            positiveText = "확인",
            onPositiveClick = {
                removeFragment()
            }
        )
        dialog.showCustomDialog()
    }

    private fun settingFindIdButton() {
        fragmentFindIdBinding.buttonFindIdFindId.setOnClickListener {
            val userName = fragmentFindIdBinding.textFieldFindIdUserName.editText?.text.toString().trim()
            val phoneNumber = fragmentFindIdBinding.textFieldFindIdPhoneNumber.editText?.text.toString()
                .replace("-", "") // '-' 제거

            if (userName.isBlank()) {
                Toast.makeText(requireContext(), "이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 혹시 한번더 검사
            if (!phoneNumber.startsWith("010") || phoneNumber.length != 11) {
                Toast.makeText(requireContext(), "유효한 전화번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            findIdViewModel.findUserId(userName, phoneNumber) // ViewModel 호출
        }
    }

    // 툴바를 구성하는 메서드
    private fun settingToolbarFindId(){
        fragmentFindIdBinding.apply {
            toolbarFindId.setNavigationIcon(R.drawable.arrow_back_ios_24px)
            toolbarFindId.setNavigationOnClickListener {
                removeFragment()
            }
            toolbarFindId.setTitle("아이디 찾기")
        }
    }


    // 전화번호 입력 자동으로 - 입력되게 하기
    private fun settingPhoneNumberFormatting(){
        fragmentFindIdBinding.textFieldFindIdPhoneNumber.editText?.addTextChangedListener(object :
            TextWatcher {
            private var isFormatting = false
            private var previousText = ""

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isFormatting) return

                val input = s.toString()
                if (input == previousText) return

                isFormatting = true

                val formatted = formattingPhoneNumber(input)
                fragmentFindIdBinding.textFieldFindIdPhoneNumber.editText?.setText(formatted)
                fragmentFindIdBinding.textFieldFindIdPhoneNumber.editText?.setSelection(formatted.length)

                previousText = formatted
                isFormatting = false
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }

    // 자동으로 - 붙이기
    private fun formattingPhoneNumber(input:String) : String{
        val numbersOnly = input.replace("-","")
        return when {
            numbersOnly.length <= 3 -> numbersOnly
            numbersOnly.length <= 7 -> "${numbersOnly.substring(0, 3)}-${numbersOnly.substring(3)}"
            numbersOnly.length <= 11 -> "${numbersOnly.substring(0, 3)}-${numbersOnly.substring(3, 7)}-${numbersOnly.substring(7)}"
            else -> numbersOnly
        }
    }


    // 인증번호 전송 버튼
    private fun settingSendVerifyCodeButton() {
        fragmentFindIdBinding.buttonFindIdSendVerifyCode.setOnClickListener {
            val phoneNumber = fragmentFindIdBinding.textFieldFindIdPhoneNumber.editText?.text.toString()
                .replace("-", "") // '-' 제거

            // 전화번호 유효성 검사
            if (!phoneNumber.startsWith("010") || phoneNumber.length != 11) {
                fragmentFindIdBinding.textFieldFindIdPhoneNumber.error = "유효한 전화번호를 입력해주세요"
                return@setOnClickListener
            } else {
                fragmentFindIdBinding.textFieldFindIdPhoneNumber.error = null
                fragmentFindIdBinding.textFieldFindIdPhoneNumber.helperText = " "
            }

            // 인증번호 전송 버튼 비활성화
            fragmentFindIdBinding.buttonFindIdSendVerifyCode.isEnabled = false
            fragmentFindIdBinding.buttonFindIdSendVerifyCode.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), android.R.color.darker_gray)

            // 3초 후 버튼 활성화
            fragmentFindIdBinding.buttonFindIdSendVerifyCode.postDelayed({
                fragmentFindIdBinding.buttonFindIdSendVerifyCode.isEnabled = true
                fragmentFindIdBinding.buttonFindIdSendVerifyCode.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.main_color)
                fragmentFindIdBinding.buttonFindIdSendVerifyCode.text = "재전송"
            }, 3000)

            // 인증번호 입력 필드와 확인 버튼 표시
            fragmentFindIdBinding.textFieldFindId.visibility = View.VISIBLE
            fragmentFindIdBinding.buttonFindIdConfirm.visibility = View.VISIBLE

            // 타이머 시작
            fragmentFindIdBinding.textViewFindIdTimer.visibility = View.VISIBLE
            startCountdownTimer()

            startPhoneVerification("+82${phoneNumber.substring(1)}") // E.164 형식
        }
    }

    // 인증번호 입력 버튼
    private fun settingVerifyCodeButton() {
        fragmentFindIdBinding.buttonFindIdConfirm.setOnClickListener {
            val code = fragmentFindIdBinding.textFieldFindId.editText?.text.toString()

            if (code.isBlank()) {
                fragmentFindIdBinding.textFieldFindId.error = "인증번호를 입력해주세요"
                return@setOnClickListener
            } else {
                fragmentFindIdBinding.textFieldFindId.error = null
            }

            verifyPhoneNumberWithCode(verificationId, code)
        }
    }

    // Firebase로 인증번호 전송
    private fun startPhoneVerification(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // 인증 자동 완료
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(exception: FirebaseException) {
                    fragmentFindIdBinding.textFieldFindIdPhoneNumber.error = "인증 실패: ${exception.message}"
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    this@FindIdFragment.verificationId = verificationId
                }
            }).build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    // 타이머 시작
    private fun startCountdownTimer() {
        countdownTimer?.cancel()

        countdownTimer = object : CountDownTimer(5 * 60 * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / 1000 / 60
                val seconds = millisUntilFinished / 1000 % 60
                fragmentFindIdBinding.textViewFindIdTimer.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                fragmentFindIdBinding.textViewFindIdTimer.text = "시간 초과"
                fragmentFindIdBinding.textFieldFindId.isEnabled = false
                fragmentFindIdBinding.buttonFindIdConfirm.isEnabled = false
            }
        }.start()
    }

    // 인증번호 확인
    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        if (verificationId == null) {
            fragmentFindIdBinding.textFieldFindId.error = "인증번호 요청을 다시 시도해주세요."
            return
        }

        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        signInWithPhoneAuthCredential(credential)
    }

    // Firebase 인증 완료 처리
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "인증 성공!", Toast.LENGTH_SHORT).show()

                // 인증 성공 시 UI 요소 비활성화
                fragmentFindIdBinding.apply {
                    textFieldFindIdPhoneNumber.editText?.isEnabled = false
                    buttonFindIdSendVerifyCode.isEnabled = false
                    buttonFindIdSendVerifyCode.backgroundTintList =
                        ContextCompat.getColorStateList(requireContext(), android.R.color.darker_gray)
                    textFieldFindId.editText?.isEnabled = false
                    buttonFindIdConfirm.isEnabled = false
                    buttonFindIdConfirm.backgroundTintList =
                        ContextCompat.getColorStateList(requireContext(), android.R.color.darker_gray)

                    buttonFindIdFindId.visibility = View.VISIBLE
                }

                // 타이머 숨기기
                countdownTimer?.cancel()
                fragmentFindIdBinding.textViewFindIdTimer.visibility = View.GONE

            } else {
                fragmentFindIdBinding.textFieldFindId.error = "인증 실패: ${task.exception?.message}"
            }
        }
    }


    // 다이얼로그를 표시하는 메서드
    fun showFindIdDialog(userName: String, userId: String) {
        val dialog = CustomDialog(
            context = requireContext(),
            contentText = "${userName}님의 아이디는 $userId 입니다.",
            icon = R.drawable.check_circle_24px,
            positiveText = "로그인 하기",
            onPositiveClick = {
                // 로그인 화면으로 이동
                removeFragment()
                // replaceMainFragment(LoginFragment(), false)
            },
            negativeText = "비밀번호 찾기",
            onNegativeClick = {
                // 비밀번호 찾기 화면으로 이동
                removeFragment()
                replaceMainFragment(FindPwFragment1(), true)
            }
        )
        dialog.showCustomDialog()
    }
}