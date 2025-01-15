package com.aladin.finalproject_shoppingmallservice_4_team.ui.notice

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentNoticeDetailBinding


class NoticeDetailFragment : Fragment() {
    private lateinit var fragmentNoticeDetailBinding: FragmentNoticeDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentNoticeDetailBinding = FragmentNoticeDetailBinding.inflate(layoutInflater, container, false)
        return fragmentNoticeDetailBinding.root
    }

}