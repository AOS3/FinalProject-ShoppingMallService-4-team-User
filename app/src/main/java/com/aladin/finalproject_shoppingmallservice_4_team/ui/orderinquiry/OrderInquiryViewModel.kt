package com.aladin.finalproject_shoppingmallservice_4_team.ui.orderinquiry

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aladin.finalproject_shoppingmallservice_4_team.model.OrderInquiryModel
import com.aladin.finalproject_shoppingmallservice_4_team.repository.OrderInquiryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderInquiryViewModel @Inject constructor(
    private val orderInquiryRepository: OrderInquiryRepository
): ViewModel() {

    private val _inquiryList = MutableLiveData<MutableList<OrderInquiryModel>>()
    val inquiryList: LiveData<MutableList<OrderInquiryModel>> get() = _inquiryList

    private val _isLoadInquiryDataList = MutableLiveData<Boolean>(false)
    val isLoadInquiryDataList: LiveData<Boolean> get() = _isLoadInquiryDataList

    fun loadData(userToken: String) = viewModelScope.launch {
        runCatching {
            orderInquiryRepository.loadOrderInquiryData(userToken)
        }.onSuccess {
            _inquiryList.value = it
            Log.d("test100", it.toString())
            _isLoadInquiryDataList.value = true
        }.onFailure {
            Log.d("OrderInquiryViewModel", "error: $it")
        }
    }
}