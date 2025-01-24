package com.aladin.finalproject_shoppingmallservice_4_team.ui.orderinquiry

import androidx.lifecycle.ViewModel
import com.aladin.finalproject_shoppingmallservice_4_team.repository.OrderInquiryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OrderInquiryViewModel @Inject constructor(
    private val orderInquiryRepository: OrderInquiryRepository
): ViewModel() {

}