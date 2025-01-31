package com.aladin.finalproject_shoppingmallservice_4_team.ui.sellinglastpage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentSellingLastPageBinding
import com.aladin.finalproject_shoppingmallservice_4_team.ui.ask.AskFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.booksellinginquiry.BookSellingInquiryFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.home.HomeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.sellinglastpage.SellingLastPageViewModel
import com.aladin.finalproject_shoppingmallservice_4_team.util.clearAllBackStack
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceMainFragment
import java.text.NumberFormat
import java.util.Locale

class SellingLastPageFragment : Fragment() {

    private lateinit var fragmmentSellingLastPageBinding: FragmentSellingLastPageBinding
    private val viewModel: SellingLastPageViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmmentSellingLastPageBinding = FragmentSellingLastPageBinding.inflate(layoutInflater, container, false)

        // Toolbar 설정 메서드 호출
        setupToolbar()

        // 텍스트뷰에 데이터 설정 메서드 호출
        setupTextView()

        // 발송 방법 체크박스 설정 메서드 호출
        setupWayChoiceCheckbox()

        // 매입 불가 상품 처리 방법 체크박스 설정 메서드 호출
        setupNoChoiceCheckbox()

        // 은행 선택 드롭다운 설정 메서드 호출
        setupBankDropdown()

        // 버튼 클릭 이벤트 메서드 호출
        setupButtonClickListener()

