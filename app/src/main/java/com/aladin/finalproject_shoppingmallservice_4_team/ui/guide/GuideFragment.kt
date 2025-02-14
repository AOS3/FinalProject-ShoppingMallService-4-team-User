package com.aladin.finalproject_shoppingmallservice_4_team.ui.guide

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentGuideBinding
import com.aladin.finalproject_shoppingmallservice_4_team.ui.ask.AskFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceSubFragment


class GuideFragment : Fragment() {
    private lateinit var fragmentGuideBinding: FragmentGuideBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentGuideBinding = FragmentGuideBinding.inflate(layoutInflater,container,false)
        // 툴바 설정
        settingToolbar()
        // Footer 설정
        settingFooter()
        return fragmentGuideBinding.root
    }

    // Toolbar
    private fun settingToolbar() {
        fragmentGuideBinding.apply {
            materialToolbarGuide.setNavigationOnClickListener {
                removeFragment()
            }
        }
    }

    // Footer
    private fun settingFooter() {
        fragmentGuideBinding.apply {
            buttonGuideAsk.setOnClickListener {
                replaceSubFragment(AskFragment(),true)
            }
        }
    }
}