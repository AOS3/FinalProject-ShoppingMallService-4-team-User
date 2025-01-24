package com.aladin.finalproject_shoppingmallservice_4_team.ui.orderinquiry

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.aladin.apiTestApplication.dto.RecommendBookItem
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentOrderInquiryBinding
import com.aladin.finalproject_shoppingmallservice_4_team.ui.home.HomeOnClickListener
import com.aladin.finalproject_shoppingmallservice_4_team.ui.mainMenu.MainMenuFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.shoppingcart.ShoppingCartFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceMainFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceSubFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderInquiryFragment : Fragment(),OrderOnClickListener {

    private val list = MutableList(30) {
        "항목 $it"
    }
    private var _binding: FragmentOrderInquiryBinding? = null
    private val binding get() = _binding!!
    private val adapter: OrderInquiryAdapter by lazy { OrderInquiryAdapter(this) }

    private val viewModel: OrderInquiryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentOrderInquiryBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        combineButtonMethod()
        settingRecyclerView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /*
    리사이클러 뷰
     */

    private fun settingRecyclerView() {
        binding.recyclerViewOrderInquiry.adapter = adapter
        adapter.updateItemList(list)
    }

    override fun itemClickListener(item: String) {
        // 상세 화면으로 변경한다.
        replaceSubFragment(OrderInquiryDetailFragment(), true)
    }

    /*
    버튼
     */

    private fun combineButtonMethod() {
        // 개별 버튼
        settingAskButton()
        // 툴바 버튼
        toolbarBackButton()
        toolbarMenuButton()
    }

    private fun settingAskButton() {
        binding.buttonOrderInquiryAsk.setOnClickListener {
            // 문의 화면으로 변경한다.
        }
    }

    /*
    툴바
     */

    private fun toolbarBackButton() = binding.materialToolbarOrderInquiry.setNavigationOnClickListener { removeFragment() }

    private fun toolbarMenuButton() {
        binding.materialToolbarOrderInquiry.setOnMenuItemClickListener {
            when(it.itemId) {
                // 메뉴
                R.id.home_menu_menuitem -> {
                    replaceSubFragment(MainMenuFragment(), true)
                }
                // 알림
                R.id.home_menu_notification -> {

                }
                // 장바구니
                R.id.home_menu_shoppingcart -> {
                    replaceMainFragment(ShoppingCartFragment(), true)
                }
            }
            false
        }
    }




}