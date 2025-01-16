package com.aladin.finalproject_shoppingmallservice_4_team.ui.booklist

import android.annotation.SuppressLint
import android.graphics.drawable.InsetDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.view.menu.MenuBuilder
import com.aladin.finalproject_shoppingmallservice_4_team.MainActivity
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentBookListBinding
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class BookListFragment : Fragment() {
    private lateinit var fragmentBookListBinding: FragmentBookListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBookListBinding = FragmentBookListBinding.inflate(layoutInflater, container, false)
        // 드롭다운 세팅
        settingDropMenu()
        // 툴바 세팅
        settingToolbar()
        return fragmentBookListBinding.root
    }

    private fun settingToolbar() {
        fragmentBookListBinding.apply {
            materialToolbarBookList.setNavigationOnClickListener {
                removeFragment()
            }
        }
    }

    private fun settingDropMenu() {
        // 드롭다운 아이템 목록
        val sortOptions = arrayOf("이름순", "최고가", "최저가")

        // 어댑터 설정
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, sortOptions)
        fragmentBookListBinding.autoCompleteTextViewBookListSortOrder.setAdapter(adapter)

        // 드롭다운 선택 시 동작
        fragmentBookListBinding.autoCompleteTextViewBookListSortOrder.setOnItemClickListener { _, _, position, _ ->
            when (sortOptions[position]) {
                "이름순" -> {
                    renewalRecyclerViewFromDropDown(1)
                }

                "최고가" -> {
                    renewalRecyclerViewFromDropDown(2)
                }

                "최저가" -> {
                    renewalRecyclerViewFromDropDown(3)
                }
            }
        }

        fragmentBookListBinding.linearLayoutBookListSortOrder.setOnClickListener {
            fragmentBookListBinding.autoCompleteTextViewBookListSortOrder.showDropDown()
        }
    }

    // 1 -> 이름순
    // 1 -> 최고가
    // 1 -> 최저가
    // 드롭다운 선택에 따라 recyclerView가 갱신
    private fun renewalRecyclerViewFromDropDown(value: Int) {
        Log.e("asdasd", "$value")
    }
}