package com.aladin.finalproject_shoppingmallservice_4_team.ui.search

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import com.aladin.apiTestApplication.dto.RecommendBookItem
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentSearchBinding
import com.aladin.finalproject_shoppingmallservice_4_team.ui.barcodescanner.BarcodeScannerFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.bookdetail.BookDetailFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.adapter.HomeAdapter
import com.aladin.finalproject_shoppingmallservice_4_team.ui.home.HomeOnClickListener
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceMainFragment


class SearchFragment : Fragment(),HomeOnClickListener {

    private lateinit var binding: FragmentSearchBinding
    private val adapter: HomeAdapter by lazy { HomeAdapter(this) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        combineButtonMethod()
        settingRecyclerView()
        settingEmptyResult()
    }

    /*
    툴바
     */

    private fun toolbarBackButton() = binding.buttonSearchBack.setOnClickListener { removeFragment() }

    private fun toolbarBarcodeButton() = binding.buttonSearchBarcode.setOnClickListener { replaceMainFragment(BarcodeScannerFragment(), true) }

    /*
    버튼
     */

    private fun combineButtonMethod() {
        // 개별 버튼
        settingSearchButton()
        settingMoreButton()
        settingAskButton()
        settingDropMenu()
        // 툴바 버튼
        toolbarBackButton()
        toolbarBarcodeButton()
    }

    private fun settingSearchButton() {
        binding.buttonSearchSearchIcon.setOnClickListener {
            // 검색한다.
        }
    }

    private fun settingMoreButton() {
        binding.buttonSearchMore.setOnClickListener {
            // api의 책 데이터를 더 불러온다.
        }
    }

    private fun settingAskButton() {
        binding.buttonSearchAsk.setOnClickListener {
            // 1대1 문의 창으로 변경한다.
        }
    }

    private fun settingDropMenu() {
        binding.apply {
            // 드롭다운 아이템 목록
            val sortOptions = arrayOf("이름순", "최고가", "최저가")

            // 어댑터 설정
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, sortOptions)
            autoCompleteTextViewSearchSortOrder.setAdapter(adapter)

            // 드롭다운 선택 시 동작
            autoCompleteTextViewSearchSortOrder.setOnItemClickListener { _, _, position, _ ->
                when (sortOptions[position]) {
                    "이름순" -> {
                        renewalRecyclerViewFromDropDown(1)
                        imageViewSearchDropDownText.text = "이름순"
                    }

                    "최고가" -> {
                        renewalRecyclerViewFromDropDown(2)
                        imageViewSearchDropDownText.text = "최고가"
                    }

                    "최저가" -> {
                        renewalRecyclerViewFromDropDown(3)
                        imageViewSearchDropDownText.text = "최저가"
                    }
                }
            }
            // 드롭다운이 닫혔을 때 화살표 모양 바뀌게
            autoCompleteTextViewSearchSortOrder.setOnDismissListener {
                imageViewSearchDropDownIcon.setImageResource(R.drawable.arrow_drop_down_24px)
            }

            // 드롭다운 열리게 설정, 화살표 변경 / 이미 열려있으면 닫히게 설정
            linearLayoutSearchSortOrder.setOnClickListener {
                if (!autoCompleteTextViewSearchSortOrder.isPopupShowing) { // 드롭다운이 열려 있는지 확인
                    autoCompleteTextViewSearchSortOrder.showDropDown()
                    imageViewSearchDropDownIcon.setImageResource(R.drawable.arrow_drop_up_24px)
                } else {
                    autoCompleteTextViewSearchSortOrder.dismissDropDown() // 이미 열려 있으면 닫기
                    imageViewSearchDropDownIcon.setImageResource(R.drawable.arrow_drop_down_24px)
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

    /*
    리사이클러 뷰
     */

    private fun settingEmptyResult() {
        // 검색 결과가 없을 경우
        binding.apply {
            includeSearchEmpty.emptyLayout.isVisible = false
            scrollSearch.isVisible = true
        }
    }

    private fun settingRecyclerView() {
        binding.recyclerViewSearch.adapter = adapter
    }

    override fun itemClickListener(item: RecommendBookItem) {
        // 상세 화면으로 변경한다.
        replaceMainFragment(BookDetailFragment(), true)
    }
}