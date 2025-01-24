package com.aladin.finalproject_shoppingmallservice_4_team.ui.notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentNotificationBinding
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentNotificationDetailBinding
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment


class NotificationDetailFragment : Fragment() {

    private var _binding: FragmentNotificationDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNotificationDetailBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadData()
        settingBackButton()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /*
    버튼
     */

    private fun settingBackButton() = binding.materialToolbarNotificationDetail.setNavigationOnClickListener { removeFragment() }

    /*
    데이터
     */

    private fun loadData() {
        binding.apply {
            textViewNotificationDetailTitle.text = arguments?.getString("notifyTitle")
            textViewNotificationDetailContent.text = arguments?.getString("notifyContent")
            textViewNotificationDetailDate.text = arguments?.getLong("notifyTime", 0L).toString()
        }
    }
}