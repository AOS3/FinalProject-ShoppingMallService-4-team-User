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
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.math.roundToInt

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

    // 초기화
    private val _selectedItems = MutableLiveData<Set<String>>(emptySet())
    val selectedItems: LiveData<Set<String>> get() = _selectedItems

    private val _totalEstimatedPrice = MutableLiveData<Int>(0)
    val totalEstimatedPrice: LiveData<Int> get() = _totalEstimatedPrice

    // UI 표시 여부 상태
    private val _isDataLoaded = MutableLiveData<Boolean>()
    val isDataLoaded: LiveData<Boolean> get() = _isDataLoaded

    private val _sellingCartBookTitle = MutableLiveData<String>()
    val sellingCartBookTitle: LiveData<String> get() = _sellingCartBookTitle

    private val _sellingCartBookAuthor = MutableLiveData<String>()
    val sellingCartBookAuthor: LiveData<String> get() = _sellingCartBookAuthor

    // 초기화
    private var allItems: List<SellingCartModel> = emptyList()

    private var currentPage: Int = 1
    private var currentQuery: String = ""
    // 중복 방지용 Set
    private val fetchedBookIds = mutableSetOf<String>()
    private var firestoreDataLoaded = false
    private var apiDataLoaded = false

    // Firestore에서 데이터 가져오기
    fun fetchCartItemsWithApi(userToken: String) {
        firestoreDataLoaded = false
        apiDataLoaded = false

        if (userToken.isEmpty()) {
            Log.e("SellingCartViewModel", "User token is empty. Cannot fetch data.")
            return
        }

        viewModelScope.launch {
            try {
                // Firestore 데이터 로드
                val items = firestore.collection("SellingCartTable")
                    .whereEqualTo("sellingCartUserToken", userToken)
                    .get()
                    .await()
                    .documents
                    .mapNotNull { doc ->
                        doc.toObject(SellingCartModel::class.java)?.apply {
                            documentId = doc.id
                        }
                    }.sortedByDescending { it.sellingCartTime }

                allItems = items
                _cartItems.postValue(items)
                firestoreDataLoaded = true

                // API 데이터 로드
                val isbns = items.map { it.sellingCartISBN }
                fetchApiData(isbns)
            } catch (e: Exception) {
                Log.e("SellingCart", "Error fetching data from Firestore", e)
            } finally {
                checkIfLoadingComplete()
            }
        }
    }

    // API 데이터 로드
    private suspend fun fetchApiData(isbns: List<String>) {
        if (isbns.isEmpty()) {
            apiDataLoaded = true
            return
        }

        val books = mutableListOf<BookItem>()
        isbns.forEach { isbn ->
            val result = runCatching {
                sellingCartRepository.searchBooks(isbn, maxResults = 1, sort = "Accuracy")
            }
            result.onSuccess { bookItems ->
                val bookItem = bookItems.firstOrNull()
                if (bookItem != null) {
                    books.add(bookItem)

                    updateBookDetailsInFirestore(
                        isbn,
                        bookItem.title,
                        bookItem.author
                    ) { success ->
                        if (success) {
                            Log.d("SellingCart", "Updated book details for ISBN: $isbn")
                        } else {
                            Log.e("SellingCart", "Failed to update book details for ISBN: $isbn")
                        }
                    }
                }
            }.onFailure { error ->
                Log.e("fetchApiData", "Error fetching API data for ISBN: $isbn", error)
            }
        }

        _sellingCartBooks.postValue(books)
        apiDataLoaded = true
        checkIfLoadingComplete()
    }

    // 로딩 완료 확인
    private fun checkIfLoadingComplete() {
        if (firestoreDataLoaded && apiDataLoaded) {
            _cartItems.value = allItems // Firestore와 API 데이터가 모두 로드된 후 UI 갱신
            _isDataLoaded.postValue(true)
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
            0 -> (bookPrice * 0.7).roundToInt()
            1 -> (bookPrice * 0.5).roundToInt()
            2 -> (bookPrice * 0.3).roundToInt()
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
            // 모든 아이템 선택
            _selectedItems.value = allItems.map { it.documentId }.toSet()
        } else {
            // 선택 해제
            _selectedItems.value = emptySet()
        }

        calculateTotalEstimatedPrice() // 총 금액 재계산

        // 강제 UI 동기화를 위해 다시 업데이트
        _selectedItems.postValue(_selectedItems.value)
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

    fun calculateTotalEstimatedPrice() {
        val selectedDocumentIds = _selectedItems.value.orEmpty()
        val selectedBooks = allItems.filter { selectedDocumentIds.contains(it.documentId) }

        // 선택된 도서들의 sellingCartSellingPrice 합산
        val totalPrice = if (selectedBooks.isEmpty()) 0 else selectedBooks.sumOf { it.sellingCartSellingPrice }

        _totalEstimatedPrice.postValue(totalPrice) // 총 금액 업데이트
    }

    // Firestore에서 데이터 삭제 및 리스트에서 삭제
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
                    // 수정 코드 (즉시 UI 반영)
                    _cartItems.value = updatedCartItems

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

    fun updateSellingCartState(documentId: String, state: Int, onComplete: (Boolean) -> Unit) {
        val updateData = mapOf("sellingCartState" to state)

        firestore.collection("SellingCartTable")
            .document(documentId)
            .update(updateData)
            .addOnSuccessListener {
                Log.d("SellingCart", "Successfully updated sellingCartState for $documentId")
                onComplete(true)
            }
            .addOnFailureListener { e ->
                Log.e("SellingCart", "Failed to update sellingCartState for $documentId", e)
                onComplete(false)
            }
    }

    fun resetAllSellingCartStates(onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                firestore.collection("SellingCartTable")
                    .get()
                    .addOnSuccessListener { result ->
                        val batch = firestore.batch()
                        result.documents.forEach { document ->
                            val docRef = document.reference
                            batch.update(docRef, "sellingCartState", 0)
                        }
                        batch.commit()
                            .addOnSuccessListener {
                                Log.d("SellingCart", "Successfully reset all sellingCartState to 0")
                                onComplete(true)
                            }
                            .addOnFailureListener { e ->
                                Log.e("SellingCart", "Failed to reset all sellingCartState to 0", e)
                                onComplete(false)
                            }
                    }
                    .addOnFailureListener { e ->
                        Log.e("SellingCart", "Failed to fetch documents to reset sellingCartState", e)
                        onComplete(false)
                    }
            } catch (e: Exception) {
                Log.e("SellingCart", "Exception occurred while resetting sellingCartState", e)
                onComplete(false)
            }
        }
    }

    fun updateBookDetailsInFirestore(isbn: String, title: String, author: String, onComplete: (Boolean) -> Unit) {
        firestore.collection("SellingCartTable")
            .whereEqualTo("sellingCartISBN", isbn) // ISBN으로 해당 문서 찾기
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val documentId = querySnapshot.documents[0].id // 첫 번째 문서의 ID 가져오기
                    firestore.collection("SellingCartTable")
                        .document(documentId)
                        .update(mapOf("sellingCartBookTitle" to title, "sellingCartBookAuthor" to author))
                        .addOnSuccessListener {
                            onComplete(true) // 성공
                        }
                        .addOnFailureListener { e ->
                            Log.e("FirestoreUpdate", "Failed to update book details for $isbn", e)
                            onComplete(false) // 실패
                        }
                } else {
                    Log.e("FirestoreUpdate", "No document found for ISBN: $isbn")
                    onComplete(false) // 문서가 없는 경우
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreQuery", "Failed to query Firestore for ISBN: $isbn", e)
                onComplete(false) // 실패
            }
    }
}