package com.aladin.finalproject_shoppingmallservice_4_team.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentMainBinding
import com.aladin.finalproject_shoppingmallservice_4_team.ui.barcodescanner.BarcodeScannerFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.home.HomeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.likeList.LikeListFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.login.LoginFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.clearAllBackStack
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceMainFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceSubFragment


class MainFragment : Fragment() {

    private lateinit var fragmentMainBinding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentMainBinding = FragmentMainBinding.inflate(layoutInflater,container,false)
        setBottomNavigationView()
        replaceSubFragment(HomeFragment(), false)
        return fragmentMainBinding.root
    }

    // 네비게이션 아이콘에 클릭에 따라 화면이 변하게
    private fun setBottomNavigationView() {
        fragmentMainBinding.bottomAppBarMain.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    clearAllBackStack()
                    replaceSubFragment(HomeFragment(),false)
                }
                R.id.nav_barcode -> {
                    clearAllBackStack()
                    replaceMainFragment(BarcodeScannerFragment(),true)
                }
                R.id.nav_info -> {
                    clearAllBackStack()
                    replaceSubFragment(LoginFragment(),true)
                }
                R.id.nav_like_list -> {
                    clearAllBackStack()
                    replaceSubFragment(LikeListFragment(),true)
                }
            }
            true
        }
    }
}