        return fragmmentSellingLastPageBinding.root
    }

    // Toolbar 설정 메서드
    private fun setupToolbar() {
        fragmmentSellingLastPageBinding.materialToolbarSellingLastPage.title = "중고 서적 팔기"
        fragmmentSellingLastPageBinding.materialToolbarSellingLastPage.setNavigationIcon(R.drawable.arrow_back_ios_24px)
        fragmmentSellingLastPageBinding.materialToolbarSellingLastPage.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    // 세 자리마다 콤마 추가하는 함수
    private fun formatNumber(number: Int): String {
        return NumberFormat.getNumberInstance(Locale.US).format(number)
    }

    // 텍스트뷰에 데이터 설정 메서드
    private fun setupTextView() {
        val bundle = arguments
        if (bundle != null) {
            val checkedItemCount = bundle.getInt("CHECKED_ITEM_COUNT", 0)
            val totalEstimatedPrice = bundle.getInt("TOTAL_ESTIMATED_PRICE", 0)

            fragmmentSellingLastPageBinding.textViewSellingLastPage.text =
                "판매 상품 : 총 ${checkedItemCount}권, ${formatNumber(totalEstimatedPrice)}원"
            fragmmentSellingLastPageBinding.textViewSellingLastPageTotalPrice.text =
                "판매 상품 : 총 ${checkedItemCount}권\n총 예상 판매 가격 : ${formatNumber(totalEstimatedPrice)}원"
        } else {
            fragmmentSellingLastPageBinding.textViewSellingLastPage.text = "판매 상품 정보가 없습니다."
        }
    }

    // 발송 방법 체크박스 설정 메서드
    private fun setupWayChoiceCheckbox() {
        fragmmentSellingLastPageBinding.checkBoxSellingLastPageWayChoice1.isChecked = true

        fragmmentSellingLastPageBinding.checkBoxSellingLastPageWayChoice1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                fragmmentSellingLastPageBinding.checkBoxSellingLastPageWayChoice2.isChecked = false
            } else if (!fragmmentSellingLastPageBinding.checkBoxSellingLastPageWayChoice2.isChecked) {
                fragmmentSellingLastPageBinding.checkBoxSellingLastPageWayChoice1.isChecked = true
            }
        }

        fragmmentSellingLastPageBinding.checkBoxSellingLastPageWayChoice2.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                fragmmentSellingLastPageBinding.checkBoxSellingLastPageWayChoice1.isChecked = false
            } else if (!fragmmentSellingLastPageBinding.checkBoxSellingLastPageWayChoice1.isChecked) {
                fragmmentSellingLastPageBinding.checkBoxSellingLastPageWayChoice2.isChecked = true
            }
        }
    }

    // 매입 불가 상품 처리 방법 체크박스 설정 메서드
    private fun setupNoChoiceCheckbox() {
        fragmmentSellingLastPageBinding.checkBoxSellingLastPageNoChoice1.isChecked = true

        fragmmentSellingLastPageBinding.checkBoxSellingLastPageNoChoice1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                fragmmentSellingLastPageBinding.checkBoxSellingLastPageNoChoice2.isChecked = false
            } else if (!fragmmentSellingLastPageBinding.checkBoxSellingLastPageNoChoice2.isChecked) {
                fragmmentSellingLastPageBinding.checkBoxSellingLastPageNoChoice1.isChecked = true
            }
        }

        fragmmentSellingLastPageBinding.checkBoxSellingLastPageNoChoice2.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                fragmmentSellingLastPageBinding.checkBoxSellingLastPageNoChoice1.isChecked = false
            } else if (!fragmmentSellingLastPageBinding.checkBoxSellingLastPageNoChoice1.isChecked) {
                fragmmentSellingLastPageBinding.checkBoxSellingLastPageNoChoice2.isChecked = true
            }
        }
    }

    // 은행 선택 드롭다운 설정 메서드
    private fun setupBankDropdown() {
        val bankOptions = listOf("선택하세요", "KB국민은행", "신한은행", "우리은행", "하나은행", "농협은행")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, bankOptions)
        fragmmentSellingLastPageBinding.autoCompleteTextViewSellingLastPageBank.setAdapter(adapter)
    }

    // 버튼 클릭 이벤트 메서드
    private fun setupButtonClickListener() {
        fragmmentSellingLastPageBinding.buttonSellingLastPageAddBookForSelling.setOnClickListener {
            val shippingMethod = if (fragmmentSellingLastPageBinding.checkBoxSellingLastPageWayChoice1.isChecked) 0 else 1
            val nonPurchaseableMethod =
                if (fragmmentSellingLastPageBinding.checkBoxSellingLastPageNoChoice1.isChecked) 0 else 1
            val depositorName = fragmmentSellingLastPageBinding.textFieldSellingLastPageDepositor.editText?.text.toString()
                .trim()
            val bankName = fragmmentSellingLastPageBinding.autoCompleteTextViewSellingLastPageBank.text.toString().trim()
            val accountNumber =
                fragmmentSellingLastPageBinding.textFieldSellingLastPageAccount.editText?.text.toString().trim()
            val userToken = "USER_TOKEN" // 로그인 모듈과 연동 필요

            // 입력 값 검증
            if (depositorName.isEmpty()) {
                Toast.makeText(requireContext(), "예금주를 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (bankName.isEmpty() || bankName == "선택하세요") {
                Toast.makeText(requireContext(), "은행을 선택하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (accountNumber.isEmpty()) {
                Toast.makeText(requireContext(), "계좌 번호를 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!fragmmentSellingLastPageBinding.checkBoxSellingLastPageAgree.isChecked) {
                Toast.makeText(requireContext(), "약관에 동의해야 상품 승인 신청이 가능합니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Firestore에서 sellingCartState가 1인 데이터 가져와서 저장
            viewModel.saveBooksToFirestore(
                shippingMethod = shippingMethod,
                nonPurchaseableMethod = nonPurchaseableMethod,
                depositor = depositorName,
                bankName = bankName,
                accountNumber = accountNumber,
                userToken = userToken,
                onSuccess = {
                    Toast.makeText(requireContext(), "상품 승인 신청이 완료되었습니다.", Toast.LENGTH_SHORT).show()

                    viewModel.deleteBooksFromSellingCart(
                        onSuccess = {
                            clearAllBackStack()
                            removeFragment()
                            replaceMainFragment(BookSellingInquiryFragment(), true)
                        },
                        onFailure = { error ->
                            Toast.makeText(requireContext(), "삭제 실패: ${error.message}", Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                onFailure = { error ->
                    Toast.makeText(requireContext(), "저장 실패: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            )
        }

        fragmmentSellingLastPageBinding.buttonSellingLastPageAsk.setOnClickListener {
            clearAllBackStack()
            removeFragment()
            replaceMainFragment(AskFragment(),true)
        }
        fragmmentSellingLastPageBinding.buttonSellingLastPageFAQ.setOnClickListener {
            Toast.makeText(requireContext(), "준비중입니다.", Toast.LENGTH_SHORT).show()
        }
    }
}