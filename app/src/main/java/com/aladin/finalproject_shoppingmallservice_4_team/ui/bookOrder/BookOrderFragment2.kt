package com.aladin.finalproject_shoppingmallservice_4_team.ui.bookOrder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentBookOrder1Binding
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentBookOrder2Binding
import com.aladin.finalproject_shoppingmallservice_4_team.ui.mainMenu.MainMenuFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.notice.NoticeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceSubFragment


class BookOrderFragment2 : Fragment() {
    private lateinit var fragmentBookOrder2Binding: FragmentBookOrder2Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBookOrder2Binding = FragmentBookOrder2Binding.inflate(layoutInflater, container, false)
        // 툴바 세팅
        settingToolbar()
        return fragmentBookOrder2Binding.root
    }

    private fun settingToolbar() {
        fragmentBookOrder2Binding.apply {
            materialToolbarBookOrder2.setNavigationOnClickListener {
                removeFragment()
            }
            materialToolbarBookOrder2.setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.home_menu_shoppingcart -> {
                        // 장바구니로 이동 예정
                        true
                    }
                    R.id.home_menu_notification -> {
                        replaceSubFragment(NoticeFragment(),true)
                        true
                    }
                    else -> {
                        replaceSubFragment(MainMenuFragment(),true)
                        true
                    }
                }
            }
        }
    }
}