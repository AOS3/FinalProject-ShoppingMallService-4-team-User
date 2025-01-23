package com.aladin.finalproject_shoppingmallservice_4_team.ui.changePw

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChangePwRepository @Inject constructor(private val firebaseFireStore: FirebaseFirestore) {
    suspend fun changeUserPassword(userToken: String, password: String) {
        val collectionReference = firebaseFireStore.collection("UserTable")

        // Firestore에서 해당 문서 가져오기
        val document = collectionReference
            .whereEqualTo("userToken", userToken)
            .get()
            .await()
            .documents
            .firstOrNull()

        // 문서가 존재하면 업데이트
        document?.reference?.update("userPw", password)?.await()
    }
}