package com.aladin.finalproject_shoppingmallservice_4_team.ui.booksellinginquiry

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentBarcodeScannerBinding
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentBookSellingInquiryBinding
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentOrderInquiryBinding
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentSellingCartBinding

class BookSellingInquiryFragment : Fragment() {

    private lateinit var fragmentBookSellingInquiryBinding: FragmentBookSellingInquiryBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentBookSellingInquiryBinding = FragmentBookSellingInquiryBinding.inflate(inflater, container, false)

        // Toolbar 설정 메서드 호출
        settingToolbar()

        return fragmentBookSellingInquiryBinding.root
    }

    // Toolbar를 설정 메서드
    private fun settingToolbar() {
        fragmentBookSellingInquiryBinding.apply {
            materialToolbarBookSellingInquiry.title = "판매 조회"
            // 네비게이션 아이콘을 설정하고 누를 경우 NavigationView가 나타나도록 한다.
            materialToolbarBookSellingInquiry.setNavigationIcon(R.drawable.arrow_back_ios_24px)
            materialToolbarBookSellingInquiry.setNavigationOnClickListener {
                // 전 화면으로 이동
            }

            // 툴바에 메뉴 아이템 동적으로 추가
            materialToolbarBookSellingInquiry.menu.add(Menu.NONE, 1, Menu.NONE, "품질 가이드")
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

            materialToolbarBookSellingInquiry.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    // toolbar_mainMenu_goSettings
                    1 -> {
                        // 품질 가이드로 이동
                        Toast.makeText(requireContext(), "품질 가이드로 이동", Toast.LENGTH_SHORT).show()
                    }
                }
                true
            }
        }
    }

    // 버튼 설정

    // RecyclerView 설정 메서드

    // RecyclerView 어댑터 설정
}