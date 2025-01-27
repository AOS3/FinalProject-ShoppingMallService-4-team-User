package com.aladin.finalproject_shoppingmallservice_4_team.ui.booksellinginquiry

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentBookSellingInquiryBinding
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentBookSellingInquirydetailBinding
import com.aladin.finalproject_shoppingmallservice_4_team.model.SellingInquiryModel
import com.aladin.finalproject_shoppingmallservice_4_team.ui.qualityguide.QualityGuideFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceMainFragment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BookSellingInquirydetailFragment : Fragment() {

    private lateinit var fragmentBookSellingInquirydetailBinding: FragmentBookSellingInquirydetailBinding
    private var selectedItem: SellingInquiryModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentBookSellingInquirydetailBinding = FragmentBookSellingInquirydetailBinding.inflate(inflater, container, false)

        // Toolbar 설정 메서드 호출
        settingToolbar()

        detailSetting()

        return fragmentBookSellingInquirydetailBinding.root
    }

    // Toolbar를 설정 메서드
    private fun settingToolbar() {
        fragmentBookSellingInquirydetailBinding.apply {
            materialToolbarBookSellingInquiryDetail.title = "팔기 상세내역"
            materialToolbarBookSellingInquiryDetail.setNavigationIcon(R.drawable.arrow_back_ios_24px)
            materialToolbarBookSellingInquiryDetail.setNavigationOnClickListener {
                requireActivity().onBackPressed()
            }
        }
    }

    private fun detailSetting() {
        arguments?.let { bundle ->
            // 데이터 추출
            val sellingInquiryPrice = bundle.getInt("sellingInquiryPrice")
            val sellingInquiryQuality = bundle.getInt("sellingInquiryQuality")
            val sellingInquiryISBN = bundle.getString("sellingInquiryISBN").orEmpty()
            val sellingInquiryDepositor = bundle.getString("sellingInquiryDepositor").orEmpty()
            val sellingInquiryBankName = bundle.getString("sellingInquiryBankName").orEmpty()
            val sellingInquiryBankAccountNumber = bundle.getString("sellingInquiryBankAccountNumber").orEmpty()
            val sellingInquiryBookName = bundle.getString("sellingInquiryBookName").orEmpty()
            val sellingInquiryBookAuthor = bundle.getString("sellingInquiryBookAuthor").orEmpty()
            val sellingInquiryTime = bundle.getLong("sellingInquiryTime")
            val sellingInquiryApprovalResult = bundle.getInt("sellingInquiryApprovalResult")

            // UI 업데이트
            fragmentBookSellingInquirydetailBinding.apply {
                textFieldBookSellingInquiryDetailDepoistor.editText?.setText(sellingInquiryDepositor)
                textFieldBookSellingInquiryDetailBankName.editText?.setText(sellingInquiryBankName)
                textFieldBookSellingInquiryDetailBankAccountNumber.editText?.setText(sellingInquiryBankAccountNumber)
                textViewBookSellingInquiryDetailTitle.text = sellingInquiryBookName
                textViewBookSellingInquiryDetailWriter.text = "저자: $sellingInquiryBookAuthor"
                textViewBookSellingInquiryDetailPrice.text = "예상 판매가: ${sellingInquiryPrice}원"
                textViewBookSellingInquiryDetailQuality.text = "내가 선택한 품질: ${getQualityText(sellingInquiryQuality)}"
                textViewBookSellingInquiryDetailSellingTime.text = "등록 날짜: ${formatDate(sellingInquiryTime)}"
                textViewBookSellingInquiryDetailApprovalResult.text = "승인 상태: ${getStateText(sellingInquiryApprovalResult)}"
            }
        }
    }

    // 품질 상태 텍스트 변환
    private fun getQualityText(quality: Int): String {
        return when (quality) {
            0 -> "상"
            1 -> "중"
            2 -> "하"
            else -> "오류"
        }
    }

    // 승인 상태 텍스트 변환
    private fun getStateText(state: Int): String {
        return when (state) {
            0 -> "승인 신청"
            1 -> "품질 검수 중"
            2 -> "검수 완료"
            else -> "오류"
        }
    }

    // 날짜 포맷팅
    private fun formatDate(time: Long): String {
        val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
        return dateFormat.format(Date(time))
    }

}