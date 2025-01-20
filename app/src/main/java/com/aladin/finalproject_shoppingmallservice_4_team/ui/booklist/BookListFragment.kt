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
import androidx.fragment.app.viewModels
import com.aladin.finalproject_shoppingmallservice_4_team.MainActivity
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentBookListBinding
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookListFragment : Fragment() {
    private lateinit var fragmentBookListBinding: FragmentBookListBinding
    private val bookListViewModel: BookListViewModel by viewModels()

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
        fragmentBookListBinding.apply {
            // 드롭다운 아이템 목록
            val sortOptions = arrayOf("이름순", "최고가", "최저가")

            // 어댑터 설정
            val adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, sortOptions)
            autoCompleteTextViewBookListSortOrder.setAdapter(adapter)

            // 드롭다운 선택 시 동작
            autoCompleteTextViewBookListSortOrder.setOnItemClickListener { _, _, position, _ ->
                when (sortOptions[position]) {
                    "이름순" -> {
                        renewalRecyclerViewFromDropDown(1)
                        imageViewBookListDropDownText.text = "이름순"
                    }

                    "최고가" -> {
                        renewalRecyclerViewFromDropDown(2)
                        imageViewBookListDropDownText.text = "최고가"
                    }

                    "최저가" -> {
                        renewalRecyclerViewFromDropDown(3)
                        imageViewBookListDropDownText.text = "최저가"
                    }
                }
            }
            // 드롭다운이 닫혔을 때 화살표 모양 바뀌게
            autoCompleteTextViewBookListSortOrder.setOnDismissListener {
                imageViewBookListDropDownIcon.setImageResource(R.drawable.arrow_drop_down_24px)
            }

            // 드롭다운 열리게 설정, 화살표 변경 / 이미 열려있으면 닫히게 설정
            linearLayoutBookListSortOrder.setOnClickListener {
                if (!autoCompleteTextViewBookListSortOrder.isPopupShowing) { // 드롭다운이 열려 있는지 확인
                    autoCompleteTextViewBookListSortOrder.showDropDown()
                    imageViewBookListDropDownIcon.setImageResource(R.drawable.arrow_drop_up_24px)
                } else {
                    autoCompleteTextViewBookListSortOrder.dismissDropDown() // 이미 열려 있으면 닫기
                    imageViewBookListDropDownIcon.setImageResource(R.drawable.arrow_drop_down_24px)
                }
            }
        }
    }

    // 1 -> 이름순
    // 1 -> 최고가
    // 1 -> 최저가
    // 드롭다운 선택에 따라 recyclerView가 갱신
    private fun renewalRecyclerViewFromDropDown(value: Int) {
        Log.e("asdasd", "$value")
    }


    // ItemNewAll : 신간 전체 리스트
    // ItemNewSpecial : 주목할 만한 신간 리스트
    // Bestseller : 베스트셀러
    // BlogBest : 블로거 베스트셀러
    // Used : 중고

}