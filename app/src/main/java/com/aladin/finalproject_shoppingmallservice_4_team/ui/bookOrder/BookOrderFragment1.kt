package com.aladin.finalproject_shoppingmallservice_4_team.ui.bookOrder

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentBookOrder1Binding
import com.aladin.finalproject_shoppingmallservice_4_team.ui.mainMenu.MainMenuFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.notice.NoticeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceMainFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceSubFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.showSoftInput


class BookOrderFragment1 : Fragment() {

    private lateinit var fragmentBookOrder1Binding: FragmentBookOrder1Binding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBookOrder1Binding = FragmentBookOrder1Binding.inflate(layoutInflater,container,false)
        // 입장 시 이름 TextField Focus
        fragmentBookOrder1Binding.textFieldBookOrderName.editText?.showSoftInput()
        // 툴바 설정
        settingToolbar()
        // 구매하기 버튼 클릭
        onClickOrderButton()
        return fragmentBookOrder1Binding.root
    }

    private fun settingToolbar() {
        fragmentBookOrder1Binding.apply {
            materialToolbarBookOrder.setNavigationOnClickListener {
                removeFragment()
            }
            materialToolbarBookOrder.setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.home_menu_shoppingcart -> {
                        // 장바구니로 이동 예정
                        true
                    }
                    R.id.home_menu_notification -> {
                        replaceSubFragment(NoticeFragment(),true)
                        true
                    }
                    else -> {
                        replaceSubFragment(MainMenuFragment(),true)
                        true
                    }
                }
            }
        }
    }

    private fun onClickOrderButton() {
        fragmentBookOrder1Binding.apply {
            buttonBookOrderBuyBook.setOnClickListener {
                // 구매자 정보는 기존 유저 정보랑 다를수 있기 때문에 비어있는지만 검사
                // 이름 검사
                if(textFieldBookOrderName.editText?.text?.isEmpty()!!) {
                    textFieldBookOrderName.error = "이름을 다시 확인해주세요."
                    textFieldBookOrderName.editText?.showSoftInput()
                    return@setOnClickListener
                }else {
                    textFieldBookOrderName.error = null
                }

                // 전화번호 검사
                if(textFieldBookOrderPhoneNumber.editText?.text?.isEmpty()!!) {
                    textFieldBookOrderPhoneNumber.error = "전화번호를 다시 확인해주세요."
                    textFieldBookOrderPhoneNumber.editText?.showSoftInput()
                    return@setOnClickListener
                }else {
                    textFieldBookOrderPhoneNumber.error = null
                }

                // 우편번호
                if(textFieldBookOrderPostCode.editText?.text?.isEmpty()!!) {
                    textFieldBookOrderPostCode.error = "우편번호를 다시 확인해주세요."
                    textFieldBookOrderPostCode.editText?.showSoftInput()
                    return@setOnClickListener
                }else {
                    textFieldBookOrderPostCode.error = null
                }

                // 도로명 주소
                if(textFieldBookOrderRoadNameAddress.editText?.text?.isEmpty()!!) {
                    textFieldBookOrderRoadNameAddress.error = "도로명 주소를 다시 확인해주세요."
                    textFieldBookOrderRoadNameAddress.editText?.showSoftInput()
                    return@setOnClickListener
                }else {
                    textFieldBookOrderRoadNameAddress.error = null
                }

                // 상세 주소
                if(textFieldBookOrderDetailAddress.editText?.text?.isEmpty()!!) {
                    textFieldBookOrderDetailAddress.error = "상세 주소를 다시 확인해주세요."
                    textFieldBookOrderDetailAddress.editText?.showSoftInput()
                    return@setOnClickListener
                }else {
                    textFieldBookOrderDetailAddress.error = null
                }
                saveDataBase()
            }
        }

    }

    // 데이터 베이스 저장 메서드
    private fun saveDataBase() {
        Log.e("asd","데이터 베이스 저장!")
    }
}