package com.aladin.finalproject_shoppingmallservice_4_team.ui.search

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
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
): ViewModel() {
    // UI에서 사용할 데이터 (LiveData)
    private val _books = MutableLiveData<List<BookItem>>(emptyList())
    val books: LiveData<List<BookItem>> get() = _books

    // 공지사항 목록을 가져왔는지
    private val _isLoadSearchList = MutableLiveData<Boolean>(false)
    val isLoadSearchList: LiveData<Boolean> get() = _isLoadSearchList

    // 책 이름으로 검색
    // ISBN 검색도 동일 (isbn에서 -빼고 숫자만  입력)
    fun searchBook(query:String, maxResults: Int, sort: String) = viewModelScope.launch {
        runCatching {
            searchRepository.searchBooks(query, maxResults, sort)
        }.onSuccess {
            _books.value = it
            _isLoadSearchList.postValue(true)
        }.onFailure {
            Log.d("bookDetail", "error: $it")
        }

    }
}