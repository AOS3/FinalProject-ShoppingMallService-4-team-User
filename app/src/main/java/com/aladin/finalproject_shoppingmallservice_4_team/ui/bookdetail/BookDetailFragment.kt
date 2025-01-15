package com.aladin.finalproject_shoppingmallservice_4_team.ui.bookdetail

import android.graphics.Paint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentBookDetailBinding


class BookDetailFragment : Fragment() {

    private lateinit var binding: FragmentBookDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBookDetailBinding.inflate(inflater, container, false)
        textViewSetting()
        return binding.root
    }

    /*
    텍스트 뷰
     */
    private fun textViewSetting() {
        binding.textViewBookDetailBookPrice.paintFlags = binding.textViewBookDetailBookPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    }
}