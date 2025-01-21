package com.aladin.finalproject_shoppingmallservice_4_team.ui.notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentNotificationBinding
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentNotificationDetailBinding


class NotificationDetailFragment : Fragment() {

    private lateinit var binding: FragmentNotificationDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNotificationDetailBinding.inflate(inflater, container, false)

        return binding.root
    }
}