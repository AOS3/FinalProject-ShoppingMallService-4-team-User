package com.aladin.finalproject_shoppingmallservice_4_team.ui.bookdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentBookDetailBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class BookDetailBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentBookDetailBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBookDetailBottomSheetBinding.inflate(inflater, container, false)

        return binding.root
    }
}