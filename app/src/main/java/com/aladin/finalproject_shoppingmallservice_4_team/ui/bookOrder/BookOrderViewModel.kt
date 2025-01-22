package com.aladin.finalproject_shoppingmallservice_4_team.ui.bookOrder

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aladin.finalproject_shoppingmallservice_4_team.model.OrderInquiryModel
import com.aladin.finalproject_shoppingmallservice_4_team.model.ShoppingCartModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class BookOrderViewModel @Inject constructor(private val bookOrderRepository: BookOrderRepository) :
    ViewModel() {

    // 정보 데이터를 다 가져왔는지
    private val _isLoadOrderDataList = MutableLiveData<Boolean>(false)
    val isLoadOrderDataList: LiveData<Boolean> get() = _isLoadOrderDataList

    // 구매하기 정보 리스트
    private val _orderBookList = MutableLiveData<List<ShoppingCartModel>>()
    val orderBookList: LiveData<List<ShoppingCartModel>> get() = _orderBookList

    // 총 구매 가격
    private val _totalSellingPrice = MutableLiveData<Int>()
    val totalSellingPrice: LiveData<Int> get() = _totalSellingPrice

    // 총 구매 갯수
    private val _totalBookQualityCount = MutableLiveData<Int>()
    val totalBookQualityCount: LiveData<Int> get() = _totalBookQualityCount

    // 구매 데이터 전부 전송 하였는지
    private val _isSuccessOrder = MutableLiveData<Pair<Boolean, Int>>(Pair(false, 0))
    val isSuccessOrder: LiveData<Pair<Boolean, Int>> get() = _isSuccessOrder

    private val _userToken = MutableLiveData<String>()
    private val _username = MutableLiveData<String>()
    private val _userPhoneNumber = MutableLiveData<String>()
    private val _userAddress = MutableLiveData<String>()

    // 로그인 안하고 들어왔을 떄 처리용
    fun dismissProgressDialog() {
        _isLoadOrderDataList.value = true
    }

    // 사용자 정보 가져오기
    fun getUserInfoData(
        token: String,
        name: String,
        phoneNumber: String,
        address: String
    ) {
        _userToken.value = token
        _username.value = name
        _userPhoneNumber.value = phoneNumber
        _userAddress.value = address
    }

    // 장바구니 상품 가져오기
    fun gettingUserOrderListBookData(userToken: String) {
        viewModelScope.launch {
            val (shoppingCartTableList, bookDataList) = withContext(Dispatchers.IO) {
                bookOrderRepository.gettingOrderBookDataFromFireBaseStore(userToken)
            }

            withContext(Dispatchers.IO) {
                // 가져온 리스트들 Model에 업데이트
                for (i in shoppingCartTableList.indices) {
                    if (i < bookDataList.first.size) {
                        shoppingCartTableList[i].shoppingCartBookCoverImage = bookDataList.first[i]
                    } else {
                        shoppingCartTableList[i].shoppingCartBookCoverImage = "" // 기본값
                    }

                    if (i < bookDataList.second.size) {
                        shoppingCartTableList[i].shoppingCartBookTitle = bookDataList.second[i]
                    } else {
                        shoppingCartTableList[i].shoppingCartBookTitle = "Unknown Title" // 기본값
                    }

                    if (i < bookDataList.third.size) {
                        shoppingCartTableList[i].shoppingCartBookAuthor = bookDataList.third[i]
                    } else {
                        shoppingCartTableList[i].shoppingCartBookAuthor = "Unknown Author" // 기본값
                    }
                }
            }

            withContext(Dispatchers.Default) {
                val totalPrice = shoppingCartTableList.sumOf { (it.shoppingCartSellingPrice) * (it.shoppingCartBookQualityCount) }
                val totalCount = shoppingCartTableList.sumOf { it.shoppingCartBookQualityCount }

                _totalSellingPrice.postValue(totalPrice)
                _totalBookQualityCount.postValue(totalCount)
                _orderBookList.postValue(shoppingCartTableList)
            }

            // 가져온 리스트가 비어있지 않다면
            _isLoadOrderDataList.postValue(shoppingCartTableList.isNotEmpty())
        }
    }

    // shoppingCartModel -> userInquiryModel로 변환
    private fun transformShoppingCartList(
        orderBookList: List<ShoppingCartModel>
    ): List<OrderInquiryModel> {
        return orderBookList.map { shoppingCartModel ->
            OrderInquiryModel(
                orderInquiryPrice = shoppingCartModel.shoppingCartSellingPrice,
                orderInquiryQuality = shoppingCartModel.shoppingCartQuality,
                orderInquiryISBN = shoppingCartModel.shoppingCartISBN,
                orderInquiryUserToken = _userToken.value!!,
                orderInquiryDeliveryResult = 0,
                orderInquiryTime = System.currentTimeMillis(),
                orderInquiryNumber = generateRandomString(),
                orderInquiryTitle = shoppingCartModel.shoppingCartBookTitle,
                orderInquiryAuthor = shoppingCartModel.shoppingCartBookAuthor,
                orderInquiryState = 0,
                orderInquiryName = _username.value!!,
                orderInquiryPhoneNumber = _userPhoneNumber.value!!,
                orderInquiryAddress = _userAddress.value!!
            )
        }
    }

    // 랜덤 으로 알파벳 한글자 + 나노숫자 7숫자
    private fun generateRandomString(): String {
        // 알파벳 한자리 (a~z)
        val alphabet = ('a'..'z').random()

        // nanoTime의 끝 7자리
        val nanoTimeEnd = System.nanoTime().toString().takeLast(7)

        // 알파벳과 nanoTime의 끝 7자리를 결합
        return "$alphabet$nanoTimeEnd"
    }

    fun saveOrderDataToOrderInquiryTable() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    bookOrderRepository.saveShoppingCartDataToOrderInquiryTable(
                        transformShoppingCartList(_orderBookList.value!!)
                    )
                    // 성공적으로 저장된 경우
                    _isSuccessOrder.postValue(Pair(true,0))
                } catch (e: Exception) {
                    // 예외 발생 시 처리할 내용
                    _isSuccessOrder.postValue(Pair(true,1))
                }
                // true, 0 -> 성공적으로 저장되었음
                // true -> 오류가 발생하여 구매 실패
            }
        }
    }
}