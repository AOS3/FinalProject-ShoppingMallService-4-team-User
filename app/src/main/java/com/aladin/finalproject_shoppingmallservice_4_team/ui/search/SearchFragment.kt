package com.aladin.finalproject_shoppingmallservice_4_team.ui.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentSearchBinding
import com.aladin.finalproject_shoppingmallservice_4_team.ui.barcodescanner.BarcodeScannerFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.bookdetail.BookDetailFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.home.HomeAdapter
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
        toolbarButton()
        settingRecyclerView()
    }

    /*
    툴바
     */

    private fun toolbarButton() {
        toolbarBackButton()
        toolbarBarcodeButton()
    }

    private fun toolbarBackButton() = binding.buttonSearchBack.setOnClickListener { removeFragment() }

    private fun toolbarBarcodeButton() = binding.buttonSearchBarcode.setOnClickListener { replaceMainFragment(BarcodeScannerFragment(), true) }

    /*
    버튼
     */

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

    /*
    리사이클러 뷰
     */

    private fun settingEmptyResult() {
        // 검색 결과가 없을 경우
    }

    private fun settingRecyclerView() {
        binding.recyclerViewSearch.adapter = adapter
    }

    override fun itemClickListener(position: Int) {
        // 상세 화면으로 변경한다.
        replaceMainFragment(BookDetailFragment(), true)
    }
}