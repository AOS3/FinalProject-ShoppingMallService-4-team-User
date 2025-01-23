package com.aladin.finalproject_shoppingmallservice_4_team.ui.bookdetail

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.aladin.finalproject_shoppingmallservice_4_team.BookApplication
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentBookDetailBottomSheetBinding
import com.aladin.finalproject_shoppingmallservice_4_team.ui.custom.CustomDialog
import com.aladin.finalproject_shoppingmallservice_4_team.ui.custom.CustomDialogProgressbar
import com.aladin.finalproject_shoppingmallservice_4_team.ui.shoppingcart.ShoppingCartFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceMainFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.toCommaString
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BookDetailBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentBookDetailBottomSheetBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetailBottomViewModel by viewModels()

    private lateinit var bookApplication: BookApplication


    private var highPrice = 0
    private var normalPrice = 0
    private var lowPrice = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBookDetailBottomSheetBinding.inflate(inflater, container, false)
        bookApplication = requireActivity().application as BookApplication

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadData()
        settingCountButton()
        settingAddShoppingCart()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    /*
    버튼
     */

    private fun settingAddShoppingCart() {
        binding.buttonBookDetailAdd.setOnClickListener {
            checkLoginProcess()
        }
    }

    private fun settingCountButton() {
        binding.apply {
            buttonSheetPlusHigh.setOnClickListener {
                viewModel.updateHighStock(true)
            }
            buttonSheetMinusHigh.setOnClickListener {
                viewModel.updateHighStock(false)
            }
            buttonSheetPlusNormal.setOnClickListener {
                viewModel.updateNormalStock(true)
            }
            buttonSheetMinusNormal.setOnClickListener {
                viewModel.updateNormalStock(false)
            }
            buttonSheetPlusLow.setOnClickListener {
                viewModel.updateLowStock(true)
            }
            buttonSheetMinusLow.setOnClickListener {
                viewModel.updateLowStock(false)
            }
            viewModel.highStock.observe(viewLifecycleOwner) {
                textViewSheetNumberHigh.text = it.toString()
                viewModel.updateHighPriceTotal(it * highPrice)
                viewModel.updateTotal()
            }
            viewModel.normalStock.observe(viewLifecycleOwner) {
                textViewSheetNumberNormal.text = it.toString()
                viewModel.updateNormalPriceTotal(it * normalPrice)
                viewModel.updateTotal()
            }
            viewModel.lowStock.observe(viewLifecycleOwner) {
                textViewSheetNumberLow.text = it.toString()
                viewModel.updateLowPriceTotal(it * lowPrice)
                viewModel.updateTotal()
            }
            viewModel.total.observe(viewLifecycleOwner) {
                textViewSheetTotalPrice.text = "전체 구매 가격 : ${it.toCommaString()}원"
            }
        }

    }

    /*
    데이터
     */

    private fun checkSelectBook() {
        if(viewModel.total.value != 0) {
            viewModel.updateShoppingCartData(
                arguments?.getString("bookIsbn")!!,
                bookApplication.loginUserModel.userToken
            )
            if(viewModel.isAddShoppingCart.value == true) {
                val successDialog = CustomDialog(
                    requireContext(),
                    onPositiveClick = {
                        replaceMainFragment(ShoppingCartFragment(), true)
                        dismiss()
                    },
                    onNegativeClick = {
                        dismiss()
                    },
                    contentText = "장바구니에 추가되었습니다",
                    icon = R.drawable.check_circle_24px,
                )
                successDialog.showCustomDialog()
            }
        }
        else {
            val failureDialog = CustomDialog(
                requireContext(),
                onPositiveClick = {

                },
                contentText = """
                    추가된 상품이 없습니다.
                    상품을 추가해주세요
                """.trimIndent(),
                icon = R.drawable.sentiment_sad_24px
            )
            failureDialog.showCustomDialog()
        }
    }

    // Check Login
    private fun checkLoginProcess() {
        try {
            if (::bookApplication.isInitialized && bookApplication.loginUserModel != null ) {
                checkSelectBook()
            }
        } catch (e: Exception) {
            val loginDialog = CustomDialog(
                requireContext(),
                // 리스트 삭제 진행
                onPositiveClick = {
                    removeFragment()
                    dismiss()
                },
                contentText = "로그인을 먼저 진행해주세요.",
                icon = R.drawable.error_24px
            )
            loginDialog.showCustomDialog()
        }
    }

    private fun loadData() {
        binding.apply {
            viewModel.loadBookCount(arguments?.getString("bookIsbn")!!)
            val progressBarDialog = CustomDialogProgressbar(requireContext())
            progressBarDialog.show()

            viewModel.isLoadBookCountList.observe(viewLifecycleOwner) {
                if (it) {
                    progressBarDialog.dismiss()
                }
            }
            viewModel.highCount.observe(viewLifecycleOwner) {
                if (it != 0) {
                    textViewSheetHighQualityStock.text = "재고 : ${it}권"
                } else {
                    textViewSheetHighQualityBook.setTextColor(Color.LTGRAY)
                    textViewSheetHighQualityStock.setTextColor(Color.LTGRAY)
                    textViewSheetHighQualityPrice.setTextColor(Color.LTGRAY)
                    cardViewBookDetailHigh.isVisible = false
                }
            }
            viewModel.normalCount.observe(viewLifecycleOwner) {
                if (it != 0) {
                    textViewSheetNormalQualityStock.text = "재고 : ${it}권"
                } else {
                    textViewSheetNormalQualityBook.setTextColor(Color.LTGRAY)
                    textViewSheetNormalQualityStock.setTextColor(Color.LTGRAY)
                    textViewSheetNormalQualityPrice.setTextColor(Color.LTGRAY)
                    cardViewBookDetailNormal.isVisible = false
                }
            }
            viewModel.lowCount.observe(viewLifecycleOwner) {
                if (it != 0) {
                    textViewSheetLowQualityStock.text = "재고 : ${it}권"
                } else {
                    textViewSheetLowQualityBook.setTextColor(Color.LTGRAY)
                    textViewSheetLowQualityStock.setTextColor(Color.LTGRAY)
                    textViewSheetLowQualityPrice.setTextColor(Color.LTGRAY)
                    cardViewBookDetailLow.isVisible = false
                }
            }

            val price = arguments?.getInt("bookPrice")!!
            highPrice = (price * 0.7).toInt()
            normalPrice = (price * 0.5).toInt()
            lowPrice = (price * 0.3).toInt()

            textViewSheetHighQualityPrice.text = "판매가 : ${highPrice.toCommaString()}원"
            textViewSheetNormalQualityPrice.text = "판매가 : ${normalPrice.toCommaString()}원"
            textViewSheetLowQualityPrice.text = "판매가 : ${lowPrice.toCommaString()}원"
        }
    }

        companion object {
            fun newInstance(isbn: String, price: Int): BookDetailBottomSheetFragment {
                val bundle = Bundle().apply {
                    putString("bookIsbn", isbn)
                    putInt("bookPrice", price)
                }
                return BookDetailBottomSheetFragment().apply {
                    arguments = bundle
                }
            }
        }
    }

