package com.aladin.finalproject_shoppingmallservice_4_team.repository

import com.aladin.apiTestApplication.dto.BookItem
import com.aladin.apiTestApplication.network.AladdinApiService
import com.aladin.finalproject_shoppingmallservice_4_team.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookDetailRepository @Inject constructor(
    private val apiService: AladdinApiService,
) {
    // 책 검색 API 호출 함수
    suspend fun searchBooks(query: String): List<BookItem> {
        // api Key는 BuildConfig.API_KEY로 불러와야됌
        val response = apiService.searchBooks(BuildConfig.API_KEY, query)
        return response.items
    }
}