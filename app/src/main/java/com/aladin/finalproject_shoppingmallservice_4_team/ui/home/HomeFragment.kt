package com.aladin.finalproject_shoppingmallservice_4_team.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.aladin.apiTestApplication.dto.RecommendBookItem
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentHomeBinding
import com.aladin.finalproject_shoppingmallservice_4_team.ui.adapter.HomeAdapter
import com.aladin.finalproject_shoppingmallservice_4_team.ui.adapter.HomeBannerAdapter
import com.aladin.finalproject_shoppingmallservice_4_team.ui.ask.AskFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.bookdetail.BookDetailFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.booklist.BookListFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.custom.CustomDialogProgressbar
import com.aladin.finalproject_shoppingmallservice_4_team.ui.mainMenu.MainMenuFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.notification.NotificationFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.search.SearchFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.shoppingcart.ShoppingCartFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceMainFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceSubFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(), HomeOnClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // ItemNewAll : 신간 전체 리스트
    // ItemNewSpecial : 주목할 만한 신간 리스트
    // Bestseller : 베스트셀러
    // BlogBest : 블로거 베스트셀러 (국내도서만 조회 가능)
    private val items = listOf(
        "ItemNewAll",
        "ItemNewSpecial",
        "Bestseller",
        "BlogBest"
    ).random()

    private val viewModel: HomeViewModel by viewModels()

    // 어뎁터
    private val adapter: HomeAdapter by lazy { HomeAdapter(this) }
    private val bannerAdapter: HomeBannerAdapter by lazy { HomeBannerAdapter() }

    // 임시 데이터
    private val listImage = MutableList(5) {
        R.drawable.main_logo
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
        loadBookData()
        observeProgressDialog()
        settingRecyclerView()
        settingBanner()
        combineButtonMethod()
        settingFooterButton()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /*
    화면 구성
     */

    // Dialog
    private fun observeProgressDialog() {
        // 화면 입장 시 공지사항 로딩을 위한 Dialog
        val progressBarDialog = CustomDialogProgressbar(requireContext())
        progressBarDialog.show()

        viewModel.isLoadBookList.observe(viewLifecycleOwner) {
            if (it) {
                progressBarDialog.dismiss()
            }
        }
    }

    /*
    리사이클러 뷰
     */

    private fun settingRecyclerView() {
        updateRecyclerView()
        // 리사이클러 뷰 구성
        binding.recyclerViewHome.adapter = adapter
    }

    private fun loadBookData() {
        viewModel.recommendBook(items)
    }

    private fun updateRecyclerView() {
        viewModel.books.observe(viewLifecycleOwner) {
            adapter.updateList(it.toMutableList())
        }
    }

    override fun itemClickListener(item: RecommendBookItem) {
        val dataBundle = Bundle()
        dataBundle.putString("bookIsbn", item.isbn13)
        // 상세 화면으로 이동한다
        replaceMainFragment(BookDetailFragment(), true, dataBundle = dataBundle)
    }

    /*
    배너
     */

    private fun settingBanner() {
        binding.apply {
            viewPagerHome.adapter = bannerAdapter
            bannerAdapter.updateItem(listImage)
            TabLayoutMediator(tabHomeIndicator, viewPagerHome) { tab: TabLayout.Tab, i: Int ->

            }.attach()
        }
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
            val dataBundle = Bundle()
            dataBundle.putString("bookQuery", items)
            dataBundle.putInt("moreValue", 1)
            replaceSubFragment(BookListFragment(), true, dataBundle = dataBundle)
        }
    }

    private fun settingFooterButton() {
        binding.buttonHomeAsk.setOnClickListener {
            replaceMainFragment(AskFragment(),true)
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
                    replaceMainFragment(NotificationFragment(), true)
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
                        val dataBundle = Bundle()
                            dataBundle.putString("bookQuery", "ItemNewAll")
                        replaceSubFragment(BookListFragment(), true, dataBundle = dataBundle)
                    }
                    // 베스트셀러
                    2 -> {
                        val dataBundle = Bundle()
                            dataBundle.putString("bookQuery", "Bestseller")
                        replaceSubFragment(BookListFragment(), true, dataBundle = dataBundle)
                    }
                    // 블로거추천
                    3 -> {
                        val dataBundle = Bundle()
                            dataBundle.putString("bookQuery", "BlogBest")
                        replaceSubFragment(BookListFragment(), true, dataBundle = dataBundle)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                when(tab?.position) {
                    // 중고
                    0 -> {
                        val dataBundle = Bundle()
                            dataBundle.putString("bookQuery", "Used")
                        replaceSubFragment(BookListFragment(), true, dataBundle = dataBundle)
                    }
                }
            }
        })
    }
}