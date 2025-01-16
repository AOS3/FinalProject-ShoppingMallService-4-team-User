package com.aladin.finalproject_shoppingmallservice_4_team.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentHomeBinding
import com.aladin.finalproject_shoppingmallservice_4_team.ui.bookdetail.BookDetailFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.booklist.BookListFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.mainMenu.MainMenuFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.search.SearchFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.shoppingcart.ShoppingCartFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceMainFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceSubFragment
import com.google.android.material.tabs.TabLayout


class HomeFragment : Fragment(), HomeOnClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val adapter: HomeAdapter by lazy { HomeAdapter(this) }
    private var list = MutableList(30) {
        "항목 $it"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingRecyclerView()
        combineButtonMethod()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /*
    리사이클러 뷰
     */

    private fun settingRecyclerView() {
        // 리사이클러 뷰 구성
        binding.recyclerViewHome.adapter = adapter
        adapter.updateList(list)
    }

    override fun itemClickListener(position: Int) {
        // 상세 화면으로 이동한다
        replaceMainFragment(BookDetailFragment(), true)
    }

    /*
    버튼
     */

    private fun combineButtonMethod() {
        // 개별 버튼
        settingSearchButton()
        settingMoreButton()
        // 탭 버튼
        tabLayoutButton()
        // 툴바 버튼
        toolbarButton()
    }

    private fun settingSearchButton() {
        binding.buttonHomeSearch.setOnClickListener {
            // 검색 화면으로 이동한다.
            replaceSubFragment(SearchFragment(), true)
        }
    }

    private fun settingMoreButton() {
        binding.buttonHomeMore.setOnClickListener {
            // 추천 도서 목록으로 이동한다.
            replaceSubFragment(BookListFragment(), true)
        }
    }


    /*
    툴바
     */

    private fun toolbarButton() {
        binding.materialToolbarHome.setOnMenuItemClickListener {
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

    /*
    탭
     */

    private fun tabLayoutButton() {
        binding.tabLayoutHome.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.position) {
                    // 신작
                    1 -> {
                        replaceSubFragment(BookListFragment(), true)
                    }
                    // 베스트셀러
                    2 -> {
                        replaceSubFragment(BookListFragment(), true)
                    }
                    // 국내
                    3 -> {
                        replaceSubFragment(BookListFragment(), true)
                    }
                    // 해외
                    4 -> {
                        replaceSubFragment(BookListFragment(), true)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                when(tab?.position) {
                    // 중고
                    0 -> {
                        replaceSubFragment(BookListFragment(), true)
                    }
                }
            }

        })
    }


}