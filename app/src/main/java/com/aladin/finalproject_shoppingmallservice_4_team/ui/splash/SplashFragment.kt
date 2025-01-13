package com.aladin.finalproject_shoppingmallservice_4_team.ui.splash

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.aladin.finalproject_shoppingmallservice_4_team.BuildConfig
import com.aladin.finalproject_shoppingmallservice_4_team.MainActivity
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentSplashBinding


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
                    replaceMainFragment()
                }
                .start()
        }
    }

    // 다음 화면 으로 이동하는 함수
    private fun replaceMainFragment() {
        Toast.makeText(
            mainActivity,
            "메인 화면 이동!",
            Toast.LENGTH_SHORT
        ).show()
    }
}