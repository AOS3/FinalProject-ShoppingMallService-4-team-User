package com.aladin.finalproject_shoppingmallservice_4_team.ui.shoppingcart

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentShoppingCartBinding
import com.aladin.finalproject_shoppingmallservice_4_team.ui.mainMenu.MainMenuFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.notice.NoticeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceMainFragment

class ShoppingCartFragment : Fragment() {

    private lateinit var fragmentShoppingCartBinding: FragmentShoppingCartBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentShoppingCartBinding = FragmentShoppingCartBinding.inflate(layoutInflater,container,false)
        // 툴바 세팅
        settingToolbar()
        return fragmentShoppingCartBinding.root
    }

    private fun settingToolbar() {
        fragmentShoppingCartBinding.apply {
            materialToolbarShoppingCart.setNavigationOnClickListener {
                removeFragment()
            }
            materialToolbarShoppingCart.setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.item_shoppingCart_notification -> {
                        replaceMainFragment(NoticeFragment(),true)
                        true
                    }
                    else -> {
                        replaceMainFragment(MainMenuFragment(),true)
                        true
                    }
                }
            }
        }
    }
}