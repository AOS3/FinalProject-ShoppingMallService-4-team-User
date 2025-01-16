package com.aladin.finalproject_shoppingmallservice_4_team.ui.notice

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentNoticeBinding
import com.aladin.finalproject_shoppingmallservice_4_team.ui.mainMenu.MainMenuFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceSubFragment


class NoticeFragment : Fragment() {
    private lateinit var fragmentNoticeBinding: FragmentNoticeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentNoticeBinding = FragmentNoticeBinding.inflate(layoutInflater, container, false)
        // 툴바 설정
        settingToolbar()
        return fragmentNoticeBinding.root
    }

    private fun settingToolbar() {
        fragmentNoticeBinding.apply {
            materialToolbarNotice.setNavigationOnClickListener {
                removeFragment()
            }
        }
    }
}