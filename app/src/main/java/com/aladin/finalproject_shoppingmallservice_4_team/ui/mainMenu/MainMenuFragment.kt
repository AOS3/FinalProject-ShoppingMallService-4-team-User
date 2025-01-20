package com.aladin.finalproject_shoppingmallservice_4_team.ui.mainMenu

import android.graphics.Paint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentMainMenuBinding
import com.aladin.finalproject_shoppingmallservice_4_team.ui.booklist.BookListFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.home.HomeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.likeList.LikeListFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.login.LoginFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.myinfo.MyInfoFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.orderinquiry.OrderInquiryFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.sellingcart.SellingCartFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.setting.SettingFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceMainFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceSubFragment

class MainMenuFragment : Fragment() {

    val menuItems = listOf(
        "내가 판매하는 중고 도서",
        "중고 도서",
        "신간 도서",
        "베스트 셀러",
        "국내 도서",
        "해외 도서"
    )

    private lateinit var fragmentMainMenuBinding: FragmentMainMenuBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentMainMenuBinding = FragmentMainMenuBinding.inflate(layoutInflater, container, false)

        // Toolbar를 구성하는 메서드를 호출한다.
        settingToolbar()
        // Tab 버튼들을 구성하는 메서드를 호출한다.
        setupTabButtons()
        // List를 구성하는 메서드를 호출한다.
        setupMenuList()

        return fragmentMainMenuBinding.root
    }

    // Toolbar를 구성하는 메서드
    fun settingToolbar() {
        fragmentMainMenuBinding.apply {
            val titleTextView = TextView(requireContext()).apply {
                text = "로그인 해 주세요"
                textSize = 20f
                setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
                // 로그인 해 주세요 클릭
                setOnClickListener {
                    // 로그인 화면으로 이동
                    replaceMainFragment(LoginFragment(),true)
                    visibility = View.GONE
                    toolbarMainMenu.title = "000 사용자님"

                    val textMainMenuLogout = fragmentMainMenuBinding.textMainMenuLogOut
                    textMainMenuLogout.visibility = View.VISIBLE
                    textMainMenuLogout.paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG

                    // 로그아웃 버튼 클릭
                    textMainMenuLogout.setOnClickListener {
                        visibility = View.VISIBLE
                        toolbarMainMenu.title = ""
                        textMainMenuLogout.visibility = View.GONE
                        // 로그 아웃
                        Toast.makeText(requireContext(), "로그 아웃 성공~!", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            // 툴바에 동적으로 추가
            toolbarMainMenu.addView(titleTextView)

            // 아이콘을 Drawable로 가져와서 Tint 적용
            val icon = ContextCompat.getDrawable(requireContext(), R.drawable.settings_24px)
            icon?.setTint(ContextCompat.getColor(requireContext(), R.color.white))

            // 툴바에 메뉴 아이템 동적으로 추가
            toolbarMainMenu.menu.add(Menu.NONE, 1, Menu.NONE, "설정")
                .setIcon(R.drawable.settings_24px)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

            toolbarMainMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    // toolbar_mainMenu_goSettings
                    1 -> {
                        // 환경 설정으로 이동
                        replaceSubFragment(SettingFragment(),true)
                    }
                }
                true
            }
        }
    }

    // Tab 버튼 설정 메서드
    private fun setupTabButtons() {
        fragmentMainMenuBinding.apply {
            buttonMainMenuMyInfo.setOnClickListener {
                // 내 정보로 이동
                replaceSubFragment(MyInfoFragment(),true)
            }
        }

        fragmentMainMenuBinding.apply {
            buttonMainMenuOrderHistory.setOnClickListener {
                // 주문 조회로 이동
                replaceSubFragment(OrderInquiryFragment(),true)
            }
        }

        fragmentMainMenuBinding.apply {
            buttonMainMenuWishlist.setOnClickListener {
                // 찜 목록으로 이동
                replaceSubFragment(LikeListFragment(),true)
            }
        }
    }

    // ListView 설정 메서드
    private fun setupMenuList() {
        val adapter = object : ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_list_item_1,
            menuItems
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent) as TextView

                // 아이콘 추가
                view.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.chevron_right_24px,
                    0
                )
                // 텍스트와 아이콘 간 간격 설정
                view.compoundDrawablePadding = 16

                return view
            }
        }

        fragmentMainMenuBinding.listViewMainMenuBookLsit.adapter = adapter

        fragmentMainMenuBinding.listViewMainMenuBookLsit.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = menuItems[position]

            // Fragment 이동 처리
            val targetFragment = when (position) {
                0 -> {
                    // 내가 판매하는 중고 도서로 이동
                    replaceSubFragment(SellingCartFragment(),true)
                }
                1 -> {
                    // 중고 도서로 이동
                    replaceSubFragment(BookListFragment(),true)
                }
                2 -> {
                    // 신간 도서로 이동
                    replaceSubFragment(BookListFragment(),true)
                }
                3 -> {
                    // 베스트 셀러로 이동
                    replaceSubFragment(BookListFragment(),true)
                }
                4 -> {
                    // 국내 도서로 이동
                    replaceSubFragment(BookListFragment(),true)
                }
                5 -> {
                    // 해외 도서로 이동
                    replaceSubFragment(BookListFragment(),true)
                }
                else -> null
            }
        }
    }
}