package com.aladin.finalproject_shoppingmallservice_4_team.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aladin.apiTestApplication.dto.BookItem
import com.aladin.apiTestApplication.dto.RecommendBookItem
import com.aladin.finalproject_shoppingmallservice_4_team.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository
): ViewModel() {
    // UI에서 사용할 데이터 (LiveData)
    private val _books = MutableLiveData<List<RecommendBookItem>>()
    val books: LiveData<List<RecommendBookItem>> get() = _books

    // 책 목록에 따라 추천 검색
    fun recommendBook(queryType:String) {
        CoroutineScope(Dispatchers.Main).launch {
            val work1 = async(Dispatchers.IO) {
                homeRepository.recommendBooks(queryType = queryType, maxResults = 5)
            }
            _books.value = work1.await()
        }
    }

}