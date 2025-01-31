package com.aladin.finalproject_shoppingmallservice_4_team.ui.orderinquiry

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.aladin.apiTestApplication.dto.RecommendBookItem
import com.aladin.finalproject_shoppingmallservice_4_team.BookApplication
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentOrderInquiryBinding
import com.aladin.finalproject_shoppingmallservice_4_team.model.OrderInquiryModel
import com.aladin.finalproject_shoppingmallservice_4_team.ui.ask.AskFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.custom.CustomDialog
import com.aladin.finalproject_shoppingmallservice_4_team.ui.custom.CustomDialogProgressbar
import com.aladin.finalproject_shoppingmallservice_4_team.ui.home.HomeOnClickListener
import com.aladin.finalproject_shoppingmallservice_4_team.ui.mainMenu.MainMenuFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.notification.NotificationFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.shoppingcart.ShoppingCartFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceMainFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceSubFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderInquiryFragment : Fragment(),OrderOnClickListener {


    private var _binding: FragmentOrderInquiryBinding? = null
    private val binding get() = _binding!!
    private val adapter: OrderInquiryAdapter by lazy { OrderInquiryAdapter(this) }

    private lateinit var bookApplication: BookApplication

    private val viewModel: OrderInquiryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentOrderInquiryBinding.inflate(inflater, container, false)
        bookApplication = requireActivity().application as BookApplication
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        combineButtonMethod()
        checkLoginProcess()
        settingRecyclerView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /*
    로그인
     */

    // Dialog
    private fun observeProgressDialog() {
        // 화면 입장 시 공지사항 로딩을 위한 Dialog
        val progressBarDialog = CustomDialogProgressbar(requireContext())
        progressBarDialog.show()

        viewModel.isLoadInquiryDataList.observe(viewLifecycleOwner) {
            if (it) {
                progressBarDialog.dismiss()
            }
        }
    }

    // Check Login
    private fun checkLoginProcess() {
        try {
            if (::bookApplication.isInitialized && bookApplication.loginUserModel != null) {
                observeProgressDialog()
                viewModel.loadData(bookApplication.loginUserModel.userToken)
            }
        } catch (e: Exception) {
            val loginDialog = CustomDialog(
                requireContext(),
                // 리스트 삭제 진행
                onPositiveClick = {
                    removeFragment()
                },
                contentText = "로그인을 먼저 진행해주세요.",
                icon = R.drawable.error_24px
            )
            loginDialog.showCustomDialog()
        }
    }

    /*
    리사이클러 뷰
     */

    private fun settingRecyclerView() {
        binding.recyclerViewOrderInquiry.adapter = adapter

        viewModel.inquiryList.observe(viewLifecycleOwner) {
            if(it.isNotEmpty()) {
                binding.scrollViewOrderInquiry.isVisible = true
                binding.textViewOrderInquiryEmpty.isVisible = false
                adapter.updateItemList(it)
            }
            else {
                binding.scrollViewOrderInquiry.isVisible = false
                binding.textViewOrderInquiryEmpty.isVisible = true
            }
        }
    }

    override fun itemClickListener(item: OrderInquiryModel) {
        // 상세 화면으로 변경한다.
        val dataBundle = Bundle()
        dataBundle.putString("userName", item.orderInquiryName)
        dataBundle.putString("userPhone", item.orderInquiryPhoneNumber)
        dataBundle.putString("userAddress", item.orderInquiryAddress)
        dataBundle.putString("bookName", item.orderInquiryTitle)
        dataBundle.putString("bookWriter", item.orderInquiryAuthor)
        dataBundle.putInt("bookQuality", item.orderInquiryQuality)
        dataBundle.putLong("buyTime", item.orderInquiryTime)
        dataBundle.putInt("bookTotal", item.orderInquiryTotal)
        dataBundle.putInt("bookDelivery", item.orderInquiryDeliveryResult)
        dataBundle.putInt("bookPrice", item.orderInquiryPrice)
        dataBundle.putString("bookNumber", item.orderInquiryNumber)
        replaceMainFragment(OrderInquiryDetailFragment(), true, dataBundle = dataBundle)
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
            replaceMainFragment(AskFragment(), true)
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
                    replaceMainFragment(MainMenuFragment(), true)
                }
                // 알림
                R.id.home_menu_notification -> {
                    replaceMainFragment(NotificationFragment(), true)
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