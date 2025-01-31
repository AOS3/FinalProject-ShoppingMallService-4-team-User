package com.aladin.finalproject_shoppingmallservice_4_team.ui.booklist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aladin.apiTestApplication.dto.RecommendBookItem
import com.aladin.finalproject_shoppingmallservice_4_team.model.BookListModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class BookListViewModel @Inject constructor(private val bookListRepository: BookListRepository) :
    ViewModel() {

    // 중고책 리스트
    private val _usedBookList = MutableLiveData<List<BookListModel>>()
    val usedBookList: LiveData<List<BookListModel>> get() = _usedBookList

    // 신책 리스트
    private val _newBookList = MutableLiveData<List<RecommendBookItem>>()
    val newBookList: LiveData<List<RecommendBookItem>> get() = _newBookList

    // 도서 목록을 가져왔는지
    private val _isLoadBookList = MutableLiveData<Boolean>(false)
    val isLoadBookList: LiveData<Boolean> get() = _isLoadBookList

    // 도서 목록 가져오기
    fun gettingBookList(query: String) {
        if (query == "Used") {
            // 중고 책일 경우
            viewModelScope.launch {
                val (usedBookInventoryList, coverList) = withContext(Dispatchers.IO) {
                    bookListRepository.gettingUsedBookInventory()
                }

                withContext(Dispatchers.IO) {
                    for (i in usedBookInventoryList.indices) {
                        usedBookInventoryList[i].cover =
                            coverList.getOrNull(i) // coverList에서 해당 index의 값을 가져옴
                    }
                }

                withContext(Dispatchers.IO) {
                    // 이후 업데이트된 목록을 _usedBookList에 전달
                    _usedBookList.postValue(usedBookInventoryList)
                }
                // 가져온 리스트가 비어있지 않다면
                if (usedBookInventoryList.isNotEmpty()) {
                    _isLoadBookList.postValue(true)
                } else {
                    _isLoadBookList.postValue(true)
                }
            }

        } else {
            // 신간,주목할만한 신간, 베스트 셀러, 블로거 베스트 셀러 일경우
            // ItemNewAll : 신간 전체 리스트
            // ItemNewSpecial : 주목할 만한 신간 리스트
            // Bestseller : 베스트셀러
            // BlogBest : 블로거 베스트셀러
            viewModelScope.launch {
                val usedBookInventoryList = withContext(Dispatchers.IO) {
                    bookListRepository.gettingAladdinRecommendBook(query)
                }
                _newBookList.postValue(usedBookInventoryList)

                // 가져온 리스트가 비어있지 않다면
                if (usedBookInventoryList.isNotEmpty()) {
                    _isLoadBookList.postValue(true)
                } else {
                    _isLoadBookList.postValue(true)
                }
            }
        }
    }
}