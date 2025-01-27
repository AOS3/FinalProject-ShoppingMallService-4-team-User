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

    // 유저 토큰값
    private val _userToken = MutableLiveData<String>()

    // 유저 이름
    private val _username = MutableLiveData<String>()

    // 유저 전화번호
    private val _userPhoneNumber = MutableLiveData<String>()

    // 유저 주소
    private val _userAddress = MutableLiveData<String>()
    val userAddress: LiveData<String> get() = _userAddress

    // 현재 시간 값
    private val _todayCurrentTime = System.currentTimeMillis()
    val todayCurrentTime: Long get() = _todayCurrentTime

    // 사용자 책 구매 갯수 리스트
    private val _inquiryBookCountList = mutableListOf<String>()
    val inquiryBookCountList: List<String> get() = _inquiryBookCountList

    // 사용자 책 구매 커버 사진 리스트
    private val _inquiryBookCoverList = mutableListOf<String>()
    val inquiryBookCoverList: List<String> get() = _inquiryBookCoverList

    // 명세서 정보 리스트
    private val _userInquiryBookList = MutableLiveData<List<OrderInquiryModel>>()
    val userInquiryBookList: LiveData<List<OrderInquiryModel>> get() = _userInquiryBookList

    // 명세서 총 수량
    private val _userInquiryBookTotalCount = mutableListOf<Int>()
    val userInquiryBookTotalCount: List<Int> get() = _userInquiryBookTotalCount

    // 명세서 총 가격
    private val _userInquiryBookTotalPrice = mutableListOf<Int>()
    val userInquiryBookTotalPrice: List<Int> get() = _userInquiryBookTotalPrice

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
                val totalPrice =
                    shoppingCartTableList.sumOf { (it.shoppingCartSellingPrice) * (it.shoppingCartBookQualityCount) }
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
            orderBookList.forEach { shoppingCartModel ->
                // 책이름/책 갯수로 넣는다.
                _inquiryBookCountList.add("${shoppingCartModel.shoppingCartBookTitle}/${shoppingCartModel.shoppingCartBookQualityCount}")
                // 책이름/책 커버 링크로 넣는다.
                _inquiryBookCoverList.add("${shoppingCartModel.shoppingCartBookTitle}/${shoppingCartModel.shoppingCartBookCoverImage}")
            }
            OrderInquiryModel(
                orderInquiryPrice = shoppingCartModel.shoppingCartSellingPrice,
                orderInquiryQuality = shoppingCartModel.shoppingCartQuality,
                orderInquiryISBN = shoppingCartModel.shoppingCartISBN,
                orderInquiryUserToken = _userToken.value!!,
                orderInquiryDeliveryResult = 0,
                orderInquiryTime = todayCurrentTime,
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

    // 장바구니 데이터 저장 함수
    fun saveOrderDataToOrderInquiryTable() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    bookOrderRepository.saveShoppingCartDataToOrderInquiryTable(
                        transformShoppingCartList(_orderBookList.value!!)
                    )
                    // 성공적으로 저장된 경우
                    _isSuccessOrder.postValue(Pair(true, 0))
                } catch (e: Exception) {
                    // 예외 발생 시 처리할 내용
                    _isSuccessOrder.postValue(Pair(true, 1))
                }
                // true, 0 -> 성공적으로 저장되었음
                // true, 1 -> 오류가 발생하여 구매 실패
            }
        }
    }

    // 주문 내역 데이터 가져오기
    fun gettingUserInquiryDataList(userToken: String, userBuyTime: Long) {
        viewModelScope.launch {
            val userInquiryDataList = withContext(Dispatchers.IO) {
                bookOrderRepository.gettingOrderInquiryDataFromFirBaseStore(userToken, userBuyTime)
            }
            userInquiryDataList.forEach { userInquiryDataList->
                // 토탈 가격
                _userInquiryBookTotalPrice += userInquiryDataList.orderInquiryPrice
            }
            _userInquiryBookList.postValue(userInquiryDataList)
        }
    }

    // 데이터 가공처리
    fun processBookDataFromArguments(bookCoverList: ArrayList<String>?): MutableMap<String, Pair<String, Int>> {
        val bookMap = mutableMapOf<String, Pair<String, Int>>()

        bookCoverList?.forEach { data ->
            // 책 제목과 링크를 구분하는 정규 표현식
            val regex = "(.*?)(/.*)".toRegex()  // 책 제목과 이미지 링크를 분리
            val matchResult = regex.matchEntire(data.trim())

            if (matchResult != null) {
                val bookTitle =
                    matchResult.groupValues[1].trim().removeSuffix(",")  // 제목에서 불필요한 ',' 제거
                var bookImageLink = matchResult.groupValues[2].trim()

                // 이미지 링크에서 맨 앞 글자와 '/' 제거
                if (bookImageLink.startsWith("/")) {
                    bookImageLink = bookImageLink.substring(1)
                }

                // 기존에 같은 제목이 있으면 갯수 증가, 없으면 새로 추가
                if (bookMap.containsKey(bookTitle)) {
                    val (link, count) = bookMap[bookTitle]!!
                    bookMap[bookTitle] = Pair(link, count + 1)
                } else {
                    bookMap[bookTitle] = Pair(bookImageLink, 1)
                }
            } else {
                Log.e("asd", "잘못된 데이터 포맷: $data")
            }
        }

        // 전체 수량값 합산
        bookMap.forEach { (_, pair) ->
            _userInquiryBookTotalCount += pair.second
        }

        return bookMap
    }
}