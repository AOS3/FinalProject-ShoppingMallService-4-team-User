package com.aladin.finalproject_shoppingmallservice_4_team.ui.qualityguide

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentMainMenuBinding
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentQualityGuideBinding

class QualityGuideFragment : Fragment() {

    private lateinit var fragmentQualityGuideBinding: FragmentQualityGuideBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        fragmentQualityGuideBinding = FragmentQualityGuideBinding.inflate(layoutInflater, container, false)

        // Toolbar를 구성하는 메서드를 호출한다.
        settingToolbar()

        return fragmentQualityGuideBinding.root
    }

    // Toolbar를 구성하는 메서드
    private fun settingToolbar() {
        fragmentQualityGuideBinding.apply {
            materialToolbarQualityGuide.title = "품질 가이드"
            // 네비게이션 아이콘을 설정하고 누를 경우 NavigationView가 나타나도록 한다.
            materialToolbarQualityGuide.setNavigationIcon(R.drawable.arrow_back_ios_24px)
            materialToolbarQualityGuide.setNavigationOnClickListener {
                // 전 화면으로 이동

            }
        }
    }

    // 버튼을 클릭 했을 때 메서드
    private fun buttonQualityGuideOnClick() {
        fragmentQualityGuideBinding.apply {

        }
    }
}