package com.aladin.finalproject_shoppingmallservice_4_team.ui.bookdetail

import android.graphics.Paint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentBookDetailBinding
import com.aladin.finalproject_shoppingmallservice_4_team.ui.mainMenu.MainMenuFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.shoppingcart.ShoppingCartFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceMainFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceSubFragment


class BookDetailFragment : Fragment() {

    private lateinit var binding: FragmentBookDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBookDetailBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textViewSetting()
        combineButtonMethod()
    }

    /*
    텍스트 뷰
     */
    private fun textViewSetting() {
        binding.textViewBookDetailBookPrice.paintFlags = binding.textViewBookDetailBookPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    }

    /*
    버튼
     */

    private fun combineButtonMethod() {
        // 개별 버튼
        settingAskButton()
        settingBuyButton()
        settingFABButton()
        // 툴바 버튼
        toolbarButton()
    }

    private fun settingBuyButton() {
        binding.buttonBookDetailBuyUsedBook.setOnClickListener {
            val bottomSheetFragment = BookDetailBottomSheetFragment()
            bottomSheetFragment.show(parentFragmentManager, "BottomSheetFragment")
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

    /*
    툴바
     */

    private fun toolbarButton() {
        binding.materialToolbarBookDetail.setOnMenuItemClickListener {
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
            true
        }
    }

}