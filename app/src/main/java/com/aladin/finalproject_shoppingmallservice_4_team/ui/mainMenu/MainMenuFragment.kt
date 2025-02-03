package com.aladin.finalproject_shoppingmallservice_4_team.ui.mainMenu

import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import android.system.Os.remove
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
import androidx.core.content.edit
import androidx.fragment.app.viewModels
import com.aladin.finalproject_shoppingmallservice_4_team.BookApplication
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentMainMenuBinding
import com.aladin.finalproject_shoppingmallservice_4_team.ui.booklist.BookListFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.likeList.LikeListFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.login.LoginFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.login.SharedPreferencesHelper
import com.aladin.finalproject_shoppingmallservice_4_team.ui.myinfo.MyInfoFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.orderinquiry.OrderInquiryFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.sellingcart.SellingCartFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.setting.SettingFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.clearAllBackStack
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceMainFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainMenuFragment : Fragment() {

    val menuItems = listOf(
        "내가 판매하는 중고 도서",
        "중고 도서",
        "신간 도서",
        "베스트 셀러",
        "블로거 추천",
    )

    private lateinit var fragmentMainMenuBinding: FragmentMainMenuBinding
    private val mainMenuViewModel: MainMenuViewModel by viewModels()
    private lateinit var bookApplication: BookApplication

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentMainMenuBinding = FragmentMainMenuBinding.inflate(inflater, container, false)

        // BookApplication 초기화
        bookApplication = requireActivity().application as BookApplication

        // 관찰 메서드 호출
        observeViewModel()

        // 로그인 여부 확인 메서드 호출
        checkLoginProcess()

        // Tab 버튼 및 메뉴 설정 메서드 호출
        setupTabButtons()
        setupSettingIcon()

        // ListView 설정 메서드 호출
        setupMenuList()

        return fragmentMainMenuBinding.root
    }

    // 관찰 메서드
    private fun observeViewModel() {
        // ViewModel의 userName을 관찰하여 툴바 제목 업데이트
        mainMenuViewModel.userName.observe(viewLifecycleOwner) { userName ->
            setupToolbar(userName)
        }

        // 로그인 상태 변경에 따라 로그인/로그아웃 UI 업데이트
        mainMenuViewModel.isLoggedIn.observe(viewLifecycleOwner) { isLoggedIn ->
            if (isLoggedIn) {
                setupLogoutButton()
            } else {
                setupLoginButton()
            }
        }
    }

    // 로그인 여부 확인 메서드
    private fun checkLoginProcess() {
        try {
            if (::bookApplication.isInitialized && bookApplication.loginUserModel.userToken.isNotEmpty()) {
                setupToolbar(bookApplication.loginUserModel.userName)
                setupLogoutButton()
            } else {
                setupToolbar(null)
                setupLoginButton()
            }
        } catch (e: Exception) {
            setupToolbar(null)
            setupLoginButton()
        }
    }

    // 툴바 제목 설정
    private fun setupToolbar(userName: String?) {
        fragmentMainMenuBinding.toolbarMainMenu.title = userName?.let {
            "${it}님"
        }
    }

    // 메뉴 설정 메서드
    private fun setupSettingIcon() {
        val icon = ContextCompat.getDrawable(requireContext(), R.drawable.settings_24px)
        icon?.setTint(ContextCompat.getColor(requireContext(), R.color.white))
        fragmentMainMenuBinding.toolbarMainMenu.menu.add(Menu.NONE, 1, Menu.NONE, "설정")
            .setIcon(R.drawable.settings_24px)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

        fragmentMainMenuBinding.toolbarMainMenu.setOnMenuItemClickListener { item ->
            if (item.itemId == 1) {
                replaceMainFragment(SettingFragment(), true)
            }
            true
        }
    }

    // 로그인 버튼 UI 설정
    private fun setupLoginButton() {
        val titleTextView = TextView(requireContext()).apply {
            text = "로그인 해 주세요"
            textSize = 20f
            setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
            setOnClickListener {
                removeFragment()
                replaceMainFragment(LoginFragment(), true)
            }
        }

        fragmentMainMenuBinding.toolbarMainMenu.apply {
            // 기존 메뉴를 유지하면서 TextView를 추가
            addView(titleTextView)
        }

        fragmentMainMenuBinding.textMainMenuLogOut.visibility = View.GONE
    }

    // 로그아웃 버튼 UI 설정
    private fun setupLogoutButton() {
        fragmentMainMenuBinding.textMainMenuLogOut.apply {
            visibility = View.VISIBLE
            paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
            setOnClickListener {
                handleLogout()
            }
        }
    }

    // 로그아웃
    private fun handleLogout() {
        val sharedPreferencesHelper = SharedPreferencesHelper(requireContext())
        // 자동 로그인 토큰 제거
        sharedPreferencesHelper.clearAutoLoginToken()

        val bookApplication = requireContext().applicationContext as BookApplication

        // val bookApplication = context as BookApplication
        // 사용자 로그인 상태 초기화 (null 이 안되어서 직접 초기화)
        // bookApplication.loginUserModel = UserModel()

        bookApplication.apply {
            // loginUserModel을 null로 설정하여 완전히 제거
            this::class.java.getDeclaredField("loginUserModel").apply {
                isAccessible = true
                set(bookApplication, null)
            }
        }

        val pref = bookApplication.getSharedPreferences("UserToken", Context.MODE_PRIVATE)
        pref.edit{
            remove("userToken")
            apply()
        }

        // 메인 화면으로 이동
        clearAllBackStack()

        // 알림
        Toast.makeText(requireContext(), "로그아웃 되었습니다", Toast.LENGTH_SHORT).show()
    }


    // Tab 버튼 설정 메서드
    private fun setupTabButtons() {
        fragmentMainMenuBinding.apply {
            buttonMainMenuMyInfo.setOnClickListener {
                // 내 정보로 이동
                replaceMainFragment(MyInfoFragment(),true)
            }
        }

        fragmentMainMenuBinding.apply {
            buttonMainMenuOrderHistory.setOnClickListener {
                // 주문 조회로 이동
                replaceMainFragment(OrderInquiryFragment(),true)
            }
        }

        fragmentMainMenuBinding.apply {
            buttonMainMenuWishlist.setOnClickListener {
                // 찜 목록으로 이동
                replaceMainFragment(LikeListFragment(),true)
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
                    replaceMainFragment(SellingCartFragment(),true)
                }
                1 -> {
                    // 중고 도서로 이동
                    val dataBundle = Bundle()
                    dataBundle.putString("bookQuery", "Used")
                    replaceMainFragment(BookListFragment(),true,dataBundle = dataBundle)
                }
                2 -> {
                    // 신간 도서로 이동
                    val dataBundle = Bundle()
                    dataBundle.putString("bookQuery", "ItemNewAll")
                    replaceMainFragment(BookListFragment(),true,dataBundle = dataBundle)
                }
                3 -> {
                    // 베스트 셀러로 이동
                    val dataBundle = Bundle()
                    dataBundle.putString("bookQuery", "Bestseller")
                    replaceMainFragment(BookListFragment(),true,dataBundle = dataBundle)
                }
                4 -> {
                    // 블로거 추천으로 이동
                    val dataBundle = Bundle()
                    dataBundle.putString("bookQuery", "BlogBest")
                    replaceMainFragment(BookListFragment(),true,dataBundle = dataBundle)
                }
                else -> null
            }
        }
    }
}