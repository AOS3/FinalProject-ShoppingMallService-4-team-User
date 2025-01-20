package com.aladin.finalproject_shoppingmallservice_4_team.ui.changePw

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ChangePwRepository {

    // 현재 비밀번호 가져오는 함수
    fun isUserPasswordMatch(currentPassword: String): Boolean {
        return true
    }

    // 비밀번호 변경 함수
    suspend fun changeUserPassword(userDocumentId: String, newPassword: String) {
        val firestore = FirebaseFirestore.getInstance()
        val collectionReference = firestore.collection("UserTable")
        val documentReference = collectionReference.document(userDocumentId)

        val updateMap = mapOf(
            "userPw" to newPassword
        )

        documentReference.update(updateMap).await()
    }
}