package com.aladin.finalproject_shoppingmallservice_4_team.ui.changePw

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentChangePwBinding
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment


class ChangePwFragment : Fragment() {
    private lateinit var fragmentChangePwBinding: FragmentChangePwBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentChangePwBinding = FragmentChangePwBinding.inflate(layoutInflater, container, false)
        settingToolbar()
        return fragmentChangePwBinding.root
    }

    private fun settingToolbar() {
        fragmentChangePwBinding.apply {
            materialToolbarChangePw.setNavigationOnClickListener {
                removeFragment()
            }
        }
    }
}