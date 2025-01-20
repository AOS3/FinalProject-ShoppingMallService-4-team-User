package com.aladin.finalproject_shoppingmallservice_4_team.repository

import com.aladin.apiTestApplication.dto.BookItem
import com.aladin.apiTestApplication.dto.RecommendBookItem
import com.aladin.apiTestApplication.network.AladdinApiService
import com.aladin.finalproject_shoppingmallservice_4_team.BuildConfig

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val apiService: AladdinApiService
) {

    // 추천 도서 불러오기
    suspend fun recommendBooks(queryType: String, maxResults: Int): List<RecommendBookItem> {
        val response = apiService.recommendedBooks(
            apiKey = BuildConfig.API_KEY,
            queryType = queryType,
            maxResults = maxResults
        )
        return response.items
    }
}