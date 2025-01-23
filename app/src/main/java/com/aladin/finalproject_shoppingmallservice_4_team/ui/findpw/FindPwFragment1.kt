package com.aladin.finalproject_shoppingmallservice_4_team.ui.findpw

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
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentFindPw1Binding
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceSubFragment
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class FindPwFragment1 : Fragment() {

    private val findPwViewModel: FindPwViewModel by viewModels()

    lateinit var fragmentFindPw1Binding: FragmentFindPw1Binding
    private lateinit var auth : FirebaseAuth
    private var verificationId: String? = null
    private var countdownTimer: CountDownTimer? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        fragmentFindPw1Binding = FragmentFindPw1Binding.inflate(inflater,container,false)
        auth = FirebaseAuth.getInstance()

        settingToolbarFindPw1()

        settingPhoneNumberFormatting()

        settingSendVerifyCodeButton()
        settingVerifyCodeButton()

        observeViewModel()

        settingChangePwButton()


        return fragmentFindPw1Binding.root
    }

    private fun observeViewModel() {
        findPwViewModel.userIdCheckResult.observe(viewLifecycleOwner) {result ->
            if (result == true) {
                // ID와 전화번호가 일치 -> 화면 이동

                // ID와 전화번호를 들고 가기
                val userId = fragmentFindPw1Binding.textFieldFindPw1UserId.editText?.text.toString().trim()
                val phoneNumber = fragmentFindPw1Binding.textFieldFindPw1PhoneNumber.editText?.text.toString().replace("-","").trim()

                val bundle = Bundle().apply {
                    putString("userId", userId)
                    putString("phoneNumber", phoneNumber)
                }
                val fragment = FindPwFragment2().apply {
                    arguments = bundle
                }

                replaceSubFragment(fragment, true)
            } else if (result == false) {
                // ID 전화번호 불일치 -> 에러 표시
                fragmentFindPw1Binding.textFieldFindPw1UserId.error = "ID와 전화번호가 일치하지 않습니다"
            } else {
                // 오류 발생
                Toast.makeText(requireContext(), "오류가 발생했습니다", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 툴바를 설정하는 메서드
    fun settingToolbarFindPw1(){
        fragmentFindPw1Binding.apply {
            toolbarFindPw1.title = "비밀번호 찾기"
            toolbarFindPw1.setNavigationIcon(R.drawable.arrow_back_ios_24px)
            toolbarFindPw1.setNavigationOnClickListener {
                removeFragment()
            }
        }
    }

    // 전화번호 입력 자동으로 - 입력되게 하기
    private fun settingPhoneNumberFormatting(){
        fragmentFindPw1Binding.textFieldFindPw1PhoneNumber.editText?.addTextChangedListener(object :
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
                fragmentFindPw1Binding.textFieldFindPw1PhoneNumber.editText?.setText(formatted)
                fragmentFindPw1Binding.textFieldFindPw1PhoneNumber.editText?.setSelection(formatted.length)

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
        fragmentFindPw1Binding.buttonFindPw1SendVerifyCode.setOnClickListener {
            val phoneNumber = fragmentFindPw1Binding.textFieldFindPw1PhoneNumber.editText?.text.toString()
                .replace("-", "") // '-' 제거

            // 전화번호 유효성 검사
            if (!phoneNumber.startsWith("010") || phoneNumber.length != 11) {
                fragmentFindPw1Binding.textFieldFindPw1PhoneNumber.error = "유효한 전화번호를 입력해주세요"
                return@setOnClickListener
            } else {
                fragmentFindPw1Binding.textFieldFindPw1PhoneNumber.error = null
                fragmentFindPw1Binding.textFieldFindPw1PhoneNumber.helperText = " "
            }

            // 인증번호 전송 버튼 비활성화
            fragmentFindPw1Binding.buttonFindPw1SendVerifyCode.isEnabled = false
            fragmentFindPw1Binding.buttonFindPw1SendVerifyCode.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), android.R.color.darker_gray)

            // 3초 후 버튼 활성화
            fragmentFindPw1Binding.buttonFindPw1SendVerifyCode.postDelayed({
                fragmentFindPw1Binding.buttonFindPw1SendVerifyCode.isEnabled = true
                fragmentFindPw1Binding.buttonFindPw1SendVerifyCode.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.main_color)
                fragmentFindPw1Binding.buttonFindPw1SendVerifyCode.text = "재전송"
            }, 3000)

            // 인증번호 입력 필드와 확인 버튼 표시
            fragmentFindPw1Binding.textFieldFindPw1VerifyCode.visibility = View.VISIBLE
            fragmentFindPw1Binding.buttonFindPw1Confirm.visibility = View.VISIBLE

            // 타이머 시작
            fragmentFindPw1Binding.textViewFindPw1Timer.visibility = View.VISIBLE
            startCountdownTimer()

            startPhoneVerification("+82${phoneNumber.substring(1)}") // E.164 형식
        }
    }

    // 인증번호 확인 버튼 설정
    private fun settingVerifyCodeButton() {
        fragmentFindPw1Binding.buttonFindPw1Confirm.setOnClickListener {
            val code = fragmentFindPw1Binding.textFieldFindPw1VerifyCode.editText?.text.toString()

            if (code.isBlank()) {
                fragmentFindPw1Binding.textFieldFindPw1VerifyCode.error = "인증번호를 입력해주세요."
                return@setOnClickListener
            } else {
                fragmentFindPw1Binding.textFieldFindPw1VerifyCode.error = null
            }

            verifyPhoneNumberWithCode(verificationId, code)
        }
    }

    private fun startPhoneVerification(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(exception: FirebaseException) {
                    Toast.makeText(requireContext(), "인증 실패: ${exception.message}", Toast.LENGTH_LONG).show()
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    this@FindPwFragment1.verificationId = verificationId
                    Toast.makeText(requireContext(), "인증번호가 전송되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }).build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        if (verificationId == null) {
            Toast.makeText(requireContext(), "인증번호 요청을 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "인증 성공!", Toast.LENGTH_SHORT).show()

                // 인증 성공 시 비밀번호 재설정 버튼 활성화
                fragmentFindPw1Binding.buttonFindPw1ChangPw.visibility = View.VISIBLE

                // 인증 성공 시 UI 요소 비활성화
                fragmentFindPw1Binding.apply {
                    textFieldFindPw1PhoneNumber.editText?.isEnabled = false
                    buttonFindPw1SendVerifyCode.isEnabled = false
                    buttonFindPw1SendVerifyCode.backgroundTintList =
                        ContextCompat.getColorStateList(requireContext(), android.R.color.darker_gray)
                    textFieldFindPw1VerifyCode.editText?.isEnabled = false
                    buttonFindPw1Confirm.isEnabled = false
                    buttonFindPw1Confirm.backgroundTintList =
                        ContextCompat.getColorStateList(requireContext(), android.R.color.darker_gray)
                    textViewFindPw1Timer.visibility = View.GONE
                }

                // 버튼 클릭 시 화면 이동
//                fragmentFindPw1Binding.buttonFindPw1ChangPw.setOnClickListener {
//                    replaceSubFragment(FindPwFragment2(), true)
//                }

            } else {
                fragmentFindPw1Binding.textFieldFindPw1VerifyCode.error = "인증번호가 일치하지 않습니다"
            }
        }
    }

    // 타이머 시작
    private fun startCountdownTimer() {
        countdownTimer?.cancel()

        countdownTimer = object : CountDownTimer(5 * 60 * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / 1000 / 60
                val seconds = millisUntilFinished / 1000 % 60
                fragmentFindPw1Binding.textViewFindPw1Timer.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                fragmentFindPw1Binding.textViewFindPw1Timer.text = "시간 초과"
                fragmentFindPw1Binding.textFieldFindPw1VerifyCode.isEnabled = false
                fragmentFindPw1Binding.buttonFindPw1Confirm.isEnabled = false
            }
        }.start()
    }

    // 확인 버튼 메서드 - 나중에 유효성 처리하고 데이터를 가지고 비밀번호 찾기 화면2로 가기
    fun settingChangePwButton(){
        fragmentFindPw1Binding.buttonFindPw1ChangPw.setOnClickListener{
            val userId = fragmentFindPw1Binding.textFieldFindPw1UserId.editText?.text.toString()
            val phoneNumber = fragmentFindPw1Binding.textFieldFindPw1PhoneNumber.editText?.text.toString()
                .replace("-", "") // '-' 제거

            if (userId.isBlank()) {
                fragmentFindPw1Binding.textFieldFindPw1UserId.error = "아이디를 입력해주세요"
                return@setOnClickListener
            }

            // 한번더 유효성 검사
            if (!phoneNumber.startsWith("010") || phoneNumber.length != 11) {
                fragmentFindPw1Binding.textFieldFindPw1PhoneNumber.error = "올바른 전화번호를 입력해주세요."
                return@setOnClickListener
            }

            // ViewModel 호출
            findPwViewModel.checkUserIdAndPhone(userId, phoneNumber)
        }
    }
}