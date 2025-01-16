package com.aladin.finalproject_shoppingmallservice_4_team.ui.sellingsearch

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentSellingCartBinding
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentSellingSearchBinding

class SellingSearchFragment : Fragment() {

    private lateinit var fragmentSellingSearchBinding: FragmentSellingSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentSellingSearchBinding = FragmentSellingSearchBinding.inflate(layoutInflater, container, false)

        // toolbar 설정 메서드 호츌
        settingToolbar()

        // RecyclerView를 구성하는 메서드 호출
        // settingRecyclerView()

        return fragmentSellingSearchBinding.root
    }

    // Toolbar를 구성하는 메서드
    private fun settingToolbar() {
        fragmentSellingSearchBinding.apply {
            materialToolbarSellingSearch.title = "판매할 도서 검색"
            // 네비게이션 아이콘을 설정하고 누를 경우 NavigationView가 나타나도록 한다.
            materialToolbarSellingSearch.setNavigationIcon(R.drawable.arrow_back_ios_24px)
            materialToolbarSellingSearch.setNavigationOnClickListener {
                // 전 화면으로 이동
            }

            // 툴바에 메뉴 아이템 동적으로 추가
            materialToolbarSellingSearch.menu.add(Menu.NONE, 1, Menu.NONE, "판매 조회")
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

            materialToolbarSellingSearch.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    // toolbar_mainMenu_goSettings
                    1 -> {
                        // 판매 조회로 이동
                        Toast.makeText(requireContext(), "판매 조회 이동", Toast.LENGTH_SHORT).show()
                    }
                }
                true
            }
        }
    }

    // 버튼을 클릭 했을 때 메서드
    private fun buttonSellingSearchOnClick() {

    }

    // RecyclerView를 구성하는 메서드
    private fun settingRecyclerView() {

    }

    // RecyclerView의 어뎁터
}