package com.aladin.finalproject_shoppingmallservice_4_team.repository

import com.aladin.finalproject_shoppingmallservice_4_team.model.NotificationModel
import com.google.firebase.firestore.FirebaseFirestore
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
                                              .whereEqualTo("notificationState", 0)
                                              .get()
                                              .await()

        // 쿼리 결과에서 ShoppingCartModel을 추출
        val notificationList = notification.documents.mapNotNull { document ->
            document.toObject(NotificationModel::class.java)
        }

        return notificationList.toMutableList()
    }

    // 리스트 삭제
    suspend fun deleteNotificationData(deleteList: List<NotificationModel>) {
        val collectionReference = firestore.collection("NotificationTable")

        deleteList.forEach {
            // Firestore에서 userToken과 title에 해당하는 문서 찾기
            val notification = collectionReference
                .whereEqualTo("notificationUserToken", it.notificationUserToken)
                .whereEqualTo("notificationTitle", it.notificationTitle)
                .get()
                .await()

            // 해당 문서가 존재하면 삭제
            for (document in notification.documents) {
                collectionReference.document(document.id).update("notificationState", 1).await()
            }
        }

    }

    // 리스트 삭제
    suspend fun deleteNotificationOneData(deleteData: NotificationModel) {
        val collectionReference = firestore.collection("NotificationTable")

        // Firestore에서 userToken과 title에 해당하는 문서 찾기
        val notification = collectionReference
            .whereEqualTo("notificationUserToken", deleteData.notificationUserToken)
            .whereEqualTo("notificationTitle", deleteData.notificationTitle)
            .whereEqualTo("notificationTime", deleteData.notificationTime)
            .get()
            .await()

        // 해당 문서가 존재하면 삭제
        for (document in notification.documents) {
            collectionReference.document(document.id).update("notificationState", 1).await()
        }
    }

    // 리스트 봤다고 표시하기
    suspend fun seeNotificationOneData(seeData: NotificationModel) {
        val collectionReference = firestore.collection("NotificationTable")

        // Firestore에서 userToken과 title에 해당하는 문서 찾기
        val notification = collectionReference
            .whereEqualTo("notificationUserToken", seeData.notificationUserToken)
            .whereEqualTo("notificationTitle", seeData.notificationTitle)
            .whereEqualTo("notificationTime", seeData.notificationTime)
            .get()
            .await()

        // 해당 문서가 존재하면 삭제
        for (document in notification.documents) {
            collectionReference.document(document.id).update("notificationSee", 1).await()
        }

    }
}