package com.aladin.finalproject_shoppingmallservice_4_team.ui.bookdetail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aladin.finalproject_shoppingmallservice_4_team.model.BookCountModel
import com.aladin.finalproject_shoppingmallservice_4_team.model.ShoppingCartModel
import com.aladin.finalproject_shoppingmallservice_4_team.repository.BookDetailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailBottomViewModel @Inject constructor(
    private val bookDetailRepository: BookDetailRepository
): ViewModel() {

    val bookCount = MutableLiveData<List<BookCountModel>>()

    private val _isLoadBookCountList = MutableLiveData<Boolean>(false)
    val isLoadBookCountList: LiveData<Boolean> get() = _isLoadBookCountList

    private val _isAddShoppingCart = MutableLiveData<Boolean>(false)
    val isAddShoppingCart: LiveData<Boolean> get() = _isAddShoppingCart

    private val _highStock = MutableLiveData<Int>()
    val highStock: LiveData<Int> get() = _highStock
    private val _normalStock = MutableLiveData<Int>()
    val normalStock: LiveData<Int> get() = _normalStock
    private val _lowStock = MutableLiveData<Int>()
    val lowStock: LiveData<Int> get() = _lowStock

    val highCount = MutableLiveData<Int>()
    val normalCount = MutableLiveData<Int>()
    val lowCount = MutableLiveData<Int>()

    private val _highPriceTotal = MutableLiveData<Int>(0)
    val highPriceTotal: LiveData<Int> get() = _highPriceTotal
    private val _normalPriceTotal = MutableLiveData<Int>(0)
    val normalPriceTotal: LiveData<Int> get() = _normalPriceTotal
    private val _lowPriceTotal = MutableLiveData<Int>(0)
    val lowPriceTotal: LiveData<Int> get() = _lowPriceTotal
    private val _total = MutableLiveData<Int>(0)
    val total: LiveData<Int> get() = _total

    fun loadBookCount(isbn: String) = viewModelScope.launch {
        runCatching {
            bookDetailRepository.loadUserBookCount(isbn)
        }.onSuccess {
            bookCount.value = it
            highCount.value = if(it.find { it.bookCountQuality == 0 } != null) it.find { it.bookCountQuality == 0 }!!.bookCountTotal else 0
            normalCount.value = if(it.find { it.bookCountQuality == 1 } != null) it.find { it.bookCountQuality == 1 }!!.bookCountTotal else 0
            lowCount.value = if(it.find { it.bookCountQuality == 2 } != null) it.find { it.bookCountQuality == 2 }!!.bookCountTotal else 0
            _isLoadBookCountList.value = true
        }.onFailure {
            Log.d("bookDetail", "error: $it")
        }
    }

    fun updateTotal() {
        val high = if(highPriceTotal.value != 0)  {
            highPriceTotal.value!!
        } else {
            0
        }
        val normal = if(normalPriceTotal.value != 0)  {
            normalPriceTotal.value!!
        } else  {
            0
        }
        val low = if(lowPriceTotal.value != 0)  {
            lowPriceTotal.value!!
        } else  {
            0
        }

        _total.value = high + normal + low
    }

    fun updateHighPriceTotal(price: Int) {
        _highPriceTotal.value = price
    }

    fun updateNormalPriceTotal(price: Int) {
        _normalPriceTotal.value = price
    }

    fun updateLowPriceTotal(price: Int) {
        _lowPriceTotal.value = price
    }

    // 수량 업데이트
    fun updateHighStock(increase: Boolean) {
        val stock = _highStock.value ?: 1
        val updateStock = if(increase) {
            (stock + 1).coerceAtMost(highCount.value!!)
        }
        else {
            (stock - 1).coerceAtLeast(0)
        }
        _highStock.value = updateStock
    }

    // 수량 업데이트
    fun updateNormalStock(increase: Boolean) {
        val stock = _normalStock.value ?: 1
        val updateStock = if(increase) {
            (stock + 1).coerceAtMost(normalCount.value!!)
        }
        else {
            (stock - 1).coerceAtLeast(0)
        }
        _normalStock.value = updateStock
    }

    // 수량 업데이트
    fun updateLowStock(increase: Boolean) {
        val stock = _lowStock.value ?: 1
        val updateStock = if(increase) {
            (stock + 1).coerceAtMost(lowCount.value!!)
        }
        else {
            (stock - 1).coerceAtLeast(0)
        }
        _lowStock.value = updateStock
    }

    fun updateShoppingCartData(isbn: String, userToken: String) = viewModelScope.launch {
        runCatching {
            if(highPriceTotal.value != 0) {
                val highItem = ShoppingCartModel(
                    shoppingCartSellingPrice = highPriceTotal.value!!,
                    shoppingCartQuality = 0,
                    shoppingCartISBN = isbn,
                    shoppingCartBookQualityCount = highStock.value!!,
                    shoppingCartTime = System.currentTimeMillis(),
                    shoppingCartUserToken = userToken,
                )
                bookDetailRepository.addShoppingCartItem(highItem)
            }
            if(normalPriceTotal.value != 0) {
                val normalItem = ShoppingCartModel(
                    shoppingCartSellingPrice = normalPriceTotal.value!!,
                    shoppingCartQuality = 1,
                    shoppingCartISBN = isbn,
                    shoppingCartBookQualityCount = normalStock.value!!,
                    shoppingCartTime = System.currentTimeMillis(),
                    shoppingCartUserToken = userToken,
                )
                bookDetailRepository.addShoppingCartItem(normalItem)
            }
            if(lowPriceTotal.value != 0) {
                val lowItem = ShoppingCartModel(
                    shoppingCartSellingPrice = lowPriceTotal.value!!,
                    shoppingCartQuality = 2,
                    shoppingCartISBN = isbn,
                    shoppingCartBookQualityCount = lowStock.value!!,
                    shoppingCartTime = System.currentTimeMillis(),
                    shoppingCartUserToken = userToken,
                )
                bookDetailRepository.addShoppingCartItem(lowItem)
            }
        }.onSuccess {
            _isAddShoppingCart.value = true
        }.onFailure {
            Log.d("DetailBottomSheet", "error: $it")
        }
    }
}