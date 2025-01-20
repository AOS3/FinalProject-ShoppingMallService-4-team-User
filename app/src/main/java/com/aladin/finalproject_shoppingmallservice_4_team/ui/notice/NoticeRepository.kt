package com.aladin.finalproject_shoppingmallservice_4_team.ui.notice

import android.util.Log
import com.aladin.apiTestApplication.network.AladdinApiService
import com.aladin.finalproject_shoppingmallservice_4_team.model.NoticeModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoticeRepository @Inject constructor(private val firebaseFireStore: FirebaseFirestore,private val apiService: AladdinApiService) {

    // 공지사항 목록을 가져오는 메서
    suspend fun gettingNoticeDataList(): List<NoticeModel> {
        val collectionReference = firebaseFireStore.collection("NoticeTable")

        // noticeState가 0인 공지사항만 필터링
        val querySnapshot: QuerySnapshot = collectionReference
            .whereEqualTo("noticeState", 0)
            .get()
            .await()

        // 쿼리 결과를 처리
        return querySnapshot.documents.mapNotNull { document ->
            document.toObject(NoticeModel::class.java)
        }
    }
}