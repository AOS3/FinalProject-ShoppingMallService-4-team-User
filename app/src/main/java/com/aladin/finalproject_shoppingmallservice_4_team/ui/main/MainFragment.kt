package com.aladin.finalproject_shoppingmallservice_4_team.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentMainBinding
import com.aladin.finalproject_shoppingmallservice_4_team.guide.GuideFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.ShopFragmentName
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceMainFragment


class MainFragment : Fragment() {

    private lateinit var fragmentMainBinding: FragmentMainBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentMainBinding = FragmentMainBinding.inflate(layoutInflater,container,false)
        setBottomNavigationView()
        return fragmentMainBinding.root
    }

    // 네비게이션 아이콘에 클릭에 따라 화면이 변하게
    fun setBottomNavigationView() {
        fragmentMainBinding.bottomAppBarMain.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    replaceMainFragment(MainFragment(),false)
                    true
                }
                R.id.nav_barcode -> {
                    replaceMainFragment(GuideFragment(),false)
                    true
                }
                R.id.nav_info -> {
                    replaceMainFragment(MainFragment(),false)
                    true
                }
                R.id.nav_like_list -> {
                    replaceMainFragment(MainFragment(),false)
                    true
                }
                else -> false
            }
        }
    }
}