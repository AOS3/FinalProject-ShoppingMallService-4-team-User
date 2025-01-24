package com.aladin.finalproject_shoppingmallservice_4_team.ui.splash

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.replace
import com.aladin.finalproject_shoppingmallservice_4_team.BookApplication
import com.aladin.finalproject_shoppingmallservice_4_team.BuildConfig
import com.aladin.finalproject_shoppingmallservice_4_team.MainActivity
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentSplashBinding
import com.aladin.finalproject_shoppingmallservice_4_team.ui.login.LoginFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.login.SharedPreferencesHelper
import com.aladin.finalproject_shoppingmallservice_4_team.ui.login.UserRepository
import com.aladin.finalproject_shoppingmallservice_4_team.ui.main.MainFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceMainFragment
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SplashFragment : Fragment() {

    private lateinit var fragmentSplashBinding: FragmentSplashBinding
    private lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentSplashBinding = FragmentSplashBinding.inflate(inflater, container, false)
        mainActivity = activity as MainActivity
        // 애니메이트 함수
        animateImageView()
        return fragmentSplashBinding.root
    }

    private fun animateImageView() {
        // 초기 상태를 투명하게 설정
        fragmentSplashBinding.apply {
            imageViewMainLogo.alpha = 0f
            imageViewMainLogo.visibility = View.VISIBLE

            // 3초 동안 서서히 나타나게 하는 애니메이션 실행
            imageViewMainLogo.animate()
                .alpha(1f) // 완전히 나타나도록 alpha 값을 1로 설정
                .setDuration(3000) // 애니메이션 지속 시간: 3초
                .withEndAction {
                    // 애니메이션 완료 후 실행할 함수
                    // replaceMainFragment()
                    checkAutoLogin()
                }
                .start()
        }
    }

    // 다음 화면 으로 이동하는 함수
    private fun replaceMainFragment() {
        replaceMainFragment(MainFragment(),false)
    }

    private fun checkAutoLogin() {
        Log.d("test100", "checkAutoLogin 호출됨")
        val sharedPreferencesHelper = SharedPreferencesHelper(requireContext())
        val autoLoginToken = sharedPreferencesHelper.getAutoLoginToken()

        if (autoLoginToken != null) {
            CoroutineScope(Dispatchers.Main).launch {
                val userRepository = UserRepository(FirebaseFirestore.getInstance())
                val user = userRepository.validateAutoLoginToken(autoLoginToken)
                if (user != null) {
                    Log.d("test100", "유효한 토큰 -> 메인 화면 이동")
                    val bookApplication = requireActivity().application as BookApplication
                    bookApplication.loginUserModel = user
                    replaceMainFragment(MainFragment(), false)
                } else {
                    Log.d("test100", "유효하지 않은 토큰 -> 비로그인 상태로 메인 화면 이동")
                    // Toast.makeText(requireContext(), "자동 로그인이 만료되었습니다.", Toast.LENGTH_SHORT).show()
                    replaceMainFragment(MainFragment(), false)
                }
            }
        } else {
            Log.d("test100", "토큰이 없음 -> 비로그인 상태로 메인 화면 이동")
            replaceMainFragment(MainFragment(), false)
        }
    }
}