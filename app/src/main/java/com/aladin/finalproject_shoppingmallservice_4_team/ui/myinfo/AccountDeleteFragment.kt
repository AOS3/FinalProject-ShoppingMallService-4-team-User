package com.aladin.finalproject_shoppingmallservice_4_team.ui.myinfo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentAccountDeleteBinding

class AccountDeleteFragment : Fragment() {

    lateinit var fragmentAccountDeleteBinding: FragmentAccountDeleteBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        fragmentAccountDeleteBinding = FragmentAccountDeleteBinding.inflate(inflater, container, false)

        return fragmentAccountDeleteBinding.root
    }


}