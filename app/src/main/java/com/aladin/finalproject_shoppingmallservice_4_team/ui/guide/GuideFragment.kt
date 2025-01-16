package com.aladin.finalproject_shoppingmallservice_4_team.ui.guide

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentGuideBinding
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment


class GuideFragment : Fragment() {
    private lateinit var fragmentGuideBinding: FragmentGuideBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentGuideBinding = FragmentGuideBinding.inflate(layoutInflater,container,false)
        // 툴바 설정
        settingToolbar()
        return fragmentGuideBinding.root
    }

    private fun settingToolbar() {
        fragmentGuideBinding.apply {
            materialToolbarGuide.setNavigationOnClickListener {
                removeFragment()
            }
        }
    }
}