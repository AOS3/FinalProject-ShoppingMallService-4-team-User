package com.aladin.finalproject_shoppingmallservice_4_team.ui.barcodescanresult

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentBarcodeScanResultBinding
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentMainMenuBinding

class BarcodeScanResultFragment : Fragment() {

    private lateinit var fragmentBarcodeScanResultBinding: FragmentBarcodeScanResultBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentBarcodeScanResultBinding =
            FragmentBarcodeScanResultBinding.inflate(layoutInflater, container, false)

        // Toolbar를 구성하는 메서드를 호출한다.
        settingToolbar()
        // 버튼을 클릭 했을 때 메서드
        buttonBarcodeScanResultPurchaseOnClick()
//        // List를 구성하는 메서드를 호출한다.
//        setupMenuList()

        return fragmentBarcodeScanResultBinding.root
    }

    // Toolbar를 구성하는 메서드
    private fun settingToolbar() {
        fragmentBarcodeScanResultBinding.apply {
            toolbarBarcodeScanResult.title = "검색 결과"
            // 네비게이션 아이콘을 설정하고 누를 경우 NavigationView가 나타나도록 한다.
            toolbarBarcodeScanResult.setNavigationIcon(R.drawable.arrow_back_ios_24px)
            toolbarBarcodeScanResult.setNavigationOnClickListener {
                // 전 화면으로 이동
            }
        }
    }

    // 버튼을 클릭 했을 때 메서드
    private fun buttonBarcodeScanResultPurchaseOnClick() {
        fragmentBarcodeScanResultBinding.apply {
            buttonBarcodeScanResultPurchase.setOnClickListener{
                // 제품 상세 정보 화면으로 이동
                Toast.makeText(requireContext(), "제품 상세 정보 화면으로 이동", Toast.LENGTH_SHORT).show()
            }

            buttonBarcodeScanResultSelling.setOnClickListener{
                // 팔기 장바구니 화면으로 이동
                Toast.makeText(requireContext(), "팔기 장바구니 화면으로 이동", Toast.LENGTH_SHORT).show()
            }
        }
    }
}