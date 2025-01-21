package com.aladin.finalproject_shoppingmallservice_4_team.ui.login

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.aladin.finalproject_shoppingmallservice_4_team.BookApplication
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentLoginBinding
import com.aladin.finalproject_shoppingmallservice_4_team.ui.findid.FindIdFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.findpw.FindPwFragment1
import com.aladin.finalproject_shoppingmallservice_4_team.ui.home.HomeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.register.RegisterStep1Fragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceMainFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceSubFragment
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginFragment : Fragment() {

    lateinit var fragmentLoginBinding: FragmentLoginBinding

    private lateinit var loginViewModel : LoginViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentLoginBinding = FragmentLoginBinding.inflate(layoutInflater,container,false)

        settingViewModel()

        settingLoginToolbar()

        settingObservers()

        settingButtons()

        return fragmentLoginBinding.root
    }

    fun settingViewModel() {
        // FirebaseFirestore 인스턴스를 가져옴
        val firestore = FirebaseFirestore.getInstance()
        val userRepository = UserRepository(firestore)

        // ViewModel 수동 생성
        loginViewModel = ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                        val application = requireContext().applicationContext as BookApplication
                        return LoginViewModel(userRepository, application) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        )[LoginViewModel::class.java]
    }

    // 툴바를 세팅하는 메서드
    fun settingLoginToolbar(){
        fragmentLoginBinding.apply {
            materialToolbarLogin.title = "로그인"
            materialToolbarLogin.setNavigationIcon(R.drawable.arrow_back_ios_24px)
        }
    }

    fun settingObservers(){
        loginViewModel.loginErrorMessage.observe(viewLifecycleOwner) { errorMessage ->
            fragmentLoginBinding.textFieldLoginPassword.error = null
            fragmentLoginBinding.textFieldLoginId.error = null

            if(errorMessage.isNotBlank()) {
                when {
                    errorMessage.contains("아이디") -> fragmentLoginBinding.textFieldLoginId.error = errorMessage
                    errorMessage.contains("비밀번호") -> fragmentLoginBinding.textFieldLoginPassword.error = errorMessage
                }
            }
        }

        loginViewModel.loginResult.observe(viewLifecycleOwner) { isSucces ->
            if (isSucces) {
                Toast.makeText(requireContext(), "로그인 성공", Toast.LENGTH_SHORT).show()
                replaceSubFragment(HomeFragment(),false)
            }
        }
    }

    // 버튼 리스너들
    fun settingButtons(){
        fragmentLoginBinding.apply {
            // 아이디 찾기 화면으로 이동
            buttonLoginFindId.setOnClickListener {
                replaceMainFragment(FindIdFragment(),true)
            }

            // 비밀번호 찾기 화면으로 이동
            buttonLoginFindPw.setOnClickListener {
                replaceMainFragment(FindPwFragment1(),true)
            }

            // 회원가입 화면으로 이동
            buttonLoginRegister.setOnClickListener {
                replaceMainFragment(RegisterStep1Fragment(),true)
            }

            // 로그인 처리 방법
            buttonLoginLogin.setOnClickListener {
                val userId = textFieldLoginId.editText?.text.toString()
                val password = textFieldLoginPassword.editText?.text.toString()

                if (userId.isBlank()) {
                    textFieldLoginId.error = "아이디를 입력해주세요."
                    return@setOnClickListener
                }

                if (password.isBlank()) {
                    textFieldLoginPassword.error = "비밀번호를 입력해주세요."
                    return@setOnClickListener
                }

                // ViewModel에 입력값 전달
                loginViewModel.textFieldUserLoginIdValue.value = userId
                loginViewModel.textFieldUserLoginPasswordValue.value = password

                // 로그인 처리
                loginViewModel.buttonUserLogin()
            }

        }
    }
}