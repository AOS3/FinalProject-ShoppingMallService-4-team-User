package com.aladin.finalproject_shoppingmallservice_4_team.ui.notice

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentNoticeBinding
import com.aladin.finalproject_shoppingmallservice_4_team.ui.adapter.NoticeAdapter
import com.aladin.finalproject_shoppingmallservice_4_team.ui.custom.CustomDialogProgressbar
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NoticeFragment : Fragment() {
    private lateinit var fragmentNoticeBinding: FragmentNoticeBinding
    private lateinit var noticeAdapter: NoticeAdapter
    private val noticeViewModel: NoticeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentNoticeBinding = FragmentNoticeBinding.inflate(layoutInflater, container, false)

        // 공지사항 목록을 읽어온다.
        noticeViewModel.gettingNoticeData()

        // 툴바 설정
        settingToolbar()

        // 리싸이클러뷰 설정
        setupRecyclerView()

        // 공지사항 데이터 옵저버 설정
        observeNoticeData()

        // ProgressDialog 옵저버 설정
        observeProgressDialog()

        return fragmentNoticeBinding.root
    }

    // Dialog
    private fun observeProgressDialog() {
        // 화면 입장 시 공지사항 로딩을 위한 Dialog
        val progressBarDialog = CustomDialogProgressbar(requireContext())
        progressBarDialog.show()

        noticeViewModel.isLoadNoticeList.observe(viewLifecycleOwner) {
            if (it) {
                progressBarDialog.dismiss()
            }
        }
    }


    // Toolbar
    private fun settingToolbar() {
        fragmentNoticeBinding.apply {
            materialToolbarNotice.setNavigationOnClickListener {
                removeFragment()
            }

        }
    }

    // RecyclerView
    private fun setupRecyclerView() {
        fragmentNoticeBinding.apply {
            noticeAdapter = NoticeAdapter(emptyList(), this@NoticeFragment) // 초기값으로 빈 리스트
            recyclerViewNotice.layoutManager = LinearLayoutManager(context)
            recyclerViewNotice.adapter = noticeAdapter
        }
    }

    // Observer
    private fun observeNoticeData() {
        // LiveData에서 데이터를 받아와서 RecyclerView 업데이트
        noticeViewModel.noticeList.observe(viewLifecycleOwner) { noticeList ->
            fragmentNoticeBinding.apply {
                if (noticeList.isNotEmpty()) {
                    // 데이터를 받아왔을 때, RecyclerView에 전달
                    noticeAdapter =
                        NoticeAdapter(noticeList, this@NoticeFragment) // 새 데이터로 Adapter를 갱신
                    recyclerViewNotice.adapter = noticeAdapter
                    textViewNoticeEmptyItem.visibility = View.GONE
                } else {
                    // 데이터가 없을 시
                    textViewNoticeEmptyItem.visibility = View.VISIBLE
                }
            }
        }
    }
}