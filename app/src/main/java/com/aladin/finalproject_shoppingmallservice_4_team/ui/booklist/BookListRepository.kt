package com.aladin.finalproject_shoppingmallservice_4_team.ui.booklist

import android.util.Log
import com.aladin.apiTestApplication.dto.BookItem
import com.aladin.apiTestApplication.dto.RecommendBookItem
import com.aladin.apiTestApplication.network.AladdinApiService
import com.aladin.finalproject_shoppingmallservice_4_team.BuildConfig
import com.aladin.finalproject_shoppingmallservice_4_team.model.BookListModel
import com.aladin.finalproject_shoppingmallservice_4_team.model.NoticeModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookListRepository @Inject constructor(
    val apiService: AladdinApiService,
    private val firebaseFireStore: FirebaseFirestore
) {

    // 알라딘 책 추천 정보 가져오기
    suspend fun gettingAladdinRecommendBook(query: String): List<RecommendBookItem> {
        val response = apiService.recommendedBooks(
            apiKey = BuildConfig.API_KEY,
            queryType = query
        )
        return response.items
    }

    // FireBase Db에서 중고 재고 내역 가져오기
    suspend fun gettingUsedBookInventory(): Pair<List<BookListModel>, List<String>> {
        val collectionReference = firebaseFireStore.collection("UsedBookInventoryTable")

        // 안팔린 책들만 필터링
        val querySnapshot: QuerySnapshot = collectionReference
            .whereEqualTo("usedBookState", 0)
            .get()
            .await()

        // 쿼리 결과에서 ISBN 리스트 추출
        val usedBookISBNList = querySnapshot.documents.mapNotNull { document ->
            document.toObject(BookListModel::class.java)?.usedBookISBN
        }

        // ISBN 리스트를 사용해 API 호출하여 cover 정보 가져오기
        val coverList = searchUsedBookCoverData(usedBookISBNList)

        // 쿼리 결과에서 BookListModel을 추출
        val bookList = querySnapshot.documents.mapNotNull { document ->
            document.toObject(BookListModel::class.java)
        }

        // Pair로 bookList와 coverList 반환
        return Pair(bookList, coverList)
    }

    // 책 검색 API 호출 함수
    suspend fun searchUsedBookCoverData(queries: List<String>): List<String> {
        val allCovers = mutableListOf<String>()

        for (query in queries) {
            // API 호출
            val response = apiService.searchBooks(BuildConfig.API_KEY, query)
            // cover만 추출하여 리스트에 추가
            allCovers.addAll(response.items.map { it.cover })
        }

        return allCovers
    }
}