package com.aladin.finalproject_shoppingmallservice_4_team.ui.search

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.aladin.apiTestApplication.dto.BookItem
import com.aladin.apiTestApplication.dto.RecommendBookItem
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentSearchBinding
import com.aladin.finalproject_shoppingmallservice_4_team.ui.barcodescanner.BarcodeScannerFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.bookdetail.BookDetailFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.adapter.HomeAdapter
import com.aladin.finalproject_shoppingmallservice_4_team.ui.adapter.SearchAdapter
import com.aladin.finalproject_shoppingmallservice_4_team.ui.adapter.SearchOnClickListener
import com.aladin.finalproject_shoppingmallservice_4_team.ui.custom.CustomDialogProgressbar
import com.aladin.finalproject_shoppingmallservice_4_team.ui.home.HomeOnClickListener
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceMainFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment(), SearchOnClickListener {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val adapter: SearchAdapter by lazy { SearchAdapter(this) }

    private val viewModel: SearchViewModel by viewModels()

    private var query = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        combineButtonMethod()
        settingRecyclerView()
        settingEmptyResult()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
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
        settingBackButton()
        // 툴바 버튼
        toolbarBackButton()
        toolbarBarcodeButton()
    }

    private fun settingBackButton() = binding.buttonSearchBack.setOnClickListener { removeFragment() }

    private fun settingSearchButton() {
        binding.apply {
            buttonSearchSearchIcon.setOnClickListener {
                // 검색한다.
                query = editTextSearchSearch.text.toString()
                viewModel.searchBook(query = query, 10, "Accuracy")
                loadingDialog()
                editTextSearchSearch.text.clear()
                buttonSearchMore.isVisible = true
            }
        }
    }

    private fun settingMoreButton() {
        binding.buttonSearchMore.setOnClickListener {
            // api의 책 데이터를 더 불러온다.
            viewModel.searchBook(query, 20, "Accuracy")
            loadingDialog()
            binding.buttonSearchMore.isVisible = false
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
        when (value) {
            1 -> adapter.sortByName()
            2 -> adapter.sortByHighestPrice()
            else -> adapter.sortByLowestPrice()
        }
    }

    /*
    리사이클러 뷰
     */

    private fun settingEmptyResult() {
        binding.apply {
            viewModel.books.observe(viewLifecycleOwner) {
                if (viewModel.books.value?.size == 0) {
                    // 검색 결과가 없을 경우
                    includeSearchEmpty.emptyLayout.isVisible = true
                    scrollSearch.isVisible = false
                } else {
                    includeSearchEmpty.emptyLayout.isVisible = false
                    scrollSearch.isVisible = true
                }
            }
        }
    }

    private fun updateRecyclerView() {
        viewModel.books.observe(viewLifecycleOwner) {
            adapter.updateList(it.toMutableList())
        }
    }

    private fun settingRecyclerView() {
        binding.recyclerViewSearch.adapter = adapter
        updateRecyclerView()
    }

    override fun itemClickListener(item: BookItem) {
        // 상세 화면으로 변경한다.
        val dataBundle = Bundle()
        dataBundle.putString("bookIsbn", item.isbn13)
        replaceMainFragment(BookDetailFragment(), true, dataBundle = dataBundle)
    }

    /*
    다이얼로그
     */

    private fun loadingDialog() {
        // 로딩을 위한 Dialog
        val progressBarDialog = CustomDialogProgressbar(requireContext())
        progressBarDialog.show()

        viewModel.isLoadSearchList.observe(viewLifecycleOwner) {
            if (it) {
                progressBarDialog.dismiss()
            }
        }
    }

}