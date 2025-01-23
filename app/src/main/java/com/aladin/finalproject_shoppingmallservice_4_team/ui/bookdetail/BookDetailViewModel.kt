package com.aladin.finalproject_shoppingmallservice_4_team.ui.bookdetail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aladin.apiTestApplication.dto.BookItem
import com.aladin.finalproject_shoppingmallservice_4_team.model.BookCountModel
import com.aladin.finalproject_shoppingmallservice_4_team.model.SellingCartModel
import com.aladin.finalproject_shoppingmallservice_4_team.repository.BookDetailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookDetailViewModel @Inject constructor(
    private val bookDetailRepository: BookDetailRepository,
): ViewModel() {
    // UI에서 사용할 데이터 (LiveData)
    private val _books = MutableLiveData<List<BookItem>>()
    val books: LiveData<List<BookItem>> get() = _books

    // 상세정보 목록을 가져왔는지
    private val _isLoadBookDetailList = MutableLiveData<Boolean>(false)
    val isLoadBookDetailList: LiveData<Boolean> get() = _isLoadBookDetailList

    // 책 이름으로 검색
    // ISBN 검색도 동일 (isbn에서 -빼고 숫자만  입력)
    fun searchBook(query:String) = viewModelScope.launch {
        runCatching {
            bookDetailRepository.searchBooks(query)
        }.onSuccess {
            _books.value = it
            _isLoadBookDetailList.value = true
        }.onFailure {
            Log.d("bookDetail", "error: $it")
        }

    }

    fun sellingAddData() {
        val sellingCartItem = SellingCartModel(
            sellingCartSellingPrice = (books.value!!.first().priceStandard * 0.7).toInt(),
            sellingCartQuality = 0,
            sellingCartISBN = books.value!!.first().isbn13,
            sellingCartUserToken = "",
            sellingCartTime = System.currentTimeMillis(),
            sellingCartState = 0
        )

        bookDetailRepository.addItemToFirestore(sellingCartItem)
    }



}