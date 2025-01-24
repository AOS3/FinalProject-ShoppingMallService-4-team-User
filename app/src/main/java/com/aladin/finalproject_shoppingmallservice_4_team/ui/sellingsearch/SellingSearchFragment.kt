package com.aladin.finalproject_shoppingmallservice_4_team.ui.sellingsearch

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aladin.apiTestApplication.dto.BookItem
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentSellingSearchBinding
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.RowSellingSearchBinding
import com.aladin.finalproject_shoppingmallservice_4_team.model.SellingCartModel
import com.aladin.finalproject_shoppingmallservice_4_team.ui.barcodescanner.BarcodeScannerFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.bookdetail.BookDetailFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.custom.CustomDialog
import com.aladin.finalproject_shoppingmallservice_4_team.ui.sellingcart.SellingCartFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.hideSoftInput
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceMainFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceSubFragment
import com.bumptech.glide.Glide
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SellingSearchFragment : Fragment() {

    private var _binding: FragmentSellingSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SellingSearchViewModel by viewModels()
    private var bookList: MutableList<BookItem> = mutableListOf()
    private var query: String = ""

    // Firebase Firestore instance
    private val firestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSellingSearchBinding.inflate(inflater, container, false)

        // 전달된 ISBN 값을 가져옴
        val scannedIsbn = arguments?.getString("ISBN")

        // Toolbar 설정 메서드 호출
        settingToolbar()

        // 검색 기능 설정 메서드 호출
        setupSearchFeature(scannedIsbn)

        // RecyclerView 설정 메서드 호출
        setupRecyclerView()

        // "더보기" 버튼 설정 메서드 호출
        setupMoreButton()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    // Toolbar 설정 메서드
    private fun settingToolbar() {
        binding.materialToolbarSellingSearch.apply {
            title = "판매할 도서 검색"
            setNavigationIcon(R.drawable.arrow_back_ios_24px)
            setNavigationOnClickListener {
                requireActivity().onBackPressed()
            }
        }
    }


    // 검색 기능 설정 메서드
    private fun setupSearchFeature(scannedIsbn: String?) {
        if (scannedIsbn != null) {
            binding.editTextSellingSearchSearch.setText(scannedIsbn)

            // 확인 버튼 클릭 동작 실행
            binding.imageViewSellingSearchSearchIcon.setOnClickListener {
                val query = binding.editTextSellingSearchSearch.text.toString().trim()
                if (query.isNotEmpty()) {
                    if (query.matches("\\d{13}".toRegex())) { // ISBN 검사
                        viewModel.searchByIsbn(query) // ISBN 검색
                    } else {
                        viewModel.searchBooks(query, 10, "Accuracy") // 일반 텍스트 검색
                    }
                    binding.editTextSellingSearchSearch.text.clear()
                    binding.editTextSellingSearchSearch.hideSoftInput() // 키보드 내리기
                } else {
                    Toast.makeText(requireContext(), "검색어를 입력하세요.", Toast.LENGTH_SHORT).show()
                }
            }

            // 프로그램적으로 "확인" 버튼 클릭
            binding.imageViewSellingSearchSearchIcon.performClick()
        }

        val performSearch = {
            query = binding.editTextSellingSearchSearch.text.toString().trim()
            if (query.isNotEmpty()) {
                if (query.matches("\\d{13}".toRegex())) { // ISBN 검사 (13자리 숫자)
                    viewModel.searchByIsbn(query) // ISBN 검색 메서드 호출
                } else {
                    viewModel.searchBooks(query, 10, "Accuracy") // 일반 텍스트 검색
                }
                binding.editTextSellingSearchSearch.text.clear()
                binding.editTextSellingSearchSearch.hideSoftInput() // 키보드 내리기
            } else {
                Toast.makeText(requireContext(), "검색어를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.editTextSellingSearchSearch.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
            ) {
                performSearch()
                true
            } else {
                false
            }
        }

        binding.imageViewSellingSearchSearchIcon.setOnClickListener {
            performSearch()
        }
    }




    // RecyclerView 설정 메서드
    private fun setupRecyclerView() {
        binding.recyclerViewSellingSearchInfo.apply {
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(MaterialDividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
            adapter = object : RecyclerView.Adapter<SellingSearchViewHolder>() {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SellingSearchViewHolder {
                    val binding = RowSellingSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                    return SellingSearchViewHolder(binding)
                }

                override fun onBindViewHolder(holder: SellingSearchViewHolder, position: Int) {
                    val book = bookList[position]
                    holder.bind(book)
                }

                override fun getItemCount(): Int = bookList.size
            }
        }

        viewModel.sellingSearchBooks.observe(viewLifecycleOwner) { books ->
            bookList.clear()
            bookList.addAll(books)
            binding.recyclerViewSellingSearchInfo.adapter?.notifyDataSetChanged()

            val isDataEmpty = books.isEmpty()
            binding.includeSellingSearchEmpty.root.isVisible = isDataEmpty
            binding.recyclerViewSellingSearchInfo.isVisible = !isDataEmpty
        }
    }


    // 버튼 설정 메서드
    private fun setupMoreButton() {

        // 바코드 찍기 버튼
        binding.buttonSellingSearchBarcodeScanner.setOnClickListener {
            val dataBundle = Bundle()
            dataBundle.putString("FragmentQuery", "SellingSearch")
            replaceMainFragment(BarcodeScannerFragment(), true, dataBundle = dataBundle)
        }

        // 더보기 버튼
        binding.buttonSellingSearchAddBookForSelling.setOnClickListener {
            if (query.isNotEmpty()) {
                viewModel.loadMoreBooks(query, "Accuracy")
            } else {
                Toast.makeText(requireContext(), "검색 후 더보기를 눌러주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    // ViewHolder 클래스
    private inner class SellingSearchViewHolder(private val binding: RowSellingSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BookItem) {
            binding.apply {
                // 책 이미지를 업데이트
                Glide.with(requireContext())
                    .load(item.cover)
                    .into(imageViewSellingSearchBook)
                textViewSellingSearchBookTitle.text = item.title
                textViewSellingSearchBookAuthor.text = item.author
                textViewSellingSearchBookPrice.text = "정가 : ${item.priceStandard}원"

                // 항목 클릭 리스너 추가
                itemView.setOnClickListener {
                    val dataBundle = Bundle()
                    dataBundle.putString("bookIsbn", item.isbn13)
                    replaceMainFragment(BookDetailFragment(), true, dataBundle = dataBundle)
                }

                // 등록 버튼 클릭 리스너
                buttonSellingSearchRegister.setOnClickListener {
                    val sellingCartItem = SellingCartModel(
                        sellingCartSellingPrice = (item.priceStandard * 0.7).toInt(),
                        sellingCartQuality = 0,
                        sellingCartISBN = item.isbn13,
                        sellingCartUserToken = "",
                        sellingCartTime = System.currentTimeMillis(),
                        sellingCartState = 0
                    )

                    addItemToFirestore(sellingCartItem)

                    val customDialog = CustomDialog(
                        context = itemView.context,
                        contentText = "팔기 장바구니에 등록되었습니다.",
                        icon = R.drawable.check_circle_24px,
                        positiveText = "장바구니로 이동",
                        onPositiveClick = {
                            replaceMainFragment(SellingCartFragment(), true)
                        },
                        negativeText = "계속 담기",
                        onNegativeClick = {
                            Toast.makeText(context, "계속 담기", Toast.LENGTH_SHORT).show()
                        }
                    )
                    customDialog.showCustomDialog()
                }
            }
        }

        // Firestore에 데이터 추가
        private fun addItemToFirestore(item: SellingCartModel) {
            val collectionRef = firestore.collection("SellingCartTable")

            collectionRef.add(item)
                .addOnSuccessListener {
//                    Toast.makeText(requireContext(), "장바구니에 추가되었습니다.", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
//                    Toast.makeText(requireContext(), "장바구니 추가 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
