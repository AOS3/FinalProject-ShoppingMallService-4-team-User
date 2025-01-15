package com.aladin.finalproject_shoppingmallservice_4_team.ui.bookdetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentBookListBinding


class BookListFragment : Fragment() {
    private lateinit var fragmentBookListBinding: FragmentBookListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBookListBinding = FragmentBookListBinding.inflate(layoutInflater, container, false)
        return fragmentBookListBinding.root
    }


}