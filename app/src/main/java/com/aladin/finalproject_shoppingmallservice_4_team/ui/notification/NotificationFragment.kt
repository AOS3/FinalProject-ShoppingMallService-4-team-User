package com.aladin.finalproject_shoppingmallservice_4_team.ui.notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentHomeBinding
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentNotificationBinding
import com.aladin.finalproject_shoppingmallservice_4_team.ui.adapter.NotificationAdapter
import com.aladin.finalproject_shoppingmallservice_4_team.ui.adapter.NotificationOnClickListener
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceMainFragment


class NotificationFragment : Fragment(), NotificationOnClickListener {

    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!
    private val adapter: NotificationAdapter by lazy { NotificationAdapter(this) }

    // 임시 데이터
    val list = List(10) {
        "항목 $it"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        combineButtonMethod()
        settingRecyclerView()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    /*
    버튼
     */

    private fun combineButtonMethod() {
        settingBackButton()
    }

    private fun settingDeleteAllButton() {
        binding.buttonNotificationDeleteAll.setOnClickListener {

        }
    }

    /*
    툴바
     */

    private fun settingBackButton() = binding.materialToolbarNotification.setNavigationOnClickListener { removeFragment() }

    /*
    리사이클러 뷰
     */

    override fun itemClickListener(item: String) {
        // 상세 화면으로 보낸다
        replaceMainFragment(NotificationDetailFragment(), true)
    }

    private fun settingRecyclerView() {
        binding.recyclerViewNotification.adapter = adapter
        adapter.updateList(list.toMutableList())
        // 리사이클러뷰에 스와이프, 드래그 기능 달기
        val swipeHelperCallback = NotificationSwipeCallback(adapter, list.toMutableList()).apply {

        }
        ItemTouchHelper(swipeHelperCallback).attachToRecyclerView(binding.recyclerViewNotification)
    }



}