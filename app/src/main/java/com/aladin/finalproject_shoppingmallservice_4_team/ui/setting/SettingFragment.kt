package com.aladin.finalproject_shoppingmallservice_4_team.ui.setting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aladin.finalproject_shoppingmallservice_4_team.MainActivity
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentSettingBinding


class SettingFragment : Fragment() {
    private lateinit var fragmentSettingBinding: FragmentSettingBinding
    private lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentSettingBinding = FragmentSettingBinding.inflate(layoutInflater, container, false)
        mainActivity = activity as MainActivity
        return fragmentSettingBinding.root
    }
}