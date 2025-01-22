package com.aladin.finalproject_shoppingmallservice_4_team.repository

import com.aladin.apiTestApplication.dto.BookItem
import com.aladin.apiTestApplication.network.AladdinApiService
import com.aladin.finalproject_shoppingmallservice_4_team.BuildConfig
import com.aladin.finalproject_shoppingmallservice_4_team.model.SellingCartModel
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookDetailRepository @Inject constructor(
    private val apiService: AladdinApiService,
    private val firestore: FirebaseFirestore,
) {
    // 책 검색 API 호출 함수
    suspend fun searchBooks(query: String): List<BookItem> {
        // api Key는 BuildConfig.API_KEY로 불러와야됌
        val response = apiService.searchBooks(BuildConfig.API_KEY, query)
        return response.items
    }

    // Firestore에 데이터 추가
    fun addItemToFirestore(item: SellingCartModel) {
        val collectionRef = firestore.collection("SellingCartTable")

        collectionRef.add(item)
            .addOnSuccessListener {
//                    Toast.makeText(requireContext(), "장바구니에 추가되었습니다.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
//                    Toast.makeText(requireContext(), "장바구니 추가 실패: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}