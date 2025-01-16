package com.aladin.finalproject_shoppingmallservice_4_team.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentHomeBinding
import com.aladin.finalproject_shoppingmallservice_4_team.ui.bookdetail.BookDetailFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.mainMenu.MainMenuFragment
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
        toolbarButton()
        tabLayoutButton()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /*
    리사이클러 뷰
     */
    private fun settingRecyclerView() {
        binding.recyclerViewHome.adapter = adapter
        adapter.updateList(list)
    }


    /*
    툴바 버튼 리스너
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

                }
            }
            true
        }
    }

    /*
    탭 카테고리 관리
     */

    private fun tabLayoutButton() {
        binding.tabLayoutHome.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.position) {
                    // 중고
                    0 -> {
                        replaceMainFragment(HomeFragment(), false)
                    }
                    // 신작
                    1 -> {
                        replaceMainFragment(HomeFragment(), false)
                    }
                    // 베스트셀러
                    2 -> {
                        replaceMainFragment(HomeFragment(), false)
                    }
                    // 국내
                    3 -> {
                        replaceMainFragment(HomeFragment(), false)
                    }
                    // 해외
                    4 -> {
                        replaceMainFragment(HomeFragment(), false)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })
    }

    override fun itemClickListener(position: Int) {
        replaceMainFragment(BookDetailFragment(), true)
    }
}