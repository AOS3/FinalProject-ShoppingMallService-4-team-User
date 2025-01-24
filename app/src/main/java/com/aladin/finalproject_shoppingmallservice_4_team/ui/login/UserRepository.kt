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
                    // userDocumentId = document.id // Firestore 문서 ID
                    this.userId = data["userId"] as? String ?: "" // Firestore 필드 "userId" 매핑
                    userPw = data["userPw"] as? String ?: "" // Firestore 필드 "userPw" 매핑
                    userName = data["userName"] as? String ?: "" // Firestore 필드 "userName" 매핑
                    userAddress = data["userAddress"] as? String ?: ""
                    userPhoneNumber = data["userPhoneNumber"] as? String ?: ""
                    userToken = data["userToken"] as? String ?: ""
                    userState = (data["userState"] as? Long)?.toInt() ?: 0
                    userAutoLoginToken = data["userAutoLoginToken"] as? String ?: ""
                    userJoinTime = data["userJoinTime"] as? Long ?: 0L
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

    // 유저 ID 중복 확인 메서드
    suspend fun isUserIdAvailable(userId: String): Boolean {
        val collectionReference = firebaseFireStore.collection("UserTable")
        val result = collectionReference.whereEqualTo("userId", userId).get().await()
        return result.isEmpty // Firestore에 해당 아이디가 없으면 true 반환
    }

    // Firebase에 사용자 정보 저장
    suspend fun saveUser(user: UserModel):Boolean{
        return try {
            firebaseFireStore.collection("UserTable")
                .add(user)
                .await()
            true
        } catch (e:Exception){
            false
        }
    }

    // 이름과 번호를 통해 Id 찾기
    suspend fun findUserIdByNameAndPhone(userName: String, phoneNumber: String): String? {
        return try {
            val collectionReference = firebaseFireStore.collection("UserTable")
            val result = collectionReference
                .whereEqualTo("userName", userName)
                .whereEqualTo("userPhoneNumber", phoneNumber)
                .get()
                .await()

            if (!result.isEmpty) {
                result.documents.first().getString("userId") // userId 필드를 반환
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("FindUserError", "Error finding user by name and phone: ${e.message}", e)
            null
        }
    }

    suspend fun updateUserPassword(userId: String, newPassword: String): Boolean {
        return try {
            val collectionReference = firebaseFireStore.collection("UserTable")
            val querySnapshot = collectionReference.whereEqualTo("userId", userId).get().await()

            if (!querySnapshot.isEmpty) {
                val documentId = querySnapshot.documents.first().id
                collectionReference.document(documentId).update("userPw", newPassword).await()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("UpdatePasswordError", "Error updating password: ${e.message}", e)
            false
        }
    }

    // 자동 로그인 토큰 업데이트
    suspend fun updateAutoLoginToken(userId: String, autoLoginToken: String): Boolean {
        return try {
            val collectionReference = firebaseFireStore.collection("UserTable")
            val querySnapshot = collectionReference.whereEqualTo("userId", userId).get().await()

            if (!querySnapshot.isEmpty) {
                val documentId = querySnapshot.documents.first().id
                collectionReference.document(documentId).update("userAutoLoginToken", autoLoginToken).await()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("UpdateAutoLoginTokenError", "Error updating auto login token: ${e.message}", e)
            false
        }
    }

    suspend fun validateAutoLoginToken(autoLoginToken: String): UserModel? {
        return try {
            val collectionReference = firebaseFireStore.collection("UserTable")
            val result = collectionReference.whereEqualTo("userAutoLoginToken", autoLoginToken).get().await()

            if (!result.isEmpty) {
                // Firestore 문서 데이터를 직접 매핑
                val document = result.documents.first()
                val data = document.data ?: return null

                UserModel().apply {
                    userId = data["userId"] as? String ?: ""
                    userPw = data["userPw"] as? String ?: ""
                    userName = data["userName"] as? String ?: ""
                    userAddress = data["userAddress"] as? String ?: ""
                    userPhoneNumber = data["userPhoneNumber"] as? String ?: ""
                    userToken = data["userToken"] as? String ?: ""
                    userState = (data["userState"] as? Long)?.toInt() ?: 0
                    userAutoLoginToken = data["userAutoLoginToken"] as? String ?: ""
                    userJoinTime = data["userJoinTime"] as? Long ?: 0L
                }
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("test100", "Error: ${e.message}", e)
            null
        }
    }

    suspend fun updateUserAutoLoginToken(userId: String, autoLoginToken: String): Boolean {
        return try {
            val collectionReference = firebaseFireStore.collection("UserTable")
            val querySnapshot = collectionReference.whereEqualTo("userId", userId).get().await()

            if (!querySnapshot.isEmpty) {
                val documentId = querySnapshot.documents.first().id
                collectionReference.document(documentId).update("userAutoLoginToken", autoLoginToken).await()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "자동 로그인 토큰 업데이트 실패: ${e.message}")
            false
        }
    }

    // 정보를 업데이트
    suspend fun updateUserInfo(userId: String, newName: String, newAddress: String): Boolean {
        return try {
            val querySnapshot = firebaseFireStore.collection("UserTable")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val documentId = querySnapshot.documents.first().id
                firebaseFireStore.collection("UserTable").document(documentId)
                    .update(mapOf("userName" to newName, "userAddress" to newAddress))
                    .await()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getUserById(userId: String): UserModel? {
        return try {
            val collectionReference = firebaseFireStore.collection("UserTable")
            val querySnapshot = collectionReference.whereEqualTo("userId", userId).get().await()

            if (!querySnapshot.isEmpty) {
                val document = querySnapshot.documents.first()
                val data = document.data
                if (data != null) {
                    UserModel().apply {
                        this.userId = data["userId"] as? String ?: ""
                        this.userPw = data["userPw"] as? String ?: ""
                        this.userName = data["userName"] as? String ?: ""
                        this.userAddress = data["userAddress"] as? String ?: ""
                        this.userPhoneNumber = data["userPhoneNumber"] as? String ?: ""
                        this.userToken = data["userToken"] as? String ?: ""
                        this.userState = (data["userState"] as? Long)?.toInt() ?: 0
                        this.userAutoLoginToken = data["userAutoLoginToken"] as? String ?: ""
                        this.userJoinTime = data["userJoinTime"] as? Long ?: 0L
                    }
                } else null
            } else null
        } catch (e: Exception) {
            Log.e("UserRepository", "getUserById 오류: ${e.message}", e)
            null
        }
    }
}