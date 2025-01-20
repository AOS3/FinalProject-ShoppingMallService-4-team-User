package com.aladin.finalproject_shoppingmallservice_4_team.ui.notice

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentNoticeDetailBinding
import com.aladin.finalproject_shoppingmallservice_4_team.ui.custom.CustomDialogProgressbar
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment


class NoticeDetailFragment : Fragment() {
    private lateinit var fragmentNoticeDetailBinding: FragmentNoticeDetailBinding
    private val noticeViewModel: NoticeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentNoticeDetailBinding =
            FragmentNoticeDetailBinding.inflate(layoutInflater, container, false)
        // 툴바 설정
        settingToolbar()
        // 텍스트 설정
        settingTextView()
        return fragmentNoticeDetailBinding.root
    }

    private fun settingTextView() {
        fragmentNoticeDetailBinding.apply {
            textViewNoticeDetailNoticeTitle.text = arguments?.getString("noticeTitle")!!
            textViewNoticeDetailNoticeContent.text = arguments?.getString("noticeContent")!!
            textViewNoticeDetailNoticeDate.text = "작성일 : ${arguments?.getString("noticeDate")!!}"
        }

    }

    private fun settingToolbar() {
        fragmentNoticeDetailBinding.apply {
            materialToolbarNoticeDetail.setNavigationOnClickListener {
                removeFragment()
            }
        }
    }
}