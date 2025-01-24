package com.aladin.finalproject_shoppingmallservice_4_team.repository

import com.aladin.finalproject_shoppingmallservice_4_team.model.OrderInquiryModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderInquiryRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    suspend fun loadOrderInquiryData(userToken: String): MutableList<OrderInquiryModel> {
        val collectionReference = firestore.collection("OrderInquiryTable")

        val inquiryData = collectionReference.whereEqualTo("orderInquiryUserToken", userToken).get().await()

        val inquiryList = inquiryData.documents.mapNotNull { document ->
            document.toObject(OrderInquiryModel::class.java)
        }

        return inquiryList.toMutableList()
    }


}