package com.aladin.finalproject_shoppingmallservice_4_team.ui.sellingsearch

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aladin.apiTestApplication.dto.BookItem
import com.aladin.finalproject_shoppingmallservice_4_team.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SellingSearchViewModel @Inject constructor(
    private val sellingSearchRepository: SellingSearchRepository
) : ViewModel() {

    private val _sellingSearchBooksBooks = MutableLiveData<List<BookItem>>(emptyList())
    val sellingSearchBooks: LiveData<List<BookItem>> get() = _sellingSearchBooksBooks

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isLoadSearchList = MutableLiveData<Boolean>(false)
    val isLoadSearchList: LiveData<Boolean> get() = _isLoadSearchList

    private var currentPage: Int = 1
    private var currentQuery: String = ""
    private val fetchedBookIds = mutableSetOf<String>() // 중복 방지용 Set


    // API 호출을 통한 데이터 호출
    fun searchBooks(query: String, maxResults: Int, sort: String) {
        if (_isLoading.value == true) return
        _isLoading.value = true

        currentQuery = query
        currentPage = 1
        fetchedBookIds.clear()

        viewModelScope.launch {
            runCatching {
                sellingSearchRepository.searchBooks(query, maxResults, sort)
            }.onSuccess { books ->
                val newBooks = books.filterNot { fetchedBookIds.contains(it.isbn13) }
                fetchedBookIds.addAll(newBooks.map { it.isbn13 })
                _sellingSearchBooksBooks.postValue(newBooks)
            }.onFailure { error ->
                Log.e("SellingSearchViewModel", "Error fetching books: $error")
            }.also {
                _isLoading.postValue(false) // 로딩 종료
            }
        }
    }

    // "더보기" 버튼을 클릭했을 때 추가 데이터 호출
    fun loadMoreBooks(query: String, sort: String) {
        if (_isLoading.value == true) return
        _isLoading.value = true
        currentPage++

        viewModelScope.launch {
            runCatching {
                sellingSearchRepository.searchBooks(query, 10 * currentPage, sort)
            }.onSuccess { books ->
                val newBooks = books.filterNot { fetchedBookIds.contains(it.isbn13) }
                fetchedBookIds.addAll(newBooks.map { it.isbn13 })
                val currentList = _sellingSearchBooksBooks.value ?: emptyList()
                _sellingSearchBooksBooks.postValue(currentList + newBooks)
            }.onFailure { error ->
                Log.e("SellingSearchViewModel", "Error fetching more books: $error")
            }.also {
                _isLoading.postValue(false) // 로딩 종료
            }
        }
    }

    fun searchByIsbn(isbn: String) {
        // ISBN 검색 API 호출
        searchBooks(isbn, 1, "ISBN")
    }
}
