package com.aladin.finalproject_shoppingmallservice_4_team.ui.barcodescanner

import com.aladin.apiTestApplication.dto.BookItem
import com.aladin.apiTestApplication.network.AladdinApiService
import com.aladin.finalproject_shoppingmallservice_4_team.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BarcodeScannerRepository @Inject constructor(
    private val apiService: AladdinApiService
) {
    // 책 검색 API 호출 함수
    suspend fun searchBooks(query: String, maxResults: Int, sort: String): List<BookItem> {
        val response = apiService.searchBooks(BuildConfig.API_KEY, query, maxResults = maxResults, sort = sort)
        return response.items
    }
}