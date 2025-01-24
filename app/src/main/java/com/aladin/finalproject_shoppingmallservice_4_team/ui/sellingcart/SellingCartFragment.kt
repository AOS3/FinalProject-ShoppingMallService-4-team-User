package com.aladin.finalproject_shoppingmallservice_4_team.ui.sellingcart

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aladin.apiTestApplication.dto.BookItem
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentSellingCartBinding
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.RowSellingCartBinding
import com.aladin.finalproject_shoppingmallservice_4_team.model.SellingCartModel
import com.aladin.finalproject_shoppingmallservice_4_team.ui.barcodescanner.BarcodeScannerFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.booksellinginquiry.BookSellingInquiryFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.custom.CustomDialogProgressbar
import com.aladin.finalproject_shoppingmallservice_4_team.ui.sellinglastpage.SellingLastPageFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.sellingsearch.SellingSearchFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceMainFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceSubFragment
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SellingCartFragment : Fragment() {

    private lateinit var fragmentSellingCartBinding: FragmentSellingCartBinding
    private val viewModel: SellingCartViewModel by viewModels()
    private lateinit var adapter: RecyclerSellingCartAdapter
    private lateinit var progressBarDialog: CustomDialogProgressbar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentSellingCartBinding = FragmentSellingCartBinding.inflate(layoutInflater, container, false)

        // ProgressBar 다이얼로그 초기화
        progressBarDialog = CustomDialogProgressbar(requireContext())

        // Toolbar 설정
        settingToolbar()

        // RecyclerView 설정
        settingRecyclerView()

        // 버튼 클릭 메서드 호출
        buttonSellingCartOnClick()

        // LiveData 관찰
        observeViewModel()

        // 전달된 ISBN 값을 Firestore에 추가
        val scannedIsbn = arguments?.getString("ISBN")
        scannedIsbn?.let {
            addItemToFirestore(it)
        }

        // Firestore 데이터 및 API 데이터 로드
        viewModel.fetchCartItemsWithApi()

        return fragmentSellingCartBinding.root
    }

    // Toolbar를 구성하는 메서드
    private fun settingToolbar() {
        fragmentSellingCartBinding.apply {
            materialToolbarSellingCart.title = "중고 서적 팔기"
            materialToolbarSellingCart.setNavigationIcon(R.drawable.arrow_back_ios_24px)
            materialToolbarSellingCart.setNavigationOnClickListener {
                requireActivity().onBackPressed()
            }

            materialToolbarSellingCart.menu.add(Menu.NONE, 1, Menu.NONE, "판매 조회")
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

            materialToolbarSellingCart.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    1 -> replaceMainFragment(BookSellingInquiryFragment(), true)
                }
                true
            }
        }
    }


    // 버튼 클릭 이벤트 설정
    private fun buttonSellingCartOnClick() {
        fragmentSellingCartBinding.apply {
            buttonSellingCartSearch.setOnClickListener {
                replaceMainFragment(SellingSearchFragment(), true)
            }

            buttonSellingCartBarcodeScanner.setOnClickListener {
                val dataBundle = Bundle()
                dataBundle.putString("FragmentQuery", "SellingCart")
                replaceMainFragment(BarcodeScannerFragment(), true, dataBundle = dataBundle)
            }

            buttonSellingCartAddBookForSelling.setOnClickListener {
                replaceMainFragment(SellingLastPageFragment(), true)
            }

            buttonSellingCartDelete.setOnClickListener {
                val selectedDocumentIds = viewModel.selectedItems.value?.toList() ?: emptyList()

                if (selectedDocumentIds.isNotEmpty()) {
                    // 삭제 작업 실행
                    viewModel.deleteCheckedSellingCartBooks(selectedDocumentIds)

                    // UI를 명시적으로 갱신
                    viewModel.cartItems.observe(viewLifecycleOwner) { updatedCartItems ->
                        if (updatedCartItems.isEmpty()) {
                            viewModel.calculateTotalEstimatedPrice() // 총 금액 0으로 갱신
                        }
                    }

                    // 삭제 완료 메시지
                    Toast.makeText(requireContext(), "선택한 도서가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    // 선택된 도서가 없을 경우
                    Toast.makeText(requireContext(), "삭제할 항목을 선택하세요.", Toast.LENGTH_SHORT).show()
                }
            }



        }
    }

    // RecyclerView 설정
    private fun settingRecyclerView() {
        adapter = RecyclerSellingCartAdapter(emptyList())
        fragmentSellingCartBinding.recyclerViewSellingCartInfo.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@SellingCartFragment.adapter
            addItemDecoration(MaterialDividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        }
    }

    // LiveData 상태 확인
    private fun observeViewModel() {
        // 데이터 로드 완료 감지
        viewModel.isDataLoaded.observe(viewLifecycleOwner) { isDataLoaded ->
            Log.d("LiveData", "isDataLoaded: $isDataLoaded")
            if (isDataLoaded) {
                if (progressBarDialog.isShowing) {
                    progressBarDialog.dismiss()
                }
            }
        }

        // 로딩 상태 관리
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            Log.d("LiveData", "isLoading: $isLoading")
            if (isLoading) {
                if (!progressBarDialog.isShowing) {
                    progressBarDialog.show()
                }
            }
        }

        viewModel.cartItems.observe(viewLifecycleOwner) { items ->
            Log.d("LiveData", "cartItems updated: ${items.size} items")
            adapter.updateData(items)
        }

        viewModel.isDataLoaded.observe(viewLifecycleOwner) { isDataLoaded ->
            Log.d("LiveData", "isDataLoaded: $isDataLoaded")
            if (isDataLoaded) {
                fragmentSellingCartBinding.recyclerViewSellingCartInfo.post {
                    adapter.notifyDataSetChanged() // RecyclerView 강제 렌더링
                }
            }
        }

        // Firestore 데이터 관찰
        viewModel.cartItems.observe(viewLifecycleOwner) { items ->
            Log.d("LiveData", "cartItems updated: ${items.size} items")
            fragmentSellingCartBinding.recyclerViewSellingCartInfo.post {
                adapter.updateData(items)
            }

            // RecyclerView 렌더링 감지
            fragmentSellingCartBinding.recyclerViewSellingCartInfo.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    Log.d("UI Render", "RecyclerView 렌더링 완료")
                    viewModel.setUiRendered() // UI 렌더링 완료 알림
                    fragmentSellingCartBinding.recyclerViewSellingCartInfo.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })
        }

        // API 데이터 관찰
        viewModel.sellingCartBooks.observe(viewLifecycleOwner) { books ->
            Log.d("SellingCartFragment", "API 데이터를 로드했습니다: $books")
        }

        // 개별 선택 및 전체 선택 동기화
        fragmentSellingCartBinding.checkBoxSellingCartAll.setOnCheckedChangeListener { _, isChecked ->
            viewModel.selectAllItems(isChecked)
        }

        viewModel.selectedItems.observe(viewLifecycleOwner) { selectedItems ->
            fragmentSellingCartBinding.recyclerViewSellingCartInfo.post {
                adapter.updateSelection(selectedItems) // 선택 상태 갱신
                adapter.notifyDataSetChanged() // RecyclerView 강제 갱신
            }

            // 전체 선택 체크박스 동기화
            val allSelected = selectedItems.size == viewModel.cartItems.value?.size
            fragmentSellingCartBinding.checkBoxSellingCartAll.setOnCheckedChangeListener(null) // 기존 리스너 제거
            fragmentSellingCartBinding.checkBoxSellingCartAll.isChecked = allSelected // 상태 업데이트
            fragmentSellingCartBinding.checkBoxSellingCartAll.setOnCheckedChangeListener { _, isChecked ->
                viewModel.selectAllItems(isChecked) // 다시 리스너 설정
            }
        }

        // 총 예상 가격 관찰
        viewModel.totalEstimatedPrice.observe(viewLifecycleOwner) { totalPrice ->
            fragmentSellingCartBinding.textViewSellingCartTotalPrice.text =
                "총 예상 판매가: ${totalPrice}원"
        }


        viewModel.cartItems.observe(viewLifecycleOwner) { updatedCartItems ->
            adapter.updateData(updatedCartItems)
            val isDataEmpty = updatedCartItems.isEmpty()
            fragmentSellingCartBinding.includeSellingCartEmpty.root.visibility = if (isDataEmpty) View.VISIBLE else View.GONE
            fragmentSellingCartBinding.recyclerViewSellingCartInfo.visibility = if (isDataEmpty) View.GONE else View.VISIBLE
        }

        viewModel.cartItems.observe(viewLifecycleOwner) { items ->
            adapter.updateData(items) // 어댑터에 최신 데이터 전달 (정렬은 어댑터 내에서 처리)

            val isDataEmpty = items.isEmpty()
            fragmentSellingCartBinding.includeSellingCartEmpty.root.visibility = if (isDataEmpty) View.VISIBLE else View.GONE
            fragmentSellingCartBinding.recyclerViewSellingCartInfo.visibility = if (isDataEmpty) View.GONE else View.VISIBLE

            viewModel.setAllItems(items) // 선택 상태 업데이트
        }

    }

    private fun addItemToFirestore(scannedIsbn: String) {
        viewModel.searchByIsbn(scannedIsbn) { bookItem ->
            if (bookItem != null) {
                val sellingCartItem = SellingCartModel(
                    sellingCartSellingPrice = (bookItem.priceStandard * 0.7).toInt(),
                    sellingCartQuality = 0,
                    sellingCartISBN = scannedIsbn,
                    sellingCartUserToken = "", // 사용자 토큰 추가 필요
                    sellingCartTime = System.currentTimeMillis(),
                    sellingCartState = 0
                )

                // Firestore 객체 초기화
                val firestore: FirebaseFirestore by lazy {
                    FirebaseFirestore.getInstance()
                }

                firestore.collection("SellingCartTable").add(sellingCartItem)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "장바구니에 추가되었습니다.", Toast.LENGTH_SHORT).show()
                        // ViewModel의 데이터 갱신
                        viewModel.fetchCartItemsWithApi()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "데이터 추가 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(requireContext(), "ISBN에 해당하는 도서를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private inner class RecyclerSellingCartAdapter(
        private var items: List<SellingCartModel>
    ) : RecyclerView.Adapter<RecyclerSellingCartAdapter.ViewHolder>() {

        private val selectedItems = mutableSetOf<String>() // 선택된 아이템 ISBN 저장
        private val bookCache = mutableMapOf<String, BookItem?>() // 도서 캐시 저장

        fun updateSelection(selectedItems: Set<String>) {
            this.selectedItems.clear()
            this.selectedItems.addAll(selectedItems)
            notifyDataSetChanged() // RecyclerView 갱신
        }

        inner class ViewHolder(val binding: RowSellingCartBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bind(item: SellingCartModel) {
                // View 초기화
                binding.checkBoxSelectRowSellingCart.setOnCheckedChangeListener(null)
                binding.checkBoxSelectRowSellingCart.isChecked = viewModel.selectedItems.value?.contains(item.documentId) == true

                binding.checkBoxSelectRowSellingCart.setOnCheckedChangeListener { _, isChecked ->
                    viewModel.toggleItemSelection(item.documentId, isChecked)
                    viewModel.calculateTotalEstimatedPrice() // 총 금액 갱신
                }

                // 품질(ToggleButton) 초기화
                binding.toggleGroupQuality.clearOnButtonCheckedListeners()
                when (item.sellingCartQuality) {
                    0 -> binding.toggleGroupQuality.check(R.id.button_sellingCart_High)
                    1 -> binding.toggleGroupQuality.check(R.id.button_sellingCart_Medium)
                    2 -> binding.toggleGroupQuality.check(R.id.button_sellingCart_Low)
                    else -> binding.toggleGroupQuality.clearChecked()
                }

                // 버튼 색상 초기화
                updateButtonColors(item.sellingCartQuality, binding)

                // 품질(ToggleButton) 변경 리스너
                binding.toggleGroupQuality.addOnButtonCheckedListener { _, checkedId, isChecked ->
                    if (isChecked) {
                        item.sellingCartQuality = when (checkedId) {
                            R.id.button_sellingCart_High -> 0
                            R.id.button_sellingCart_Medium -> 1
                            R.id.button_sellingCart_Low -> 2
                            else -> item.sellingCartQuality
                        }

                        // Firestore 업데이트
                        val bookPrice = bookCache[item.sellingCartISBN]?.priceStandard ?: 0
                        item.sellingCartSellingPrice = calculateEstimatedPrice(bookPrice, item.sellingCartQuality)

                        viewModel.updateSellingCartQuality(item, bookPrice) { success ->
                            if (!success) {
                                Toast.makeText(requireContext(), "업데이트 실패", Toast.LENGTH_SHORT).show()
                            }
                        }

                        // 버튼 색상 업데이트
                        updateButtonColors(item.sellingCartQuality, binding)

                        // 예상 판매가 업데이트
                        binding.textViewSellingCartEstimatedPrice.text = "예상 판매가: ${item.sellingCartSellingPrice}원"

                        // 총 금액 갱신
                        viewModel.calculateTotalEstimatedPrice()
                    }
                }

                // 데이터 초기화 및 캐시 활용
                val bookItem = bookCache[item.sellingCartISBN]
                if (bookItem != null) {
                    binding.textViewSellingCartBookTitle.text = bookItem.title
                    binding.textViewSellingCartBookAuthor.text = bookItem.author
                    binding.textViewSellingCartBookPrice.text = "${bookItem.priceStandard}원"
                    binding.textViewSellingCartEstimatedPrice.text =
                        "예상 판매가: ${item.sellingCartSellingPrice}원"

                    Glide.with(binding.imageViewSellingCartBook.context)
                        .load(bookItem.cover)
                        .into(binding.imageViewSellingCartBook)
                } else {
                    // 캐시에 데이터가 없는 경우 기본값 설정
                    binding.textViewSellingCartBookTitle.text = "데이터 로딩 중..."
                    binding.textViewSellingCartBookAuthor.text = "데이터 로딩 중..."
                    binding.textViewSellingCartBookPrice.text = "데이터 로딩 중..."
                    binding.textViewSellingCartEstimatedPrice.text = "데이터 로딩 중..."

                    // 비동기로 데이터 로드
                    viewModel.searchByIsbn(item.sellingCartISBN) { loadedBookItem ->
                        if (loadedBookItem != null) {
                            bookCache[item.sellingCartISBN] = loadedBookItem
                            binding.textViewSellingCartBookTitle.text = loadedBookItem.title
                            binding.textViewSellingCartBookAuthor.text = loadedBookItem.author
                            binding.textViewSellingCartBookPrice.text = "${loadedBookItem.priceStandard}원"
                            binding.textViewSellingCartEstimatedPrice.text =
                                "예상 판매가: ${calculateEstimatedPrice(loadedBookItem.priceStandard, item.sellingCartQuality)}원"

                            Glide.with(binding.imageViewSellingCartBook.context)
                                .load(loadedBookItem.cover)
                                .into(binding.imageViewSellingCartBook)

                            // 총 금액 갱신
                            viewModel.calculateTotalEstimatedPrice()
                        }
                    }
                }
            }


            private fun calculateEstimatedPrice(price: Int, quality: Int): Int {
                return when (quality) {
                    0 -> (price * 0.7).toInt()
                    1 -> (price * 0.5).toInt()
                    2 -> (price * 0.3).toInt()
                    else -> 0
                }
            }

            private fun updateUI(item: SellingCartModel, bookItem: BookItem?) {
                if (bookItem != null) {
                    Glide.with(requireContext()).load(bookItem.cover)
                        .into(binding.imageViewSellingCartBook)
                    binding.textViewSellingCartBookTitle.text = bookItem.title
                    binding.textViewSellingCartBookAuthor.text = bookItem.author
                    binding.textViewSellingCartBookPrice.text = "${bookItem.priceStandard}원"
                    binding.textViewSellingCartEstimatedPrice.text =
                        when (item.sellingCartQuality) {
                            0 -> "예상 판매가: ${(bookItem.priceStandard * 0.7).toInt()}원"
                            1 -> "예상 판매가: ${(bookItem.priceStandard * 0.5).toInt()}원"
                            2 -> "예상 판매가: ${(bookItem.priceStandard * 0.3).toInt()}원"
                            else -> "예상 판매가: 정해지지 않음"
                        }

                    setupQualityToggleGroup(item, bookItem)

                    // 총 금액 계산
                    viewModel.calculateTotalEstimatedPrice()
                }
            }

            private fun setupQualityToggleGroup(item: SellingCartModel, bookItem: BookItem) {
                binding.toggleGroupQuality.addOnButtonCheckedListener { _, checkedId, isChecked ->
                    if (isChecked) {
                        // 품질 변경에 따라 값 설정
                        item.sellingCartQuality = when (checkedId) {
                            R.id.button_sellingCart_High -> 0
                            R.id.button_sellingCart_Medium -> 1
                            R.id.button_sellingCart_Low -> 2
                            else -> item.sellingCartQuality
                        }

                        // 품질에 따른 예상 판매가 계산
                        val estimatedPrice = when (item.sellingCartQuality) {
                            0 -> (bookItem.priceStandard * 0.7).toInt()
                            1 -> (bookItem.priceStandard * 0.5).toInt()
                            2 -> (bookItem.priceStandard * 0.3).toInt()
                            else -> bookItem.priceStandard
                        }
                        item.sellingCartSellingPrice = estimatedPrice // 예상 판매가를 도서 객체에 업데이트

                        // Firestore 업데이트: 품질과 예상 판매가 업데이트
                        viewModel.updateSellingCartQuality(item, bookItem.priceStandard) { success ->
                            if (success) {
                                Toast.makeText(requireContext(), "품질 및 예상 판매가가 성공적으로 업데이트되었습니다.", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(requireContext(), "Firestore 업데이트 실패.", Toast.LENGTH_SHORT).show()
                            }
                        }

                        // UI 업데이트
                        binding.textViewSellingCartEstimatedPrice.text = "예상 판매가: ${estimatedPrice}원"

                        // 버튼 색상 업데이트
                        updateButtonColors(item.sellingCartQuality, binding)

                        // 총 금액 재계산
                        viewModel.calculateTotalEstimatedPrice()
                    }
                }
            }

            private fun updateButtonColors(selectedQuality: Int, binding: RowSellingCartBinding) {
                when (selectedQuality) {
                    0 -> setButtonState(
                        binding.buttonSellingCartHigh,
                        binding.buttonSellingCartMedium,
                        binding.buttonSellingCartLow
                    )

                    1 -> setButtonState(
                        binding.buttonSellingCartMedium,
                        binding.buttonSellingCartHigh,
                        binding.buttonSellingCartLow
                    )

                    2 -> setButtonState(
                        binding.buttonSellingCartLow,
                        binding.buttonSellingCartHigh,
                        binding.buttonSellingCartMedium
                    )

                    else -> resetButtonState(
                        binding.buttonSellingCartHigh,
                        binding.buttonSellingCartMedium,
                        binding.buttonSellingCartLow
                    )
                }
            }

            private fun setButtonState(selected: MaterialButton, vararg others: MaterialButton) {
                selected.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.main_color
                    )
                )
                selected.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

                others.forEach { button ->
                    button.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                    button.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.main_color
                        )
                    )
                }
            }

            private fun resetButtonState(vararg buttons: MaterialButton) {
                buttons.forEach { button ->
                    button.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                    button.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.main_color
                        )
                    )
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = RowSellingCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount(): Int = items.size

        fun updateData(newItems: List<SellingCartModel>) {
            items = newItems.sortedByDescending { it.sellingCartTime } // 최신순 정렬
            notifyDataSetChanged() // RecyclerView 갱신
        }

    }


}
