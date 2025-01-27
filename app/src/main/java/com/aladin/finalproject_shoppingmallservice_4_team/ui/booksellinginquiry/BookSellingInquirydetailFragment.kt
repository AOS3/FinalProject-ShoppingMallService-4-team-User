package com.aladin.finalproject_shoppingmallservice_4_team.ui.booksellinginquiry

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentBookSellingInquiryBinding
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentBookSellingInquirydetailBinding
import com.aladin.finalproject_shoppingmallservice_4_team.model.SellingInquiryModel
import com.aladin.finalproject_shoppingmallservice_4_team.ui.ask.AskFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.qualityguide.QualityGuideFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceMainFragment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BookSellingInquirydetailFragment : Fragment() {

    private lateinit var fragmentBookSellingInquirydetailBinding: FragmentBookSellingInquirydetailBinding

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
            materialToolbarBookSellingInquiryDetail.title = "팔기 상세 내역"
            materialToolbarBookSellingInquiryDetail.setNavigationIcon(R.drawable.arrow_back_ios_24px)
            materialToolbarBookSellingInquiryDetail.setNavigationOnClickListener {
                requireActivity().onBackPressed()
            }
        }
    }

    // 버튼 설정
    private fun buttonSetting() {
        fragmentBookSellingInquirydetailBinding.apply {
            buttonOrderInquiryAsk.setOnClickListener {
                replaceMainFragment(AskFragment(), true)
            }
        }
        fragmentBookSellingInquirydetailBinding.apply {
            buttonOrderInquiryFAQ.setOnClickListener {
                Toast.makeText(requireContext(), "준비중인 기능입니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun detailSetting() {
        arguments?.let { bundle ->
            // 데이터 추출
            val sellingInquiryPrice = bundle.getInt("sellingInquiryPrice")
            val sellingInquiryQuality = bundle.getInt("sellingInquiryQuality")
            val sellingInquiryDepositor = bundle.getString("sellingInquiryDepositor")
            val sellingInquiryBankName = bundle.getString("sellingInquiryBankName")
            val sellingInquiryBankAccountNumber = bundle.getString("sellingInquiryBankAccountNumber")
            val sellingInquiryBookName = bundle.getString("sellingInquiryBookName")
            val sellingInquiryBookAuthor = bundle.getString("sellingInquiryBookAuthor")
            val sellingInquiryTime = bundle.getLong("sellingInquiryTime")
            val sellingInquiryApprovalResult = bundle.getInt("sellingInquiryApprovalResult")
            val sellingInquiryShippingMethod = bundle.getInt("sellingInquiryShippingMethod")
            val sellingInquiryNonPurchaseableMethod = bundle.getInt("sellingInquiryNonPurchaseableMethod")
            val sellingInquiryFinalPrice = bundle.getInt("sellingInquiryFinalPrice")
            val sellingInquiryChoiceQuality = bundle.getInt("sellingInquiryChoiceQuality")

            // UI 업데이트
            fragmentBookSellingInquirydetailBinding.apply {
                textFieldBookSellingInquiryDetailDepoistor.editText?.setText(sellingInquiryDepositor)
                textFieldBookSellingInquiryDetailBankName.editText?.setText(sellingInquiryBankName)
                textFieldBookSellingInquiryDetailBankAccountNumber.editText?.setText(sellingInquiryBankAccountNumber)
                textFieldBookSellingInquiryDetailShippingMethod.editText?.setText(getShippingMethodText(sellingInquiryShippingMethod))
                textFieldBookSellingInquiryDetailNonPurchaseableMethod.editText?.setText(getNonPurchaseableMethodText(sellingInquiryNonPurchaseableMethod))


                textViewBookSellingInquiryDetailTitle.text = sellingInquiryBookName
                textViewBookSellingInquiryDetailWriter.text = sellingInquiryBookAuthor
                // 품질 표시
                val qualityText = if (sellingInquiryApprovalResult == 2) {
                    val staffQuality = getQualityText(sellingInquiryChoiceQuality)
                    "최종 품질: ${getQualityText(sellingInquiryQuality)} -> $staffQuality"
                } else {
                    "내가 선택한 품질: ${getQualityText(sellingInquiryQuality)}"
                }
                textViewBookSellingInquiryDetailQuality.text = qualityText

                // 판매가 표시
                val priceText = if (sellingInquiryApprovalResult == 2) {
                    "판매가: ${sellingInquiryPrice}원 -> ${sellingInquiryFinalPrice}원"
                } else {
                    "예상 판매가: ${sellingInquiryPrice}원"
                }
                textViewBookSellingInquiryDetailPrice.text = priceText
                textViewBookSellingInquiryDetailSellingTime.text = "등록 날짜: ${formatDate(sellingInquiryTime)}"
                textViewBookSellingInquiryDetailApprovalResult.text = getStateText(sellingInquiryApprovalResult)
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

    private fun getShippingMethodText(method: Int): String {
        return when (method) {
            0 -> "책방 마켓 자체 택바사 신청"
            1 -> "편의점 택배 신청"
            else -> "오류"
        }
    }

    private fun getNonPurchaseableMethodText(method: Int): String {
        return when (method) {
            0 -> "반송(반송비 고객 부담, 보낸 주소로 발송)"
            1 -> "폐기(선택 시 자체 폐기되므로 반송 불가)"
            else -> "오류"
        }
    }

    // 날짜 포맷팅
    private fun formatDate(time: Long): String {
        val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
        return dateFormat.format(Date(time))
    }

}