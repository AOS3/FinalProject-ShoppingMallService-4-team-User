package com.aladin.finalproject_shoppingmallservice_4_team.repository

import com.aladin.apiTestApplication.dto.BookItem
import com.aladin.apiTestApplication.network.AladdinApiService
import com.aladin.finalproject_shoppingmallservice_4_team.BuildConfig
import javax.inject.Inject

class BarcodeScanResultRepository @Inject constructor(
    private val apiService: AladdinApiService
) {
    suspend fun fetchBookData(isbn: String): List<BookItem> {
        val response = apiService.searchBooks(apiKey = BuildConfig.API_KEY, query = isbn, queryType = "ISBN")
        return response.items
    }
}
