package com.aladin.finalproject_shoppingmallservice_4_team.ui.barcodescanner

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aladin.apiTestApplication.dto.BookItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BarcodeScannerViewModel @Inject constructor(
    private val barcodeScannerRepository: BarcodeScannerRepository
) : ViewModel() {

    // LiveData
    private val _BarcodeScannerBooksBooks = MutableLiveData<List<BookItem>>(emptyList())
    val BarcodeScannerBooks: LiveData<List<BookItem>> get() = _BarcodeScannerBooksBooks

    // 로딩 상태
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private var currentPage: Int = 1
    private var currentQuery: String = ""
    // 중복 데이터 추적을 위한 Set
    private val fetchedBookIds = mutableSetOf<String>()

    // API 호출을 통한 데이터 호출
    fun searchBooks(query: String, maxResults: Int, sort: String) {
        if (_isLoading.value == true) return
        _isLoading.value = true
        currentQuery = query
        currentPage = 1
        // 새 검색 시 중복 데이터 추적 초기화
        fetchedBookIds.clear()

        viewModelScope.launch {
            runCatching {
                barcodeScannerRepository.searchBooks(query, maxResults, sort)
            }.onSuccess { BarcodeScannerBooks ->
                val newBooks = BarcodeScannerBooks.filterNot { fetchedBookIds.contains(it.isbn13) }
                // 새로 가져온 데이터의 ID 저장
                fetchedBookIds.addAll(newBooks.map { it.isbn13 })
                _BarcodeScannerBooksBooks.value = newBooks
                _isLoading.value = false
            }.onFailure { error ->
                Log.e("BarcodeScannerViewModel", "Error fetching books: $error")
                _isLoading.value = false
            }
        }
    }

    // "더보기" 버튼을 클릭 했을 때 데이터 호출
    fun loadMoreBooks(query: String, sort: String) {
        if (_isLoading.value == true) return
        _isLoading.value = true
        currentPage++

        viewModelScope.launch {
            runCatching {
                barcodeScannerRepository.searchBooks(query, 10 * currentPage, sort)
            }.onSuccess { books ->
                val newBooks = books.filterNot { fetchedBookIds.contains(it.isbn13) }
                fetchedBookIds.addAll(newBooks.map { it.isbn13 }) // 새로 가져온 데이터의 ID 저장
                val currentList = _BarcodeScannerBooksBooks.value ?: emptyList()
                _BarcodeScannerBooksBooks.value = currentList + newBooks
                _isLoading.value = false
            }.onFailure { error ->
                Log.e("BarcodeScannerViewModel", "Error fetching more books: $error")
                _isLoading.value = false
            }
        }
    }

    // ISBN 검색 API 호출
    fun searchByIsbn(isbn: String) {
        searchBooks(isbn, 1, "ISBN")
    }
}
