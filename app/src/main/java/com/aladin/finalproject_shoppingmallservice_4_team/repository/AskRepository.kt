package com.aladin.finalproject_shoppingmallservice_4_team.repository

import android.net.Uri
import com.aladin.finalproject_shoppingmallservice_4_team.model.AskModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AskRepository @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val fireStorage: FirebaseStorage
) {

    // Firestore에 문의 저장
    suspend fun saveAsk(askModel: AskModel): Boolean {
        return try {
            fireStore.collection("AskTable").add(askModel).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Firebase Storage에 첨부파일 업로드
    suspend fun uploadAttachment(uri: Uri): String? {
        return try {
            val fileName = "attachments/${System.currentTimeMillis()}_${uri.lastPathSegment}"
            val storageReference = fireStorage.reference.child(fileName)
            val uploadTask = storageReference.putFile(uri).await()
            storageReference.downloadUrl.await().toString()
        } catch (e: Exception) {
            null
        }
    }
}