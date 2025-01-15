package com.aladin.finalproject_shoppingmallservice_4_team.ui.shoppingcart

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentShoppingCartBinding

class ShoppingCartFragment : Fragment() {

    private lateinit var fragmentShoppingCartBinding: FragmentShoppingCartBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentShoppingCartBinding = FragmentShoppingCartBinding.inflate(layoutInflater,container,false)
        return fragmentShoppingCartBinding.root
    }
}