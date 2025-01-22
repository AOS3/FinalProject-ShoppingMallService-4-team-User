package com.aladin.finalproject_shoppingmallservice_4_team.ui.bookdetail

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentBookDetailBinding
import com.aladin.finalproject_shoppingmallservice_4_team.model.SellingCartModel
import com.aladin.finalproject_shoppingmallservice_4_team.ui.custom.CustomDialog
import com.aladin.finalproject_shoppingmallservice_4_team.ui.custom.CustomDialogProgressbar
import com.aladin.finalproject_shoppingmallservice_4_team.ui.mainMenu.MainMenuFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.search.SearchFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.sellingcart.SellingCartFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.shoppingcart.ShoppingCartFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.loadImage
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceMainFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceSubFragment
import com.google.firebase.firestore.Query
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookDetailFragment : Fragment() {

    private var _binding: FragmentBookDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BookDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBookDetailBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadData()
        loadBookInfo()
        textViewSetting()
        combineButtonMethod()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    /*
    텍스트 뷰
     */
    private fun textViewSetting() {
        binding.textViewBookDetailBookPrice.paintFlags = binding.textViewBookDetailBookPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    }

    /*
    다이얼로그
     */

    private fun sellingDialog() {
        val customDialog = CustomDialog(
            context = requireContext(),
            contentText = "팔기 장바구니에 등록되었습니다.",
            icon = R.drawable.check_circle_24px,
            positiveText = "장바구니로 이동",
            onPositiveClick = {
                replaceMainFragment(SellingCartFragment(), true)
            },
            negativeText = "계속 담기",
            onNegativeClick = {
                Toast.makeText(context, "계속 담기", Toast.LENGTH_SHORT).show()
            }
        )
        customDialog.showCustomDialog()
    }

    /*
    버튼
     */

    private fun combineButtonMethod() {
        // 개별 버튼
        settingAskButton()
        settingLinkButton()
        settingBuyButton()
        settingFABButton()
        settingSearchButton()
        settingSellButton()
        // 툴바 버튼
        toolbarMenuButton()
        toolbarBackButton()
    }

    private fun settingBuyButton() {
        binding.buttonBookDetailBuyUsedBook.setOnClickListener {
            val bottomSheetFragment = BookDetailBottomSheetFragment.newInstance(
                viewModel.books.value!!.first().isbn13,
                viewModel.books.value!!.first().priceStandard
            )
            bottomSheetFragment.show(parentFragmentManager, "BottomSheetFragment")
        }
    }

    private fun settingSellButton() {
        binding.buttonBookDetailSellBook.setOnClickListener {
            viewModel.sellingAddData()
            sellingDialog()
        }
    }

    private fun settingAskButton() {
        binding.buttonBookDetailAsk.setOnClickListener {
            // 문의 화면으로 변경한다.
        }
    }

    private fun settingFABButton() {
        binding.fabBookDetailUp.setOnClickListener {
            // 위로 올라가게 한다.
        }
    }

    private fun settingSearchButton() {
        binding.buttonBookDetailSearch.setOnClickListener {
            replaceMainFragment(SearchFragment(), true)
        }
    }

    private fun settingLikeButton() {

    }

    private fun settingLinkButton() {
        binding.buttonBookDetailBuyNewBook.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(viewModel.books.value!!.first().link))
            startActivity(intent)
        }
    }

    /*
    툴바
     */

    private fun toolbarMenuButton() {
        binding.materialToolbarBookDetail.setOnMenuItemClickListener {
            when(it.itemId) {
                // 메뉴
                R.id.home_menu_menuitem -> {
                    replaceMainFragment(MainMenuFragment(), true)
                }
                // 알림
                R.id.home_menu_notification -> {

                }
                // 장바구니
                R.id.home_menu_shoppingcart -> {
                    replaceMainFragment(ShoppingCartFragment(), true)
                }
            }
            true
        }
    }

    private fun toolbarBackButton() = binding.materialToolbarBookDetail.setNavigationOnClickListener { removeFragment() }

    /*
    데이터
     */

    private fun loadData() {
        val query = arguments?.getString("bookIsbn")!!
        viewModel.searchBook(query)
    }

    private fun loadBookInfo() {
        viewModel.books.observe(viewLifecycleOwner) {
            with(binding) {
                val book = it.first()
                textViewBookDetailBookName.text = book.title
                textViewBookDetailBookWriter.text = book.author
                textViewBookDetailBookPublisher.text = book.publisher
                textViewBookDetailBookPublisherDate.text = book.pubDate
                textViewBookDetailBookCategory.text = book.categoryName
                textViewBookDetailBookPrice.text = "정가 : ${book.priceStandard}원"
                textViewBookDetailUsedBookPrice.text = "판매가 : ${(book.priceStandard * 0.3).toInt()}원 ~ ${(book.priceStandard * 0.7).toInt()}원"
                textViewBookDetailBookIntroduction.text = book.description
                imageViewBookDetail.loadImage(book.cover)
            }
        }

        // 화면 입장 시 공지사항 로딩을 위한 Dialog
        val progressBarDialog = CustomDialogProgressbar(requireContext())
        progressBarDialog.show()

        viewModel.isLoadBookDetailList.observe(viewLifecycleOwner) {
            if (it) {
                progressBarDialog.dismiss()
            }
        }
    }
}