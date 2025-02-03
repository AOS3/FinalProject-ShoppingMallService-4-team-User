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
import com.aladin.finalproject_shoppingmallservice_4_team.BookApplication
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.FragmentSellingCartBinding
import com.aladin.finalproject_shoppingmallservice_4_team.databinding.RowSellingCartBinding
import com.aladin.finalproject_shoppingmallservice_4_team.model.SellingCartModel
import com.aladin.finalproject_shoppingmallservice_4_team.ui.ask.AskFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.barcodescanner.BarcodeScannerFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.booksellinginquiry.BookSellingInquiryFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.custom.CustomDialog
import com.aladin.finalproject_shoppingmallservice_4_team.ui.custom.CustomDialogProgressbar
import com.aladin.finalproject_shoppingmallservice_4_team.ui.login.LoginFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.notification.NotificationFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.sellinglastpage.SellingLastPageFragment
import com.aladin.finalproject_shoppingmallservice_4_team.ui.sellingsearch.SellingSearchFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.removeFragment
import com.aladin.finalproject_shoppingmallservice_4_team.util.replaceMainFragment
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.divider.MaterialDividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

@AndroidEntryPoint
class SellingCartFragment : Fragment() {

    private lateinit var fragmentSellingCartBinding: FragmentSellingCartBinding
    private lateinit var adapter: RecyclerSellingCartAdapter
    private lateinit var progressBarDialog: CustomDialogProgressbar
    private lateinit var bookApplication: BookApplication

    private val sellingCartViewModel: SellingCartViewModel by viewModels()

    // 상태 플래그
    private var firestoreDataLoaded = false
    private var apiDataLoaded = false
    private var uiRendered = false

    // 체크된 도서 수량
    private var checkedItemCount: Int = 0
    // 총 예상 판매가
    private var totalEstimatedPrice: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        fragmentSellingCartBinding =
            FragmentSellingCartBinding.inflate(layoutInflater, container, false)

        // bookApplication 초기화
        bookApplication = requireActivity().application as BookApplication

        // 로그인 여부 확인
        if (checkLoginProcess()) {
            // 프로그레스 다이얼로그 설정
            progressBarDialog = CustomDialogProgressbar(requireContext())
            progressBarDialog.show()

            // 상태 플래그 초기화
            firestoreDataLoaded = false
            apiDataLoaded = false
            uiRendered = false

            // 팔기 장바구니 화면으로 넘어갈 올 때 sellingCartState 상태를 0으로 초기화하는 메서드 호출
            resetSellingCartStates()

            // RecyclerView 설정 메서드 호출
            settingRecyclerView()

            // 프로그레스 다이얼로그 호출
            startDataLoading()

            // LiveData 관찰
            observeViewModel()

            // 버튼 클릭 메서드 호출
            buttonSellingCartOnClick()
        }
        // Toolbar 설정 메서드 호출
        settingToolbar()

