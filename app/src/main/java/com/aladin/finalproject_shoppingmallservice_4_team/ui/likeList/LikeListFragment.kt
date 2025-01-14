package com.aladin.finalproject_shoppingmallservice_4_team.ui.likeList

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentLikeListBinding


class LikeListFragment : Fragment() {
    private lateinit var fragmentLikeListBinding: FragmentLikeListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentLikeListBinding = FragmentLikeListBinding.inflate(layoutInflater)
        return fragmentLikeListBinding.root
    }
}