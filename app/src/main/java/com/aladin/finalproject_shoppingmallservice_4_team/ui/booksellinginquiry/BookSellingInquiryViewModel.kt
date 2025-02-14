package com.aladin.finalproject_shoppingmallservice_4_team.ui.booksellinginquiry

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aladin.finalproject_shoppingmallservice_4_team.model.SellingInquiryModel
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class BookSellingInquiryViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _items = MutableLiveData<List<SellingInquiryModel>>()
    val items: LiveData<List<SellingInquiryModel>> = _items

    private val _documentCount = MutableLiveData<Int>()
    val documentCount: LiveData<Int> = _documentCount // 문서 개수 LiveData

    fun loadSellingInquiries(userToken: String) {
        if (userToken.isEmpty()) {
            Log.e("BookSellingInquiryViewModel", "User token is empty. Cannot load data.")
            _items.postValue(emptyList()) // 빈 데이터로 업데이트
            _documentCount.postValue(0) // 개수도 0으로 업데이트
            return
        }

        firestore.collection("SellingInquiryTable")
            .whereEqualTo("sellingInquiryUserToken", userToken) // 로그인된 사용자에 해당하는 데이터만 가져오기
            .get()
            .addOnSuccessListener { result ->
                // 문서 개수 계산
                val count = result.size()
                _documentCount.postValue(count) // LiveData로 개수 업데이트

                // Firestore 데이터 매핑
                val data = result.map { document ->
                    SellingInquiryModel(
                        sellingInquiryPrice = document.getLong("sellingInquiryPrice")?.toInt() ?: 0,
                        sellingInquiryFinalPrice = document.getLong("sellingInquiryFinalPrice")?.toInt() ?: 0,
                        sellingInquiryQuality = document.getLong("sellingInquiryQuality")?.toInt() ?: -1,
                        sellingInquiryISBN = document.getString("sellingInquiryISBN").orEmpty(),
                        sellingInquiryUserToken = document.getString("sellingInquiryUserToken").orEmpty(),
                        sellingInquiryApprovalResult = document.getLong("sellingInquiryApprovalResult")?.toInt() ?: -1,
                        sellingInquiryTime = document.getLong("sellingInquiryTime") ?: 0L,
                        sellingInquiryShippingMethod = document.getLong("sellingInquiryShippingMethod")?.toInt() ?: -1,
                        sellingInquiryNonPurchaseableMethod = document.getLong("sellingInquiryNonPurchaseableMethod")?.toInt() ?: -1,
                        sellingInquiryDepositor = document.getString("sellingInquiryDepositor").orEmpty(),
                        sellingInquiryBankName = document.getString("sellingInquiryBankName").orEmpty(),
                        sellingInquiryBankAccountNumber = document.getString("sellingInquiryBankAccountNumber").orEmpty(),
                        sellingInquiryBookName = document.getString("sellingInquiryBookName").orEmpty(),
                        sellingInquiryBookAuthor = document.getString("sellingInquiryBookAuthor").orEmpty(),
                        sellingInquiryState = document.getLong("sellingInquiryState")?.toInt() ?: -1,
                        sellingInquiryChoiceQuality = document.getLong("sellingInquiryChoiceQuality")?.toInt() ?: -1,
                        documentId = document.id // Firestore 문서 ID
                    )
                }

                _items.postValue(data) // LiveData로 데이터 업데이트

                Log.d("BookSellingInquiryViewModel", "총 문서 개수: $count")
            }
            .addOnFailureListener { e ->
                Log.e("BookSellingInquiryViewModel", "데이터 로드 실패: ${e.message}")
            }
    }
}