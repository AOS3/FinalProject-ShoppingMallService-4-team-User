package com.aladin.finalproject_shoppingmallservice_4_team.ui.sellinglastpage

import androidx.lifecycle.ViewModel
import com.aladin.finalproject_shoppingmallservice_4_team.model.SellingInquiryModel
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore

class SellingLastPageViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    fun saveBooksToFirestore(
        shippingMethod: Int,
        nonPurchaseableMethod: Int,
        depositor: String,
        bankName: String,
        accountNumber: String,
        userToken: String, // 전달받은 userToken
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestore.collection("SellingCartTable")
            .whereEqualTo("sellingCartState", 1) // 상태가 1인 도서만 가져오기
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    onFailure(Exception("판매 승인 대기 중인 상품이 없습니다."))
                    return@addOnSuccessListener
                }

                val currentTime = System.currentTimeMillis()

                // 도서별로 SellingInquiryModel 생성 및 저장
                val tasks = documents.map { document ->
                    val isbn = document.getString("sellingCartISBN").orEmpty()
                    val title = document.getString("sellingCartBookTitle").orEmpty()
                    val author = document.getString("sellingCartBookAuthor").orEmpty()
                    val price = document.getLong("sellingCartSellingPrice")?.toInt() ?: 0
                    val cartUserToken = document.getString("sellingCartUserToken").orEmpty() // userToken 가져오기

                    if (isbn.isEmpty() || title.isEmpty() || cartUserToken.isEmpty()) {
                        onFailure(Exception("도서 정보 또는 사용자 정보가 누락되었습니다."))
                        return@map null
                    }

                    val inquiry = SellingInquiryModel(
                        sellingInquiryISBN = isbn,
                        sellingInquiryBookName = title,
                        sellingInquiryBookAuthor = author,
                        sellingInquiryPrice = price,
                        sellingInquiryShippingMethod = shippingMethod,
                        sellingInquiryNonPurchaseableMethod = nonPurchaseableMethod,
                        sellingInquiryDepositor = depositor,
                        sellingInquiryBankName = bankName,
                        sellingInquiryBankAccountNumber = accountNumber,
                        sellingInquiryUserToken = cartUserToken, // cartUserToken 사용
                        sellingInquiryTime = currentTime
                    )

                    firestore.collection("SellingInquiryTable").add(inquiry)
                }

                // 모든 저장 작업 완료 후 콜백 호출
                Tasks.whenAllComplete(tasks.filterNotNull()) // null 값 필터링
                    .addOnSuccessListener { results ->
                        val failedTasks = results.filter { !it.isSuccessful }
                        if (failedTasks.isNotEmpty()) {
                            onFailure(Exception("일부 데이터를 저장하지 못했습니다."))
                        } else {
                            onSuccess()
                        }
                    }
                    .addOnFailureListener { e ->
                        onFailure(e)
                    }
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }



    fun deleteBooksFromSellingCart(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("SellingCartTable")
            .whereEqualTo("sellingCartState", 1) // 상태가 1인 도서만 가져오기
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    onFailure(Exception("삭제할 도서가 없습니다."))
                    return@addOnSuccessListener
                }

                val batch = firestore.batch()

                // 상태가 1인 모든 도서를 삭제
                documents.forEach { document ->
                    batch.delete(document.reference)
                }

                batch.commit()
                    .addOnSuccessListener {
                        onSuccess() // 성공 콜백 호출
                    }
                    .addOnFailureListener { e ->
                        onFailure(e) // 실패 콜백 호출
                    }
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }
}
