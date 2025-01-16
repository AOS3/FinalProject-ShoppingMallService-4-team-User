package com.aladin.finalproject_shoppingmallservice_4_team.ui.register

import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toolbar
import androidx.core.content.ContextCompat
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentRegisterStep1Binding
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentRegisterStep2Binding
import com.aladin.finalproject_shoppingmallservice_4_team.ui.login.LoginFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceMainFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class RegisterStep2Fragment : Fragment() {

    lateinit var fragmentRegisterStep2Binding: FragmentRegisterStep2Binding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        fragmentRegisterStep2Binding = FragmentRegisterStep2Binding.inflate(layoutInflater)

        settingRegister2Toolbar()

        registerButtonListener()

        sendingVerifyCodeButton()

        checkingIdValidation()

        return fragmentRegisterStep2Binding.root
    }

    // 툴바를 설정하는 메서드
    fun settingRegister2Toolbar(){
        fragmentRegisterStep2Binding.apply {
            toolbarRegisterStep2.setNavigationIcon(R.drawable.arrow_back_ios_24px)
            toolbarRegisterStep2.setNavigationOnClickListener {
                removeFragment()
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

    // 인증번호 보내기 메서드
    fun sendingVerifyCodeButton(){
        fragmentRegisterStep2Binding.apply {
            buttonRegisterStep2VerifyPhoneNumber.setOnClickListener {
                // 유효성 검사 (번호 입력이 안됐을때) + 나중에 더 추가할 예정
                if (textFieldRegisterStep2PhoneNumber.editText?.text?.isBlank() == true) {
                    textFieldRegisterStep2PhoneNumber.error = "전화번호를 입력해주세요"
                } else {
                    // 인증번호가 왔을 때
                    textFieldRegisterStep2PhoneNumber.helperText = "인증번호 전송 성공!"
                }
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