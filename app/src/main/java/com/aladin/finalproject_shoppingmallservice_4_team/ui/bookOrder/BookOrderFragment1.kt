package com.aladin.finalproject_shoppingmallservice_4_team.ui.bookOrder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentBookOrder1Binding


class BookOrderFragment1 : Fragment() {

    private lateinit var fragmentBookOrder1Binding: FragmentBookOrder1Binding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBookOrder1Binding = FragmentBookOrder1Binding.inflate(layoutInflater,container,false)
        return fragmentBookOrder1Binding.root
    }
}