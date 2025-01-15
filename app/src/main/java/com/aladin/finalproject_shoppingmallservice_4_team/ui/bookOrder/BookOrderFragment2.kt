package com.aladin.finalproject_shoppingmallservice_4_team.ui.bookOrder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentBookOrder1Binding
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentBookOrder2Binding


class BookOrderFragment2 : Fragment() {
    private lateinit var fragmentBookOrder2Binding: FragmentBookOrder2Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBookOrder2Binding =
            FragmentBookOrder2Binding.inflate(layoutInflater, container, false)
        return fragmentBookOrder2Binding.root
    }
}