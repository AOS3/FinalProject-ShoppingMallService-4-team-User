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
import com.aladin.finalproject_shoppingmallservice_4_team.ui.booksellinginquiry.BookSellingInquiryFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.home.HomeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.sellinglastpage.SellingLastPageViewModel
import com.aladin.finalproject_shoppingmallservice_4_team.util.clearAllBackStack
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceMainFragment

class SellingLastPageFragment : Fragment() {

    private lateinit var binding: FragmentSellingLastPageBinding
    private val viewModel: SellingLastPageViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSellingLastPageBinding.inflate(layoutInflater, container, false)

        // 툴바 설정
        setupToolbar()

        // 텍스트뷰에 데이터 설정
        setupTextView()

        // 발송 방법 체크박스 설정
        setupWayChoiceCheckbox()

        // 매입 불가 상품 처리 방법 체크박스 설정
        setupNoChoiceCheckbox()

        // 은행 선택 드롭다운 설정
        setupBankDropdown()

        // 버튼 클릭 이벤트
        setupButtonClickListener()

        return binding.root
    }

    private fun setupToolbar() {
        binding.materialToolbarSellingLastPage.title = "중고 서적 팔기"
        binding.materialToolbarSellingLastPage.setNavigationIcon(R.drawable.arrow_back_ios_24px)
        binding.materialToolbarSellingLastPage.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun setupTextView() {
        val bundle = arguments
        if (bundle != null) {
            val checkedItemCount = bundle.getInt("CHECKED_ITEM_COUNT", 0)
            val totalEstimatedPrice = bundle.getInt("TOTAL_ESTIMATED_PRICE", 0)

            binding.textViewSellingLastPage.text =
                "판매 상품 : 총 ${checkedItemCount}권, ${totalEstimatedPrice}원"
            binding.textViewSellingLastPageTotalPrice.text =
                "판매 상품 : 총 ${checkedItemCount}권\n총 예상 판매 가격 : ${totalEstimatedPrice}원"
        } else {
            binding.textViewSellingLastPage.text = "판매 상품 정보가 없습니다."
        }
    }

    private fun setupWayChoiceCheckbox() {
        binding.checkBoxSellingLastPageWayChoice1.isChecked = true

        binding.checkBoxSellingLastPageWayChoice1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.checkBoxSellingLastPageWayChoice2.isChecked = false
            } else if (!binding.checkBoxSellingLastPageWayChoice2.isChecked) {
                binding.checkBoxSellingLastPageWayChoice1.isChecked = true
            }
        }

        binding.checkBoxSellingLastPageWayChoice2.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.checkBoxSellingLastPageWayChoice1.isChecked = false
            } else if (!binding.checkBoxSellingLastPageWayChoice1.isChecked) {
                binding.checkBoxSellingLastPageWayChoice2.isChecked = true
            }
        }
    }

    private fun setupNoChoiceCheckbox() {
        binding.checkBoxSellingLastPageNoChoice1.isChecked = true

        binding.checkBoxSellingLastPageNoChoice1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.checkBoxSellingLastPageNoChoice2.isChecked = false
            } else if (!binding.checkBoxSellingLastPageNoChoice2.isChecked) {
                binding.checkBoxSellingLastPageNoChoice1.isChecked = true
            }
        }

        binding.checkBoxSellingLastPageNoChoice2.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.checkBoxSellingLastPageNoChoice1.isChecked = false
            } else if (!binding.checkBoxSellingLastPageNoChoice1.isChecked) {
                binding.checkBoxSellingLastPageNoChoice2.isChecked = true
            }
        }
    }

    private fun setupBankDropdown() {
        val bankOptions = listOf("선택하세요", "KB국민은행", "신한은행", "우리은행", "하나은행", "농협은행")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, bankOptions)
        binding.autoCompleteTextViewSellingLastPageBank.setAdapter(adapter)
    }

    private fun setupButtonClickListener() {
        binding.buttonSellingLastPageAddBookForSelling.setOnClickListener {
            val shippingMethod = if (binding.checkBoxSellingLastPageWayChoice1.isChecked) 0 else 1
            val nonPurchaseableMethod =
                if (binding.checkBoxSellingLastPageNoChoice1.isChecked) 0 else 1
            val depositorName = binding.textFieldSellingLastPageDepositor.editText?.text.toString()
                .trim()
            val bankName = binding.autoCompleteTextViewSellingLastPageBank.text.toString().trim()
            val accountNumber =
                binding.textFieldSellingLastPageAccount.editText?.text.toString().trim()
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

            if (!binding.checkBoxSellingLastPageAgree.isChecked) {
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
    }
}