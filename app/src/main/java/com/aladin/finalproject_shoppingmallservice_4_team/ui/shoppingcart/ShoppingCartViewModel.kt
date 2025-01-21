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

    fun testasd() {
        val shoppingCartItems = listOf(
            ShoppingCartModel(
                shoppingCartBookQualityCount = 2,
                shoppingCartISBN = "9791197613005",
                shoppingCartQuality = 0,
                shoppingCartSellingPrice = 30000,
                shoppingCartState = 0,
                shoppingCartTime = 1231233213L,
                shoppingCartUserToken = "test"
            ),
            ShoppingCartModel(
                shoppingCartBookQualityCount = 1,
                shoppingCartISBN = "9788962626421",
                shoppingCartQuality = 1,
                shoppingCartSellingPrice = 20000,
                shoppingCartState = 1,
                shoppingCartTime = 1631233213L,
                shoppingCartUserToken = "exampleUser"
            )
        )
        // Firestore에 데이터 추가
        viewModelScope.launch {
            val result = shoppingCartRepository.createShoppingCartBookData(shoppingCartItems)
            if (result) {
                println("데이터가 성공적으로 생성되었습니다!")
            } else {
                println("데이터 생성에 실패했습니다.")
            }
        }
    }

    // 책 데이터 가져오기
    fun gettingShoppingCartBookData() {
        viewModelScope.launch {
            val (shoppingCartTableList, bookDataList) = withContext(Dispatchers.IO) {
                shoppingCartRepository.gettingBookDataFromFireBaseStore()
            }

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

            // 업데이트된 리스트 전달
            _shoppingCartBookList.postValue(shoppingCartTableList)
            Log.e("asd", shoppingCartTableList.toString())

            // 가져온 리스트가 비어있지 않다면
            if (shoppingCartTableList.isNotEmpty()) {
                _isLoadShoppingCartList.postValue(true)
            } else {
                _isLoadShoppingCartList.postValue(true)
            }
        }
    }

    // 체크된 장바구니 데이터 삭제
    fun deleteCheckedShoppingCartBookList(deleteList: List<Pair<String, String>>) {
        viewModelScope.launch {
            val currentList = _shoppingCartBookList.value.orEmpty()

            // IO 스레드에서 삭제 작업 수행
            val updatedList = withContext(Dispatchers.IO) {
                // Firestore에서 데이터 삭제
                shoppingCartRepository.deleteShoppingCartBookData(deleteList)

                // 삭제된 데이터를 현재 리스트에서 제외
                currentList.filterNot { item ->
                    deleteList.any { it.first == item.shoppingCartUserToken && it.second == item.shoppingCartISBN }
                }
            }
            // LiveData에 갱신된 리스트 반영
            _shoppingCartBookList.value = updatedList
        }
    }
}

