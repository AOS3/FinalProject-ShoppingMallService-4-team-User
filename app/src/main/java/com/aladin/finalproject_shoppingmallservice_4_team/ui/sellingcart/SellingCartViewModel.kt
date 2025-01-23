package com.aladin.finalproject_shoppingmallservice_4_team.ui.sellingcart

import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aladin.apiTestApplication.dto.BookItem
import com.aladin.finalproject_shoppingmallservice_4_team.model.SellingCartModel
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SellingCartViewModel @Inject constructor(
    private val sellingCartRepository: SellingCartRepository,
    val firestore: FirebaseFirestore
) : ViewModel() {

    private val _cartItems = MutableLiveData<List<SellingCartModel>>()
    val cartItems: LiveData<List<SellingCartModel>> get() = _cartItems

    private val _sellingCartBooks = MutableLiveData<List<BookItem>>(emptyList())
    val sellingCartBooks: LiveData<List<BookItem>> get() = _sellingCartBooks

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _selectedItems = MutableLiveData<Set<String>>(emptySet()) // 초기화
    val selectedItems: LiveData<Set<String>> get() = _selectedItems

    private val _totalEstimatedPrice = MutableLiveData<Int>(0)
    val totalEstimatedPrice: LiveData<Int> get() = _totalEstimatedPrice

    private var allItems: List<SellingCartModel> = emptyList() // 초기화

    private var currentPage: Int = 1
    private var currentQuery: String = ""
    private val fetchedBookIds = mutableSetOf<String>() // 중복 방지용 Set
    private var firestoreDataLoaded = false
    private var apiDataLoaded = false


    fun fetchCartItemsWithApi() {
        _isLoading.postValue(true) // 로딩 시작
        firestoreDataLoaded = false
        apiDataLoaded = false

        viewModelScope.launch {
            firestore.collection("SellingCartTable")
                .get()
                .addOnSuccessListener { result ->
                    val items = result.documents.mapNotNull { doc ->
                        doc.toObject(SellingCartModel::class.java)?.apply {
                            documentId = doc.id // 문서 ID 추가
                        }
                    }.sortedByDescending { it.sellingCartTime }

                    _cartItems.postValue(items)
                    firestoreDataLoaded = true
                    fetchApiData(items.map { it.sellingCartISBN })
                }
                .addOnFailureListener { e ->
                    Log.e("SellingCart", "Error fetching cart items", e)
                    firestoreDataLoaded = true
                    checkIfLoadingComplete()
                }
        }
    }

    private fun fetchApiData(isbns: List<String>) {
        viewModelScope.launch {
            if (isbns.isEmpty()) {
                apiDataLoaded = true
                checkIfLoadingComplete()
                return@launch
            }

            val books = mutableListOf<BookItem>()
            val totalCalls = isbns.size
            var completedCalls = 0

            for (isbn in isbns) {
                runCatching {
                    sellingCartRepository.searchBooks(isbn, maxResults = 1, sort = "Accuracy")
                }.onSuccess { result ->
                    books.addAll(result)
                }.onFailure { error ->
                    Log.e("SellingCart", "Error fetching API data for ISBN: $isbn", error)
                }.also {
                    completedCalls++

                    if (completedCalls == totalCalls) {
                        _sellingCartBooks.postValue(books)
                        apiDataLoaded = true
                        checkIfLoadingComplete()
                    }
                }
            }
        }
    }

    private fun checkIfLoadingComplete() {
        if (firestoreDataLoaded && apiDataLoaded) {
            _isLoading.postValue(false)
            calculateTotalEstimatedPrice() // 로딩 완료 후 총 금액 계산
        }
    }


    fun searchBooks(query: String, maxResults: Int, sort: String) {
        if (_isLoading.value == true) return
        _isLoading.value = true
        currentQuery = query
        currentPage = 1
        fetchedBookIds.clear()

        viewModelScope.launch {
            runCatching {
                sellingCartRepository.searchBooks(query, maxResults, sort)
            }.onSuccess { sellingCartBooks ->
                val newBooks = sellingCartBooks.filterNot { fetchedBookIds.contains(it.isbn13) }
                fetchedBookIds.addAll(newBooks.map { it.isbn13 })
                _sellingCartBooks.postValue(newBooks)
            }.onFailure { error ->
                Log.e("SellingCart", "Error fetching books: $error")
            }.also {
                _isLoading.postValue(false) // 로딩 종료
            }
        }
    }

    fun searchByIsbn(isbn: String, onResult: (BookItem?) -> Unit) {
        _isLoading.value = true // 로딩 시작
        viewModelScope.launch {
            runCatching {
                sellingCartRepository.searchBooks(isbn, maxResults = 1, sort = "Accuracy")
            }.onSuccess { books ->
                onResult(books.firstOrNull()) // 성공 시 첫 번째 책 반환
            }.onFailure {
                onResult(null) // 실패 시 null 반환
            }.also {
                _isLoading.postValue(false) // 로딩 종료
            }
        }
    }

    // Firestore의 품질 및 예상 판매가 업데이트
    fun updateSellingCartQuality(item: SellingCartModel, bookPrice: Int, onComplete: (Boolean) -> Unit) {
        val updatedSellingPrice = when (item.sellingCartQuality) {
            0 -> (bookPrice * 0.7).toInt()
            1 -> (bookPrice * 0.5).toInt()
            2 -> (bookPrice * 0.3).toInt()
            else -> 0
        }

        val updateData = mapOf(
            "sellingCartQuality" to item.sellingCartQuality,
            "sellingCartSellingPrice" to updatedSellingPrice
        )

        try {
            firestore.collection("SellingCartTable")
                .document(item.documentId)
                .update(updateData)
                .addOnSuccessListener {
                    Log.d("SellingCart", "Firestore 업데이트 성공: ${item.documentId}")
                    onComplete(true) // 성공 콜백 호출
                }
                .addOnFailureListener { e ->
                    Log.e("SellingCart", "Firestore 업데이트 실패: ${item.documentId}", e)
                    onComplete(false) // 실패 콜백 호출
                }
        } catch (e: Exception) {
            Log.e("SellingCart", "Firestore 업데이트 중 예외 발생: ${item.documentId}", e)
            onComplete(false) // 예외 발생 시 실패 콜백 호출
        }
    }

    // 전체 데이터를 가져온 뒤 allItems를 업데이트
    fun setAllItems(items: List<SellingCartModel>) {
        allItems = items
        // 기본 값 : 전체 선택
        _selectedItems.value = items.map { it.documentId }.toSet()
    }

    fun selectAllItems(selectAll: Boolean) {
        if (selectAll) {
            _selectedItems.value = allItems.map { it.documentId }.toSet() // 모든 documentId 선택
        } else {
            _selectedItems.value = emptySet() // 선택 해제
        }
        calculateTotalEstimatedPrice() // 총 예상 금액 재계산
    }

    // 개별 선택/해제 메서드
    fun toggleItemSelection(documentId: String, isSelected: Boolean) {
        val currentSelection = _selectedItems.value.orEmpty().toMutableSet()
        if (isSelected) {
            currentSelection.add(documentId)
        } else {
            currentSelection.remove(documentId)
        }
        _selectedItems.value = currentSelection
        calculateTotalEstimatedPrice() // 총 예상 금액 재계산
    }

    // 모든 아이템이 선택되었는지 확인
    fun areAllItemsSelected(): Boolean {
        val currentSelection = _selectedItems.value.orEmpty()
        return currentSelection.size == allItems.size
    }

    fun calculateTotalEstimatedPrice() {
        val selectedDocumentIds = _selectedItems.value.orEmpty()
        val selectedBooks = allItems.filter { selectedDocumentIds.contains(it.documentId) }

        // 선택된 도서들의 sellingCartSellingPrice 합산
        val totalPrice = if (selectedBooks.isEmpty()) 0 else selectedBooks.sumOf { it.sellingCartSellingPrice }

        _totalEstimatedPrice.postValue(totalPrice) // 총 금액 업데이트
    }






    // Firestore에서 중복 데이터 제거 및 정렬 처리
    fun fetchDistinctCartItemsFromFirestore() {
        _isLoading.value = true // 로딩 시작
        viewModelScope.launch {
            firestore.collection("SellingCartTable")
                .get()
                .addOnSuccessListener { result ->
                    val items = result.documents.mapNotNull { it.toObject(SellingCartModel::class.java) }
                        .distinctBy { it.sellingCartISBN } // 중복 제거
                    _cartItems.postValue(items)
                    _isLoading.value = false // 로딩 종료
                }
                .addOnFailureListener { e ->
                    Log.e("SellingCart", "Error fetching cart items", e)
                    _isLoading.value = false // 로딩 종료
                }
        }
    }

    fun deleteCheckedSellingCartBooks(deleteDocumentIds: List<String>) {
        if (deleteDocumentIds.isEmpty()) {
            Log.e("SellingCart", "삭제할 Document ID가 없습니다.")
            return
        }

        viewModelScope.launch {
            try {
                firestore.runBatch { batch ->
                    deleteDocumentIds.forEach { documentId ->
                        val docRef = firestore.collection("SellingCartTable").document(documentId)
                        batch.delete(docRef)
                    }
                }.addOnSuccessListener {
                    Log.d("SellingCart", "Document 삭제 성공: $deleteDocumentIds")

                    // 삭제된 항목을 제외한 리스트로 갱신
                    val updatedCartItems = _cartItems.value.orEmpty()
                        .filterNot { deleteDocumentIds.contains(it.documentId) }
                    _cartItems.postValue(updatedCartItems)

                    // 선택 항목 초기화
                    _selectedItems.postValue(emptySet())

                    // 총 예상 금액 재계산
                    calculateTotalEstimatedPrice()

                }.addOnFailureListener { e ->
                    Log.e("SellingCart", "Firestore 데이터 삭제 실패", e)
                }
            } catch (e: Exception) {
                Log.e("SellingCart", "삭제 작업 중 예외 발생: ${e.message}", e)
            }
        }
    }



}
