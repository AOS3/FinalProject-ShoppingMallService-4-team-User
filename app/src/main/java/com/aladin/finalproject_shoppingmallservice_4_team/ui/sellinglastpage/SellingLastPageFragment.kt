package com.aladin.finalproject_shoppingmallservice_4_team.ui.sellinglastpage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentSellingCartBinding
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentSellingLastPageBinding

class SellingLastPageFragment : Fragment() {

    private lateinit var fragmentSellingLastPageBinding: FragmentSellingLastPageBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentSellingLastPageBinding = FragmentSellingLastPageBinding.inflate(layoutInflater, container, false)

        // toolbar 설정 메서드 호츌
        settingToolbar()

        // 은행 선택 드롭다운 설정
        setupBankDropdown()

        return fragmentSellingLastPageBinding.root
    }

    // toolbar 설정 메서드 호츌
    private fun settingToolbar() {
        fragmentSellingLastPageBinding.apply {
            materialToolbarSellingLastPage.title = "중고 서적 팔기"
            // 네비게이션 아이콘을 설정하고 누를 경우 NavigationView가 나타나도록 한다.
            materialToolbarSellingLastPage.setNavigationIcon(R.drawable.arrow_back_ios_24px)
            materialToolbarSellingLastPage.setNavigationOnClickListener {
                // 전 화면으로 이동

            }
        }
    }

    // 은행 선택 드롭다운 설정 메서드
    private fun setupBankDropdown() {
        val bankOptions = listOf("선택하세요","KB국민은행", "신한은행", "우리은행", "하나은행", "농협은행")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, bankOptions)

        // AutoCompleteTextView와 연결
        fragmentSellingLastPageBinding.autoCompleteTextViewSellingLastPageBank.setAdapter(adapter)
    }
}