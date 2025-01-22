package com.aladin.finalproject_shoppingmallservice_4_team.repository

import com.aladin.finalproject_shoppingmallservice_4_team.model.NotificationModel
import com.aladin.finalproject_shoppingmallservice_4_team.model.ShoppingCartModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun loadNotificationData(userToken: String): MutableList<NotificationModel> {
        val collectionReference = firestore.collection("NotificationTable")

        val notification = collectionReference.whereEqualTo("notificationUserToken", userToken)
                                               .get()
                                               .await()

        // 쿼리 결과에서 ShoppingCartModel을 추출
        val notificationList = notification.documents.mapNotNull { document ->
            document.toObject(NotificationModel::class.java)
        }

        return notificationList.toMutableList()
    }
}