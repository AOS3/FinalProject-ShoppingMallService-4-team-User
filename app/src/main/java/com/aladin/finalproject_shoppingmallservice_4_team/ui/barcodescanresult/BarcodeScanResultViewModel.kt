package com.aladin.finalproject_shoppingmallservice_4_team.ui.barcodescanresult

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aladin.apiTestApplication.dto.BookItem
import com.aladin.finalproject_shoppingmallservice_4_team.repository.BarcodeScanResultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BarcodeScanResultViewModel @Inject constructor(
    private val repository: BarcodeScanResultRepository
) : ViewModel() {
    // LiveData
    private val _ISBNbook = MutableLiveData<BookItem?>()
    val ISBNbook: LiveData<BookItem?> get() = _ISBNbook

    // 책이 정상적으로 가져와 졌는지
    private val _isBookFetched = MutableLiveData<Boolean>(false)
    val isBookFetched: LiveData<Boolean> get() = _isBookFetched

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun fetchBookData(isbn: String) {
        viewModelScope.launch {
            try {
                val bookResponse = repository.fetchBookData(isbn)
                val book = bookResponse.firstOrNull()
                _ISBNbook.value = book
                _isBookFetched.value = true
            } catch (e: Exception) {
                _errorMessage.value = "API 호출 중 오류가 발생했습니다."
                _isBookFetched.value = false
            }
        }
    }
}