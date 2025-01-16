package com.aladin.finalproject_shoppingmallservice_4_team.ui.likeList

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentLikeListBinding
import com.aladin.finalproject_shoppingmallservice_4_team.ui.mainMenu.MainMenuFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.notice.NoticeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceMainFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceSubFragment


class LikeListFragment : Fragment() {
    private lateinit var fragmentLikeListBinding: FragmentLikeListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentLikeListBinding = FragmentLikeListBinding.inflate(layoutInflater)
        // 툴바 설정
        settingToolbar()
        return fragmentLikeListBinding.root
    }

    private fun settingToolbar() {
        fragmentLikeListBinding.apply {
            materialToolbarLikeList.setNavigationOnClickListener {
                removeFragment()
            }
            materialToolbarLikeList.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.item_likeList_shoppingCart -> {
                        // 쇼핑카트 이동 예정
                        true
                    }

                    R.id.item_likeList_notification -> {
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