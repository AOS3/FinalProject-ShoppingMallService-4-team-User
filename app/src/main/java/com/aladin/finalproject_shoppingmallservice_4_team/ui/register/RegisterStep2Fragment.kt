package com.aladin.finalproject_shoppingmallservice_4_team.ui.register

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
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
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentRegisterStep1Binding
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentRegisterStep2Binding
import com.aladin.finalproject_shoppingmallservice_4_team.model.UserModel
import com.aladin.finalproject_shoppingmallservice_4_team.ui.custom.CustomDialog
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
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class RegisterStep2Fragment : Fragment() {

    lateinit var fragmentRegisterStep2Binding: FragmentRegisterStep2Binding
    private lateinit var registerViewModel: RegisterViewModel
    private var verificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private lateinit var auth: FirebaseAuth

    // 타이머 변수 선언
    var countdownTimer : CountDownTimer? = null



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // ViewModel
        registerViewModel = ViewModelProvider(this)[RegisterViewModel::class.java]

        auth = FirebaseAuth.getInstance()

        // Inflate the layout for this fragment
        fragmentRegisterStep2Binding = FragmentRegisterStep2Binding.inflate(layoutInflater)

        settingRegister2Toolbar()

        registerButtonListener()

        settingPhoneNumberFormatting()

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

    // 사용 가능한 ID일 때 커스텀 다이얼로그 표시
    private fun showIdConfirmationDialog(userId: String) {
        val dialog = CustomDialog(
            context = requireContext(),
            contentText = "사용 가능한 아이디입니다. 사용하시겠습니까?",
            icon = R.drawable.check_circle_24px, // 적절한 아이콘 리소스
            positiveText = "확인",
            onPositiveClick = {
                // 확인 버튼 클릭 시 동작
                fragmentRegisterStep2Binding.textFieldRegisterStep2Id.editText?.isEnabled = false
                fragmentRegisterStep2Binding.buttonRegisterStep2CheckId.isEnabled = false
                fragmentRegisterStep2Binding.buttonRegisterStep2CheckId.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), android.R.color.darker_gray)
            },
            negativeText = "취소",
            onNegativeClick = {
                // 취소 버튼 클릭 시 동작 (특별히 필요 없으면 빈 블록으로 처리)
            }
        )
        dialog.showCustomDialog()
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

    // 전화번호 입력 자동으로 - 입력되게 하기
    private fun settingPhoneNumberFormatting(){
        fragmentRegisterStep2Binding.textFieldRegisterStep2PhoneNumber.editText?.addTextChangedListener(object : TextWatcher{
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
                fragmentRegisterStep2Binding.textFieldRegisterStep2PhoneNumber.editText?.setText(formatted)
                fragmentRegisterStep2Binding.textFieldRegisterStep2PhoneNumber.editText?.setSelection(formatted.length)

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

    // +82를 자동으로 입력되게 하는 메서드
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

                // 유효성 검사 : 010으로 시작, 11자리
                if(!phoneNumber.startsWith("010") || phoneNumber.length != 13 || phoneNumber.isBlank()) {
                    textFieldRegisterStep2PhoneNumber.error = "유효한 전화번호를 입력해주세요"
                    return@setOnClickListener
                } else {
                    textFieldRegisterStep2PhoneNumber.helperText = " "
                    textFieldRegisterStep2PhoneNumber.error = null
                }

                // 타이머 초기화 및 시작
                countdownTimer?.cancel()
                startCountdownTimer()

                // 인증번호 입력 필드와 버튼 활성화
                textFieldRegisterStep2VerifyNumber.isEnabled = true
                buttonRegisterStep2VerifyCode.isEnabled = true

                // 인증번호 받기 버튼 비활성화
                buttonRegisterStep2VerifyPhoneNumber.isEnabled = false
                buttonRegisterStep2VerifyPhoneNumber.backgroundTintList = ContextCompat.getColorStateList(requireContext(), android.R.color.darker_gray)

                // 3초 후 버튼 활성화
                buttonRegisterStep2VerifyPhoneNumber.postDelayed({
                    buttonRegisterStep2VerifyPhoneNumber.isEnabled = true
                    buttonRegisterStep2VerifyPhoneNumber.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.main_color)
                }, 3000)

                val formattedPhoneNumber = formatPhoneNumber(phoneNumber)
                startPhoneVerification(formattedPhoneNumber)
            }


            // 인증번호 입력 버튼
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

    // 타이머 시작 메서드
    private fun startCountdownTimer() {
        fragmentRegisterStep2Binding.apply {
            textViewRegisterStep2Timer.visibility = View.VISIBLE // 타이머 텍스트뷰 표시
            countdownTimer = object : CountDownTimer(5 * 60 * 1000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val minutes = millisUntilFinished / 1000 / 60
                    val seconds = millisUntilFinished / 1000 % 60
                    textViewRegisterStep2Timer.text = String.format("%02d:%02d", minutes, seconds)
                }

                override fun onFinish() {
                    textViewRegisterStep2Timer.text = "시간 초과"
                    textFieldRegisterStep2VerifyNumber.isEnabled = false // 입력 필드 비활성화
                    buttonRegisterStep2VerifyCode.isEnabled = false // 인증번호 확인 버튼 비활성화
                    buttonRegisterStep2VerifyCode.backgroundTintList = ContextCompat.getColorStateList(requireContext(), android.R.color.darker_gray)
                }
            }.start()
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
                // 인증했으니까 휴대폰 입력, 인증번호입력, 인증버튼2개, 비활성화, 타이머 visible 설정
                fragmentRegisterStep2Binding.textFieldRegisterStep2PhoneNumber.isEnabled = false
                fragmentRegisterStep2Binding.textFieldRegisterStep2VerifyNumber.isEnabled = false
                fragmentRegisterStep2Binding.buttonRegisterStep2VerifyPhoneNumber.isEnabled = false
                fragmentRegisterStep2Binding.buttonRegisterStep2VerifyCode.isEnabled = false
                fragmentRegisterStep2Binding.textViewRegisterStep2Timer.visibility = View.GONE

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

    // 유효성 검사 메서드
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
            } else if (textFieldRegisterStep2Id.isEnabled == false) { // 중복 확인 실패
                textFieldRegisterStep2Id.error = "중복 확인 해주세요"
                isValid = false
            } else {
                textFieldRegisterStep2Id.error = null
                textFieldRegisterStep2Id.helperText = " "
            }

            // 비밀번호 검사
            if (password.isBlank()) {
                textFieldRegisterStep2Password.error = "비밀번호를 입력해주세요"
                isValid = false
            } else if(password.length < 6) {
                textFieldRegisterStep2Password.error = "6자리 이상 입력해주세요"
                isValid = false
            }
            else if (password != passwordCheck) {
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
        val dialog = CustomDialog(
            context = requireContext(),
            contentText = "이 정보로 가입하시겠습니까?",
            icon = R.drawable.check_circle_24px, // 아이콘 리소스
            positiveText = "확인",
            onPositiveClick = {
                // 확인 버튼 클릭 시 동작
                saveUserData()
                replaceMainFragment(RegisterStep3Fragment(), true)
            },
            negativeText = "취소",
            onNegativeClick = {
                // 취소 버튼 클릭 시 동작
            }
        )
        dialog.showCustomDialog()
    }

    // 사용자 데이터를 저장하는 메서드
    private fun saveUserData(){
        fragmentRegisterStep2Binding.apply {
            val userId = textFieldRegisterStep2Id.editText?.text.toString()
            val userPw = textFieldRegisterStep2Password.editText?.text.toString()
            val userName = textFieldRegisterStep2UserName.editText?.text.toString()
            val userPhoneNumber = textFieldRegisterStep2PhoneNumber.editText?.text.toString().replace("-", "") // '-' 제거
            val postCode = textFieldRegisterStep2PostCode.editText?.text.toString()
            val roadAddress = textFieldRegisterStep2Address1.editText?.text.toString()
            val detailAddress = textFieldRegisterStep2Address2.editText?.text.toString()
            val userAddress = "$postCode/$roadAddress/$detailAddress"
            // 간단하게 고유 토큰을 위해 UUID를 사용
            val userToken = UUID.randomUUID().toString().replace("-","")
            val userAutoLoginToken = ""
            val userJoinTime = System.currentTimeMillis()
            val userState = 0

            val user = UserModel().apply {
                this.userId = userId
                this.userPw = userPw
                this.userName = userName
                this.userAddress = userAddress
                this.userPhoneNumber = userPhoneNumber
                this.userToken = userToken
                this.userAutoLoginToken = userAutoLoginToken
                this.userJoinTime = userJoinTime
                this.userState = userState
            }


            registerViewModel.saveUser(user)

            // ViewModel의 저장 결과를 관찰
            registerViewModel.saveUserSuccess.observe(viewLifecycleOwner) { success ->
                if (success) {
                    Toast.makeText(requireContext(), "회원가입 성공!", Toast.LENGTH_SHORT).show()
                    replaceMainFragment(RegisterStep3Fragment(), false)
                } else {
                    Toast.makeText(requireContext(), "회원가입 실패. 다시 시도해주세요.", Toast.LENGTH_LONG).show()
                }
            }
        }

    }

}