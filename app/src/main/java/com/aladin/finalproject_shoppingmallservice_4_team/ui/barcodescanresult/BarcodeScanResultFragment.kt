package com.aladin.finalproject_shoppingmallservice_4_team.ui.barcodescanresult

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.aladin.apiTestApplication.dto.BookItem
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentBarcodeScanResultBinding
import com.aladin.finalproject_shoppingmallservice_4_team.ui.bookdetail.BookDetailFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.custom.CustomDialogProgressbar
import com.aladin.finalproject_shoppingmallservice_4_team.ui.sellingcart.SellingCartFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceSubFragment
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BarcodeScanResultFragment : Fragment() {

    private lateinit var binding: FragmentBarcodeScanResultBinding
    private val viewModel: BarcodeScanResultViewModel by viewModels()
    private var isbn: String? = null // 전달받은 ISBN 값을 저장하는 변수
    private var progressDialog: CustomDialogProgressbar? = null // 다이얼로그
    private var isLoaded = false // 중복 호출 방지 플래그

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isbn = arguments?.getString("ISBN")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentBarcodeScanResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ProgressDialog 생성 및 표시
        // 만약 이미 로드된 경우 다시 실행 하지 않음
        if (!isLoaded) {
            progressDialog = CustomDialogProgressbar(requireContext())
            progressDialog?.show()
        }

        // Toolbar 설정 추가
        settingToolbar()

        // ISBN 값이 있으면 ViewModel을 통해 책 정보 로드
        // 중복 데이터 로드 방지
        if (!isLoaded) {
            isbn?.let { viewModel.fetchBookData(it) }
        }

        // ViewModel의 책 데이터를 도서 정보 업데이트
        observeViewModel()
    }

    // Toolbar를 구성하는 메서드
    private fun settingToolbar() {
        binding.apply {
            toolbarBarcodeScanResult.title = "검색 결과"
            toolbarBarcodeScanResult.setNavigationIcon(R.drawable.arrow_back_ios_24px)
            toolbarBarcodeScanResult.setNavigationOnClickListener {
                requireActivity().onBackPressed()
            }
        }
    }

    private fun observeViewModel() {
        // 책 데이터를 업데이트
        viewModel.ISBNbook.observe(viewLifecycleOwner) { book ->
            // 중복 업데이트 방지
            if (book != null && !isLoaded) {
                updateUI(book)
                progressDialog?.dismiss()
                // 데이터 로드 완료 플래그 설정
                isLoaded = true
            } else if (book == null && !isLoaded) {
                Toast.makeText(requireContext(), "책 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                progressDialog?.dismiss()
            }
        }

        // 에러 메시지 관련
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                Log.e("API_CALL", it)
                // 에러 발생 시 다이얼로그 닫기
                progressDialog?.dismiss()
            }
        }
    }

    private fun updateUI(book: BookItem) {
        with(binding) {
            // 책 이미지를 업데이트
            Glide.with(requireContext())
                .load(book.cover)
                .into(imageViewBarcodeScanResultBookImage)

            // 텍스트 뷰를 API 응답 데이터로 업데이트
            textViewBarcodeScanResultBookName.text = book.title
            textViewBarcodeScanResultBookAuthor.text = book.author
            textViewBarcodeScanResultPrice.text = "정가 : ${book.priceStandard}"

            // 버튼 클릭 리스너
            buttonBarcodeScanResultPurchase.setOnClickListener {
                replaceSubFragment(BookDetailFragment(), true)
            }
            buttonBarcodeScanResultSelling.setOnClickListener {
                replaceSubFragment(SellingCartFragment(), true)
            }
        }
    }
}
