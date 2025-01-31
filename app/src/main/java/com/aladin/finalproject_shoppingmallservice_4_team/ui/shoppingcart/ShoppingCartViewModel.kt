package com.aladin.finalproject_shoppingmallservice_4_team.ui.shoppingcart

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aladin.finalproject_shoppingmallservice_4_team.model.ShoppingCartModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ShoppingCartViewModel @Inject constructor(private val shoppingCartRepository: ShoppingCartRepository) :
    ViewModel() {

    // 장바구니 리스트
    private val _shoppingCartBookList = MutableLiveData<List<ShoppingCartModel>>()
    val shoppingCartBookList: LiveData<List<ShoppingCartModel>> get() = _shoppingCartBookList

    // 장바구니 리스트를 가져왔는지
    private val _isLoadShoppingCartList = MutableLiveData<Boolean>(false)
    val isLoadShoppingCartList: LiveData<Boolean> get() = _isLoadShoppingCartList

    // 로그인 안하고 들어왔을 떄 처리용
    fun dismissProgressDialog() {
        _isLoadShoppingCartList.value = true
    }

    // 책 데이터 가져오기
    fun gettingShoppingCartBookData(userToken: String) {
        viewModelScope.launch {
            val (shoppingCartTableList, bookDataList) = withContext(Dispatchers.IO) {
                shoppingCartRepository.gettingBookDataFromFireBaseStore(userToken)
            }

            withContext(Dispatchers.IO) {
                // 가져온 리스트들 Model에 업데이트
                for (i in shoppingCartTableList.indices) {
                    // getOrNull로 가져온 값이 null일 경우 안전하게 처리
                    shoppingCartTableList[i].shoppingCartBookCoverImage =
                        bookDataList.first.getOrNull(i) ?: "" // 기본값을 설정
                    shoppingCartTableList[i].shoppingCartBookTitle =
                        bookDataList.second.getOrNull(i) ?: "Unknown Title" // 기본값 설정
                    shoppingCartTableList[i].shoppingCartBookAuthor =
                        bookDataList.third.getOrNull(i) ?: "Unknown Author" // 기본값 설정
                }
            }

            withContext(Dispatchers.IO) {
                // 업데이트된 리스트 전달
                _shoppingCartBookList.postValue(shoppingCartTableList)
            }

            // 가져온 리스트가 비어있지 않다면
            if (shoppingCartTableList.isNotEmpty()) {
                _isLoadShoppingCartList.postValue(true)
            } else {
                _isLoadShoppingCartList.postValue(true)
            }
        }
    }

    // 로딩 다이얼로그 초기화 함수
    fun refreshProgressDialog() {
        _isLoadShoppingCartList.postValue(false)
    }

    fun deleteCheckedShoppingCartBookList(deleteList: List<Triple<String, String, Int>>, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val currentList = _shoppingCartBookList.value.orEmpty()

            // IO 스레드에서 삭제 작업 수행
            withContext(Dispatchers.IO) {
                // Firestore에서 데이터 삭제
                shoppingCartRepository.deleteShoppingCartBookData(deleteList)

                // 삭제된 데이터를 현재 리스트에서 제외
                val updatedList = currentList.filterNot { item ->
                    deleteList.any { it.first == item.shoppingCartUserToken && it.second == item.shoppingCartISBN }
                }

                // LiveData에 갱신된 리스트 반영 (postValue 사용)
                _shoppingCartBookList.postValue(updatedList)

                // 삭제 완료 후 콜백 호출
                onComplete(true)
            }
        }
    }

    // 구매하는 책 상태 변경 함수
    fun updateShoppingCartState(buyList: List<Triple<String, String,Int>>, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {

            withContext(Dispatchers.IO) {
                shoppingCartRepository.changeShoppingCartStateToOne(buyList)
            }
            onComplete(true)
        }
    }
}

