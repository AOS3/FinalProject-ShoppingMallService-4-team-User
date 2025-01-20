package com.aladin.finalproject_shoppingmallservice_4_team.ui.login

import android.util.Log
import com.aladin.finalproject_shoppingmallservice_4_team.model.UserModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(private val firebaseFireStore: FirebaseFirestore){

    suspend fun selectUserDataByUserId(userId: String): List<UserModel> {
        val collectionReference = firebaseFireStore.collection("UserTable")
        Log.d("test100", "Firestore 쿼리 실행 - userId: $userId")

        val result = collectionReference.whereEqualTo("userId", userId).get().await()

        // Firestore 데이터를 직접 매핑
        val userList = result.documents.mapNotNull { document ->
            val data = document.data
            if (data != null) {
                UserModel().apply {
                    userDocumentId = document.id // Firestore 문서 ID
                    this.userId = data["userId"] as? String ?: "" // Firestore 필드 "userId" 매핑
                    userPw = data["userPw"] as? String ?: "" // Firestore 필드 "userPw" 매핑
                    userName = data["userName"] as? String ?: "" // Firestore 필드 "userName" 매핑
                    userAddress = data["userAddress"] as? String ?: ""
                    userPhoneNumber = data["userPhoneNumber"] as? String ?: ""
                    userToken = data["userToken"] as? String ?: ""
                    userState = (data["userState"] as? Long)?.toString() ?: "0"
                    userAutoLoginToken = data["userAutoLoginToken"] as? String ?: ""
                    userJoinTime = data["userJoinTime"] as? String ?: ""
                }
            } else {
                null
            }
        }

        Log.d("test100", "Firestore에서 매핑된 사용자 리스트: ${userList.map { user ->
            "userId=${user.userId}, userPw=${user.userPw}, userName=${user.userName}"
        }}")

        return userList
    }
}