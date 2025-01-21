package com.aladin.finalproject_shoppingmallservice_4_team.ui.register

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
import java.util.concurrent.TimeUnit

class RegisterStep2Fragment : Fragment() {

    lateinit var fragmentRegisterStep2Binding: FragmentRegisterStep2Binding
    private var verificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private lateinit var auth: FirebaseAuth

    // 중복확인 디폴트값 false
    private var checkIdUsable = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        auth = FirebaseAuth.getInstance()

        // Inflate the layout for this fragment
        fragmentRegisterStep2Binding = FragmentRegisterStep2Binding.inflate(layoutInflater)

        settingRegister2Toolbar()

        registerButtonListener()

        settingPhoneVerification()

        // 카카오 주소 api
        settingKakaoPostCode()

        checkingIdValidation()

        return fragmentRegisterStep2Binding.root
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
                }

                override fun onCodeSent(verificationId:String, token: PhoneAuthProvider.ForceResendingToken) {
                    this@RegisterStep2Fragment.verificationId = verificationId
                    this@RegisterStep2Fragment.resendToken = token
                    fragmentRegisterStep2Binding.textFieldRegisterStep2PhoneNumber.helperText = " "
                    fragmentRegisterStep2Binding.textFieldRegisterStep2VerifyNumber.helperText = " "

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


            } else {
                Toast.makeText(requireContext(), "인증 실패: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                fragmentRegisterStep2Binding.textFieldRegisterStep2VerifyNumber.error = "인증번호가 일치하지 않습니다"
            }
        }
    }


    // 유효성 검사 메서드
    fun checkingIdValidation(){
        fragmentRegisterStep2Binding.apply {
            // 중복검사 버튼
            buttonRegisterStep2CheckId.setOnClickListener {
                // 임시로
                if(textFieldRegisterStep2Id.editText?.text?.isEmpty() == true) {
                    textFieldRegisterStep2Id.error = "아이디를 입력해주세요"
                }
                else{
                    textFieldRegisterStep2Id.helperText = "사용 가능한 아이디입니다"
                }
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

                // 비밀번호1가 비어있으면
                if(textFieldRegisterStep2Password.editText?.text?.isEmpty() == true) {
                    textFieldRegisterStep2Password.error = "비밀번호를 입력해주세요"
                } else {
                    textFieldRegisterStep2Password.helperText = "비밀번호 OK!"
                }

                // 비밀번호2가 비어있으면
                if(textFieldRegisterStep2PasswordCheck.editText?.text?.isEmpty() == true){
                    textFieldRegisterStep2PasswordCheck.error = "비밀번호를 입력해주세요"
                } else {
                    textFieldRegisterStep2PasswordCheck.helperText = "비밀번호 OK!"
                }

                // 비밀번호1이랑 비밀번호2가 다르면
                if(textFieldRegisterStep2Password.editText?.text.toString() != textFieldRegisterStep2PasswordCheck.editText?.text.toString()){
                    textFieldRegisterStep2Password.error = null
                    textFieldRegisterStep2PasswordCheck.error = "비밀번호가 일치하지 않습니다"
                } else{
                    textFieldRegisterStep2PasswordCheck.helperText = "비밀번호 OK!"
                }

                // 주소가 없다면 -> (우편번호랑 도로명주소는 어차피 같이 움직이니까)
                if(textFieldRegisterStep2Address2.editText?.text?.isEmpty() == true){
                    textFieldRegisterStep2Address2.error = "주소를 입력해주세요"
                } else {
                    textFieldRegisterStep2Address2.helperText = "주소 OK!"
                }

                // 인증번호가 없다면
                if(textFieldRegisterStep2VerifyNumber.editText?.text?.isEmpty() == true){
                    textFieldRegisterStep2VerifyNumber.error = "인증번호를 입력해주세요"
                } else {


                    // 임시로 여기에다가 둠
                    val dialogBuilder = MaterialAlertDialogBuilder(requireContext())
                    dialogBuilder.setTitle("회원가입")
                    dialogBuilder.setMessage("이 정보로 가입하시겠습니까?")
                    dialogBuilder.setPositiveButton("확인") { dialog, _ ->
                        replaceMainFragment(RegisterStep3Fragment(),false)
                        dialog.dismiss()
                    }
                    dialogBuilder.setNegativeButton("취소") { dialog, _ ->
                        dialog.dismiss()
                    }
                    dialogBuilder.create().show()

                }

                // 나중에 각 TextField에 error이 없으면 Dialog를 띄워 넘어갈건지 구현


            }
        }
    }

}