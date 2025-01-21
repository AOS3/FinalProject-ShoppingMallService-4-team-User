package com.aladin.finalproject_shoppingmallservice_4_team.ui.register

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentRegisterStep1Binding
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentRegisterStep2Binding
import com.aladin.finalproject_shoppingmallservice_4_team.ui.login.LoginFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceMainFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class RegisterStep2Fragment : Fragment() {

    lateinit var fragmentRegisterStep2Binding: FragmentRegisterStep2Binding
    private lateinit var registerViewModel: RegisterViewModel
    private var verificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private lateinit var auth: FirebaseAuth



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // ViewModel
        registerViewModel = ViewModelProvider(this)[RegisterViewModel::class.java]

        auth = FirebaseAuth.getInstance()

        // Inflate the layout for this fragment
        fragmentRegisterStep2Binding = FragmentRegisterStep2Binding.inflate(layoutInflater)

        settingRegister2Toolbar()

        registerButtonListener()

        settingPhoneVerification()

        // 카카오 주소 api
        settingKakaoPostCode()

        settingObservers()

        settingIdCheckListener()


        return fragmentRegisterStep2Binding.root
    }

    private fun settingObservers(){
        registerViewModel.isIdAvailable.observe(viewLifecycleOwner) { isAvailable ->
            when (isAvailable) {
                true -> {
                    fragmentRegisterStep2Binding.textFieldRegisterStep2Id.helperText = "사용 가능한 아이디입니다."
                    fragmentRegisterStep2Binding.textFieldRegisterStep2Id.error = null
                }
                false -> {
                    fragmentRegisterStep2Binding.textFieldRegisterStep2Id.error = "이미 존재하는 아이디입니다."
                }
                null -> {
                    // 초기 상태 또는 확인 중
                    fragmentRegisterStep2Binding.textFieldRegisterStep2Id.helperText = " "
                    fragmentRegisterStep2Binding.textFieldRegisterStep2Id.error = null
                }
            }
        }

        registerViewModel.idCheckErrorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage.isNotBlank()) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun settingIdCheckListener(){
        fragmentRegisterStep2Binding.buttonRegisterStep2CheckId.setOnClickListener{
            val userId = fragmentRegisterStep2Binding.textFieldRegisterStep2Id.editText?.text.toString()

            // 아이디를 입력하지 않은 경우 처리
            if (userId.isBlank()) {
                fragmentRegisterStep2Binding.textFieldRegisterStep2Id.error = "아이디를 입력해주세요"
                return@setOnClickListener
            }


            // ViewModel을 통해 ID 중복 확인
            registerViewModel.checkUserIdAvailability(userId)

            // 중복 확인 결과를 옵저버로 처리
            registerViewModel.isIdAvailable.observe(viewLifecycleOwner) { isAvailable ->
                when (isAvailable) {
                    true -> showIdConfirmationDialog(userId)
                    false -> {
                        fragmentRegisterStep2Binding.textFieldRegisterStep2Id.error = "이미 존재하는 아이디입니다."
                        Toast.makeText(requireContext(), "이미 존재하는 아이디입니다.", Toast.LENGTH_SHORT).show()
                    }
                    null -> {
                        // 초기 상태 또는 확인 중
                        fragmentRegisterStep2Binding.textFieldRegisterStep2Id.error = null
                    }
                }
            }
        }
    }

    // 사용 가능한 ID일 때 다이얼로그 표시
    private fun showIdConfirmationDialog(userId: String) {
        val dialogBuilder = MaterialAlertDialogBuilder(requireContext())
        dialogBuilder.setTitle("중복확인")
        dialogBuilder.setMessage("사용 가능한 닉네임입니다. 사용하시겠습니까?")
        dialogBuilder.setPositiveButton("확인") { dialog, _ ->
            // 아이디 입력 필드를 비활성화
            fragmentRegisterStep2Binding.textFieldRegisterStep2Id.editText?.isEnabled = false
            // 다이얼로그 닫기
            dialog.dismiss()
        }
        dialogBuilder.setNegativeButton("취소") { dialog, _ ->
            // 다이얼로그 닫기
            dialog.dismiss()
        }
        dialogBuilder.create().show()
    }



    // 툴바를 설정하는 메서드
    fun settingRegister2Toolbar(){
        fragmentRegisterStep2Binding.apply {
            toolbarRegisterStep2.setNavigationIcon(R.drawable.arrow_back_ios_24px)

            // 뒤로 가는 메서드 분기
            toolbarRegisterStep2.setNavigationOnClickListener {
                // 만약 도로명주소 검색화면 이라면
                if (webViewRegisterStep2Address.visibility == View.VISIBLE){
                    webViewRegisterStep2Address.visibility = View.GONE
                } else {
                    removeFragment()
                }
            }

        }
    }

    private fun formatPhoneNumber(phoneNumber: String): String {
        val formattedNumber = if (phoneNumber.startsWith("0")) {
            "+82" + phoneNumber.substring(1)
        } else {
            phoneNumber
        }
        Log.d("PhoneVerification", "Formatted phone number: $formattedNumber")
        return formattedNumber
    }

    private fun settingPhoneVerification() {
        fragmentRegisterStep2Binding.apply {
            buttonRegisterStep2VerifyPhoneNumber.setOnClickListener {
                val phoneNumber = textFieldRegisterStep2PhoneNumber.editText?.text.toString()
                if (phoneNumber.isBlank()) {
                    textFieldRegisterStep2PhoneNumber.error = "전화번호를 입력해주세요"
                    return@setOnClickListener
                } else {
                    textFieldRegisterStep2PhoneNumber.helperText = " "
                }

                val formattedPhoneNumber = formatPhoneNumber(phoneNumber)
                startPhoneVerification(formattedPhoneNumber)
            }

            buttonRegisterStep2VerifyCode.setOnClickListener {
                val code = textFieldRegisterStep2VerifyNumber.editText?.text.toString()
                if (code.isBlank()) {
                    textFieldRegisterStep2VerifyNumber.error = "인증번호를 입력해주세요"
                    return@setOnClickListener
                } else {
                    textFieldRegisterStep2VerifyNumber.helperText = " "
                }

                verifyPhoneNumberWithCode(verificationId!!, code)
            }
        }
    }

    private fun startPhoneVerification(phoneNumber:String) {

        verificationId = null

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // 자동 인증완료
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(exception: FirebaseException) {
                    fragmentRegisterStep2Binding.textFieldRegisterStep2PhoneNumber.error = "유효한 번호를 입력해주세요"
                    Toast.makeText(requireContext(), "인증 실패: ${exception.message}", Toast.LENGTH_LONG).show()
                    Log.e("PhoneVerification", "Verification failed: ${exception.message}", exception)
                    Log.d("test100", "Verification failed")

                    fragmentRegisterStep2Binding.buttonRegisterStep2VerifyPhoneNumber.setText("인증번호 받기")
                }

                override fun onCodeSent(verificationId:String, token: PhoneAuthProvider.ForceResendingToken) {
                    this@RegisterStep2Fragment.verificationId = verificationId
                    this@RegisterStep2Fragment.resendToken = token
                    fragmentRegisterStep2Binding.textFieldRegisterStep2VerifyNumber.visibility = View.VISIBLE
                    fragmentRegisterStep2Binding.textFieldRegisterStep2PhoneNumber.helperText = " "
                    fragmentRegisterStep2Binding.textFieldRegisterStep2VerifyNumber.helperText = " "

                    fragmentRegisterStep2Binding.buttonRegisterStep2VerifyPhoneNumber.setText("재발급 받기")

                    Toast.makeText(requireContext(), "인증번호 전송 성공!", Toast.LENGTH_SHORT).show()
                    fragmentRegisterStep2Binding.buttonRegisterStep2VerifyCode.visibility = View.VISIBLE
                }
            }).build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String){

        if (verificationId == null) {
            Toast.makeText(requireContext(), "인증 요청을 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential){
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "인증 성공!", Toast.LENGTH_SHORT).show()
                fragmentRegisterStep2Binding.buttonRegisterStep2Register.visibility = View.VISIBLE
                fragmentRegisterStep2Binding.textFieldRegisterStep2VerifyNumber.helperText = " "
                // 인증했으니까 휴대폰 입력, 인증번호입력, 인증버튼2개, 비활성화
                fragmentRegisterStep2Binding.textFieldRegisterStep2PhoneNumber.isEnabled = false
                fragmentRegisterStep2Binding.textFieldRegisterStep2VerifyNumber.isEnabled = false
                fragmentRegisterStep2Binding.buttonRegisterStep2VerifyPhoneNumber.isEnabled = false
                fragmentRegisterStep2Binding.buttonRegisterStep2VerifyCode.isEnabled = false

                fragmentRegisterStep2Binding.buttonRegisterStep2VerifyPhoneNumber.setBackgroundColor(Color.GRAY)
                fragmentRegisterStep2Binding.buttonRegisterStep2VerifyCode.setBackgroundColor(Color.GRAY)


            } else {
                Toast.makeText(requireContext(), "인증 실패: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                fragmentRegisterStep2Binding.textFieldRegisterStep2VerifyNumber.error = "인증번호가 일치하지 않습니다"
            }
        }
    }


    fun settingKakaoPostCode(){
        fragmentRegisterStep2Binding.apply {

            // 도로명 주소 찾기 버튼 눌렀을 때
            buttonRegisterStep2SearchPostCode.setOnClickListener {

                // webView 투명도 조절
                webViewRegisterStep2Address.visibility = View.VISIBLE

                webViewRegisterStep2Address.settings.javaScriptEnabled = true
                webViewRegisterStep2Address.settings.javaScriptCanOpenWindowsAutomatically = true
                webViewRegisterStep2Address.settings.allowUniversalAccessFromFileURLs = true

                webViewRegisterStep2Address.addJavascriptInterface(object {
                    @android.webkit.JavascriptInterface
                    fun setAddress(zoneCode: String, fullRoadAddr: String) {
                        // UI 변경은 UI 스레드에서 실행
                        requireActivity().runOnUiThread {
                            Toast.makeText(requireContext(), "우편번호: $zoneCode\n지번 주소: $fullRoadAddr", Toast.LENGTH_LONG).show()

                            // WebView 숨김 처리
                            webViewRegisterStep2Address.visibility = View.GONE

                            // 선택한 주소를 입력 필드에 반영
                            textFieldRegisterStep2PostCode.editText?.setText(zoneCode)
                            textFieldRegisterStep2Address1.editText?.setText(fullRoadAddr)
                        }
                    }
                }, "Android")

                webViewRegisterStep2Address.webViewClient = object : WebViewClient(){
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        // 페이지가 완전히 로드 되면, JavaScript를 메서드 호출
                        view?.loadUrl("javascript:sample2_execDaumPostcode();")
                    }
                }
                // assets 폴더에 저장된 daum.html 파일을 로드
                webViewRegisterStep2Address.loadUrl("file:///android_asset/daum.html")
            }
        }
    }

    // 다음 단계로 이동하는 메서드
    fun registerButtonListener(){
        fragmentRegisterStep2Binding.apply {
            buttonRegisterStep2Register.setOnClickListener {
                // 유효성 검사
                if (validateInputs()) {
                    // 유효성 검사를 통과하면 다이얼로그 표시
                    showConfirmationDialog()
                }
            }
        }
    }

     private fun validateInputs(): Boolean {
         //
         var isValid = true
         fragmentRegisterStep2Binding.apply {
             val userId = textFieldRegisterStep2Id.editText?.text.toString()
             val password = textFieldRegisterStep2Password.editText?.text.toString()
             val passwordCheck = textFieldRegisterStep2PasswordCheck.editText?.text.toString()
             val postCode = textFieldRegisterStep2PostCode.editText?.text.toString()
             val addressDetail = textFieldRegisterStep2Address2.editText?.text.toString()
             val userName = textFieldRegisterStep2UserName.editText?.text.toString()

             // 아이디 검사
             if (userId.isBlank()) {
                 textFieldRegisterStep2Id.error = "아이디를 입력해주세요"
                 isValid = false
             } else if (!registerViewModel.isIdAvailable.value!!) { // 중복 확인 실패
                 textFieldRegisterStep2Id.error = "중복 확인을 통과하지 못했습니다"
                 isValid = false
             } else {
                 textFieldRegisterStep2Id.error = null
                 textFieldRegisterStep2Id.helperText = " "
             }

             // 비밀번호 검사
             if (password.isBlank()) {
                 textFieldRegisterStep2Password.error = "비밀번호를 입력해주세요"
                 isValid = false
             } else if (password != passwordCheck) {
                 textFieldRegisterStep2Password.error = "비밀번호가 일치하지 않습니다"
                 textFieldRegisterStep2PasswordCheck.error = "비밀번호가 일치하지 않습니다"
                 isValid = false
             } else {
                 textFieldRegisterStep2Password.error = null
                 textFieldRegisterStep2PasswordCheck.error = null
                 textFieldRegisterStep2Password.helperText = " "
                 textFieldRegisterStep2PasswordCheck.helperText = " "
             }

             // 이름 검사
             if (userName.isBlank()){
                 textFieldRegisterStep2UserName.error = "이름을 입력해주세요"
                 isValid = false
             } else {
                 textFieldRegisterStep2UserName.error = null
                 textFieldRegisterStep2UserName.helperText = " "
             }


             // 우편번호 검사
             if (postCode.isBlank()) {
                 textFieldRegisterStep2PostCode.error = "주소를 입력해주세요"
                 textFieldRegisterStep2Address1.error = "주소를 입력해주세요"
                 isValid = false
             } else {
                 textFieldRegisterStep2Address1.error = null
                 textFieldRegisterStep2PostCode.error = null
                 textFieldRegisterStep2PostCode.helperText = " "
                 textFieldRegisterStep2Address1.helperText = " "
             }

             // 주소 검사
             if (addressDetail.isBlank()) {
                 textFieldRegisterStep2Address2.error = "주소를 입력해주세요"
                 isValid = false
             } else {
                 textFieldRegisterStep2Address2.error = null
                 textFieldRegisterStep2Address2.helperText = " "
             }
         }


         return isValid
    }

    // 다이얼로그 표시 메서드
    private fun showConfirmationDialog() {
        val dialogBuilder = MaterialAlertDialogBuilder(requireContext())
        dialogBuilder.setTitle("회원가입")
        dialogBuilder.setMessage("이 정보로 가입하시겠습니까?")
        dialogBuilder.setPositiveButton("확인") { dialog, _ ->
            // 다음 단계로 이동
            replaceMainFragment(RegisterStep3Fragment(), false)
            dialog.dismiss()
        }
        dialogBuilder.setNegativeButton("취소") { dialog, _ ->
            dialog.dismiss()
        }
        dialogBuilder.create().show()
    }

}