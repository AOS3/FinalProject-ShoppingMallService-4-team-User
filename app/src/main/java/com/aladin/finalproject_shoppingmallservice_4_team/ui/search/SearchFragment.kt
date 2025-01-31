package com.aladin.finalproject_shoppingmallservice_4_team.ui.search

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Toast
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
import com.aladin.finalproject_shoppingmallservice_4_team.ui.ask.AskFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.custom.CustomDialogProgressbar
import com.aladin.finalproject_shoppingmallservice_4_team.ui.home.HomeOnClickListener
import com.aladin.finalproject_shoppingmallservice_4_team.util.hideSoftInput
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceMainFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceSubFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.showSoftInput
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
        settingBarcode()
        settingEmptyResult()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /*
    툴바
     */

    private fun toolbarBackButton() = binding.buttonSearchBack.setOnClickListener { removeFragment() }

    private fun toolbarBarcodeButton() = binding.buttonSearchBarcode.setOnClickListener {
        val dataBundle = Bundle()
        dataBundle.putString("FragmentQuery", "Search")
        replaceSubFragment(BarcodeScannerFragment(), true, dataBundle)
    }

    /*
    버튼
     */

    private fun combineButtonMethod() {
        // 개별 버튼
        settingSearchButton()
        settingMoreButton()
        settingAskButton()
        settingDropMenu()
        settingSearchEnter()
        // 툴바 버튼
        toolbarBackButton()
        toolbarBarcodeButton()
    }

    private fun settingSearchEnter() {
        binding.editTextSearchSearch.setOnEditorActionListener { v, actionId, event ->
            query = v.text.toString().trim()
            if (query.isNotEmpty()) {
                // 유효한 검색어가 입력된 경우 검색 실행
                viewModel.searchBook(query = query, maxResults = 10, sort = "Accuracy")
                loadingDialog() // 로딩 다이얼로그 표시
                binding.editTextSearchSearch.text.clear() // 입력창 초기화
                binding.editTextSearchSearch.hideSoftInput()
                binding.buttonSearchMore.isVisible = true // 더보기 버튼 활성화
                true
            }
            else {
                // 검색어가 비어있는 경우 사용자에게 알림
                Toast.makeText(requireContext(), "검색어를 입력하세요.", Toast.LENGTH_SHORT).show()
                false
            }
        }
    }

    private fun settingSearchButton() {
        binding.apply {
            buttonSearchSearchIcon.setOnClickListener {
                // 검색한다.
                query = editTextSearchSearch.text.toString().trim()
                if (query.isNotEmpty()) {
                    viewModel.searchBook(query = query, 10, "Accuracy")
                    loadingDialog()
                    editTextSearchSearch.text.clear()
                    editTextSearchSearch.hideSoftInput()
                    buttonSearchMore.isVisible = true
                }
                else{
                    // 검색어가 비어있는 경우 사용자에게 알림
                    Toast.makeText(requireContext(), "검색어를 입력하세요.", Toast.LENGTH_SHORT).show()
                }
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
            replaceMainFragment(AskFragment(), true)
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
            1 -> viewModel.checkNameFilter()
            2 -> viewModel.checkTopPriceFilter()
            else -> viewModel.checkBottomPriceFilter()
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
            if(viewModel.nameFilter.value!!) {
                adapter.updateList(it.sortedBy { it.title }.toMutableList())
            }
            if(viewModel.topPriceFilter.value!!) {
                adapter.updateList(it.sortedByDescending { it.priceStandard }.toMutableList())
            }
            if(viewModel.bottomPriceFilter.value!!) {
                adapter.updateList(it.sortedBy { it.priceStandard }.toMutableList())
            }
        }

        viewModel.nameFilter.observe(viewLifecycleOwner) {
            if(it) {
                adapter.updateList(viewModel.books.value!!.sortedBy { it.title }.toMutableList())
            }
        }

        viewModel.topPriceFilter.observe(viewLifecycleOwner) {
            if(it) {
                adapter.updateList(viewModel.books.value!!.sortedByDescending { it.priceStandard }.toMutableList())
            }
        }

        viewModel.bottomPriceFilter.observe(viewLifecycleOwner) {
            if(it) {
                adapter.updateList(viewModel.books.value!!.sortedBy { it.priceStandard }.toMutableList())
            }
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

    /*
    바코드
     */

    private fun settingBarcode() {
        if(arguments?.getString("ISBN") != null) {
            // 검색한다.
            query = arguments?.getString("ISBN")!!
            viewModel.searchBook(query = query, 10, "Accuracy")
            loadingDialog()
            binding.editTextSearchSearch.text.append(query)
            binding.buttonSearchMore.isVisible = false
            binding.footerSearch.isVisible = false
        }
    }

}