        return fragmentSellingCartBinding.root
    }

    // 로그인 여부 확인 메서드
    private fun checkLoginProcess(): Boolean {
        return try {
            // 로그인 상태 확인
            if (::bookApplication.isInitialized && bookApplication.loginUserModel != null) {
                // 로그인된 경우 ViewModel에 데이터 요청
                sellingCartViewModel.fetchCartItemsWithApi(bookApplication.loginUserModel.userToken)
                true
            } else {
                throw Exception("로그인 정보가 없습니다.")
            }
        } catch (e: Exception) {
            // 로그인되지 않은 경우 다이얼로그 표시 및 프래그먼트 종료
            val loginDialog = CustomDialog(
                requireContext(),
                onPositiveClick = {
                    removeFragment()
                    replaceMainFragment(LoginFragment(), true)
                },
                contentText = "로그인을 먼저 진행해주세요.",
                icon = R.drawable.error_24px
            )
            loginDialog.showCustomDialog()
            false
        }
    }

    // Toolbar를 구성하는 메서드
    private fun settingToolbar() {
        fragmentSellingCartBinding.apply {
            materialToolbarSellingCart.title = "팔기 장바구니"
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
                val dataBundle = Bundle().apply {
                    putString("FragmentQuery", "SellingCart")
                    putString("userToken", bookApplication.loginUserModel.userToken) // userToken 전달
                }
                removeFragment()
                replaceMainFragment(BarcodeScannerFragment(), true, dataBundle = dataBundle)
            }
            buttonSellingCartAddBookForSelling.setOnClickListener {
                if (checkedItemCount == 0) {
                    // Toast 메시지 표시
                    Toast.makeText(requireContext(), "선택된 도서가 없습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    // checkedItemCount가 0이 아닌 경우 선택된 도서의 sellingCartState를 업데이트
                    val selectedDocumentIds = sellingCartViewModel.selectedItems.value?.toList() ?: emptyList()

                    if (selectedDocumentIds.isNotEmpty()) {
                        // Firestore에서 선택된 도서들의 sellingCartState 업데이트
                        selectedDocumentIds.forEach { documentId ->
                            sellingCartViewModel.updateSellingCartState(documentId, 1) { success ->
                                if (!success) {
                                    Log.e("SellingCart", "Failed to update sellingCartState for $documentId")
                                }
                            }
                        }
                    }
                    // 다음 화면으로 이동
                    val bundle = Bundle().apply {
                        putInt("CHECKED_ITEM_COUNT", checkedItemCount)
                        putInt("TOTAL_ESTIMATED_PRICE", totalEstimatedPrice)
                    }
                    val sellingLastPageFragment = SellingLastPageFragment().apply {
                        arguments = bundle
                    }
                    replaceMainFragment(sellingLastPageFragment, true)
                }
            }
            buttonSellingCartDelete.setOnClickListener {
                val selectedDocumentIds = sellingCartViewModel.selectedItems.value?.toList() ?: emptyList()
                if (selectedDocumentIds.isNotEmpty()) {
                    // 삭제 작업 실행
                    sellingCartViewModel.deleteCheckedSellingCartBooks(selectedDocumentIds)

                    // UI를 명시적으로 갱신
                    sellingCartViewModel.cartItems.observe(viewLifecycleOwner) { updatedCartItems ->
                        if (updatedCartItems.isEmpty()) {
                            sellingCartViewModel.calculateTotalEstimatedPrice()
                        }
                    }
                    // 삭제 완료 메시지
                    Toast.makeText(requireContext(), "선택한 도서가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    // 선택된 도서가 없을 경우
                    Toast.makeText(requireContext(), "삭제할 항목을 선택하세요.", Toast.LENGTH_SHORT).show()
                }
            }
            buttonSellingCartAsk.setOnClickListener {
                replaceMainFragment(AskFragment(),true)
            }
            buttonSellingCartFAQ.setOnClickListener {
                Toast.makeText(requireContext(), "준비중입니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 팔기 장바구니 화면으로 넘어갈 올 때 sellingCartState 상태를 0으로 초기화하는 메서드
    private fun resetSellingCartStates() {
        sellingCartViewModel.resetAllSellingCartStates { success ->
            if (success) Log.d("SellingCart", "SellingCartState 초기화 성공")
            else Log.e("SellingCart", "SellingCartState 초기화 실패")
        }
    }

    // RecyclerView 설정 메서드
    private fun settingRecyclerView() {
        adapter = RecyclerSellingCartAdapter(emptyList())
        fragmentSellingCartBinding.recyclerViewSellingCartInfo.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@SellingCartFragment.adapter
            addItemDecoration(MaterialDividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        }
    }

    // 데이터 로딩 시작 메서드
    private fun startDataLoading() {
        val userToken = bookApplication.loginUserModel.userToken
        sellingCartViewModel.fetchCartItemsWithApi(userToken)
    }

    // 선택된 도서 수량 및 예상 판매가 계산 메서드
    private fun calculateCheckedItems() {
        val selectedItems = sellingCartViewModel.selectedItems.value.orEmpty()
        val items = sellingCartViewModel.cartItems.value.orEmpty()

        // 체크된 도서
        val checkedItems = items.filter { selectedItems.contains(it.documentId) }

        // 체크된 도서 수량 및 예상 판매가 계산
        checkedItemCount = checkedItems.size
        totalEstimatedPrice = checkedItems.sumOf { it.sellingCartSellingPrice }

        // UI 업데이트
        fragmentSellingCartBinding.textViewSellingCartTotalPrice.text = "총 예상 판매가: ${formatNumber(totalEstimatedPrice)}원"
    }

    // LiveData 상태 확인
    private fun observeViewModel() {
        sellingCartViewModel.isDataLoaded.observe(viewLifecycleOwner) { isDataLoaded ->
            if (isDataLoaded) {
                firestoreDataLoaded = true
                apiDataLoaded = true
                checkIfAllTasksCompleted()
            }
        }

        // RecyclerView 렌더링 완료 감지
        fragmentSellingCartBinding.recyclerViewSellingCartInfo.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    uiRendered = true
                    checkIfAllTasksCompleted()
                    fragmentSellingCartBinding.recyclerViewSellingCartInfo.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })

        // 장바구니 데이터 관찰
        sellingCartViewModel.cartItems.observe(viewLifecycleOwner) { items ->
            adapter.updateData(items)
            val isDataEmpty = items.isEmpty()

            fragmentSellingCartBinding.includeSellingCartEmpty.root.visibility =
                if (isDataEmpty) View.VISIBLE else View.GONE
            fragmentSellingCartBinding.recyclerViewSellingCartInfo.visibility =
                if (isDataEmpty) View.GONE else View.VISIBLE

            // 선택 상태 업데이트
            sellingCartViewModel.setAllItems(items)
        }

        // 선택된 도서 데이터 관찰
        sellingCartViewModel.selectedItems.observe(viewLifecycleOwner) { selectedItems ->
            fragmentSellingCartBinding.recyclerViewSellingCartInfo.post {
                // 선택 상태 갱신
                adapter.updateSelection(selectedItems)
                // RecyclerView 강제 갱신
                adapter.notifyDataSetChanged()
            }

            // 체크된 도서와 총 예상 판매가 계산
            calculateCheckedItems()

            // 전체 선택 체크박스 동기화
            val allSelected = selectedItems.size == sellingCartViewModel.cartItems.value?.size
            // 기존 리스너 제거
            fragmentSellingCartBinding.checkBoxSellingCartAll.setOnCheckedChangeListener(null)
            // 상태 업데이트
            fragmentSellingCartBinding.checkBoxSellingCartAll.isChecked = allSelected
            fragmentSellingCartBinding.checkBoxSellingCartAll.setOnCheckedChangeListener { _, isChecked ->
                // 다시 리스너 설정
                sellingCartViewModel.selectAllItems(isChecked)
            }
        }

        // 총 예상 판매가 관찰
        sellingCartViewModel.totalEstimatedPrice.observe(viewLifecycleOwner) { totalPrice ->
            fragmentSellingCartBinding.textViewSellingCartTotalPrice.text = "총 예상 판매가: ${formatNumber(totalPrice)}원"
        }

        // 추가 데이터 (제목, 저자) 관찰
        sellingCartViewModel.sellingCartBookTitle.observe(viewLifecycleOwner) { title ->
            Log.d("SellingCartFragment", "Book title observed: $title")
        }

        sellingCartViewModel.sellingCartBookAuthor.observe(viewLifecycleOwner) { author ->
            Log.d("SellingCartFragment", "Book author observed: $author")
        }
    }

    // 프로그레스 다이얼로그 해제 메서드
    private fun checkIfAllTasksCompleted() {
        if (firestoreDataLoaded && apiDataLoaded && uiRendered) {
            if (progressBarDialog.isShowing) {
                progressBarDialog.dismiss()
            }
        }
    }

    // 세 자리마다 콤마 추가하는 함수
    private fun formatNumber(number: Int): String {
        return NumberFormat.getNumberInstance(Locale.US).format(number)
    }

    // RecyclerView 어댑터
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
                binding.checkBoxSelectRowSellingCart.isChecked = sellingCartViewModel.selectedItems.value?.contains(item.documentId) == true

                binding.checkBoxSelectRowSellingCart.setOnCheckedChangeListener { _, isChecked ->
                    sellingCartViewModel.toggleItemSelection(item.documentId, isChecked)
                    sellingCartViewModel.calculateTotalEstimatedPrice() // 총 금액 갱신
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

                        sellingCartViewModel.updateSellingCartQuality(item, bookPrice) { success ->
                            if (!success) {
                                Toast.makeText(requireContext(), "업데이트 실패", Toast.LENGTH_SHORT).show()
                            }
                        }

                        // 버튼 색상 업데이트
                        updateButtonColors(item.sellingCartQuality, binding)

                        // 예상 판매가 업데이트
                        binding.textViewSellingCartEstimatedPrice.text = "예상 판매가: ${formatNumber(item.sellingCartSellingPrice)}원"

                        // 총 금액 갱신
                        sellingCartViewModel.calculateTotalEstimatedPrice()
                    }
                }

                // 데이터 초기화 및 캐시 활용
                val bookItem = bookCache[item.sellingCartISBN]
                if (bookItem != null) {
                    binding.textViewSellingCartBookTitle.text = bookItem.title
                    binding.textViewSellingCartBookAuthor.text = bookItem.author
                    binding.textViewSellingCartBookPrice.text = "${formatNumber(bookItem.priceStandard)}원"
                    binding.textViewSellingCartEstimatedPrice.text =
                        "예상 판매가: ${formatNumber(item.sellingCartSellingPrice)}원"

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
                    sellingCartViewModel.searchByIsbn(item.sellingCartISBN) { loadedBookItem ->
                        if (loadedBookItem != null) {
                            bookCache[item.sellingCartISBN] = loadedBookItem
                            binding.textViewSellingCartBookTitle.text = loadedBookItem.title
                            binding.textViewSellingCartBookAuthor.text = loadedBookItem.author
                            binding.textViewSellingCartBookPrice.text = "${formatNumber(loadedBookItem.priceStandard)}원"
                            binding.textViewSellingCartEstimatedPrice.text =
                                "예상 판매가: ${formatNumber(calculateEstimatedPrice(loadedBookItem.priceStandard, item.sellingCartQuality))}원"

                            Glide.with(binding.imageViewSellingCartBook.context)
                                .load(loadedBookItem.cover)
                                .into(binding.imageViewSellingCartBook)

                            // 총 금액 갱신
                            sellingCartViewModel.calculateTotalEstimatedPrice()
                        }
                    }
                }
            }

            // 품질에 따른 계산
            private fun calculateEstimatedPrice(price: Int, quality: Int): Int {
                return when (quality) {
                    0 -> (price * 0.7).roundToInt()
                    1 -> (price * 0.5).roundToInt()
                    2 -> (price * 0.3).roundToInt()
                    else -> 0
                }
            }

            // 상태에 따른 버튼 색상 변경
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

            // 버튼 상태 설정
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

            // 버튼 상태 리셋
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

        // ViewHolder 생성
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = RowSellingCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        // ViewHolder 바인딩
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount(): Int = items.size

        // 데이터 업데이트 메서드
        fun updateData(newItems: List<SellingCartModel>) {
            // 최신순 정렬
            items = newItems.sortedByDescending { it.sellingCartTime }
            notifyDataSetChanged()
        }
    }
}