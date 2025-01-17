package com.aladin.finalproject_shoppingmallservice_4_team.ui.orderinquiry

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentOrderInquiryDetailBinding
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment


class OrderInquiryDetailFragment : Fragment() {

    private lateinit var binding: FragmentOrderInquiryDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOrderInquiryDetailBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbarButton()
    }

    /*
    툴바
     */

    private fun toolbarButton() {
        toolbarBackButton()
    }

    private fun toolbarBackButton() = binding.materialToolbarOrderInquiryDetail.setNavigationOnClickListener { removeFragment() }
}