package com.aladin.finalproject_shoppingmallservice_4_team.ui.notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentNotificationDetailBinding
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment
import java.text.SimpleDateFormat


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
            textViewNotificationDetailDate.text = showDateData(arguments?.getLong("notifyTime", 0L)!!)
        }
    }

    // 날짜 보여주는 메서드
    private fun showDateData(timeData: Long): String {
        val dataFormat1 = SimpleDateFormat("yyyy-MM-dd. HH:mm:ss")
        val date = dataFormat1.format(timeData)
        return date
    }
}