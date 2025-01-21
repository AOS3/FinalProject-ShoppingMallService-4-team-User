package com.aladin.finalproject_shoppingmallservice_4_team.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.aladin.apiTestApplication.dto.BookItem
import com.aladin.apiTestApplication.network.AladdinApiService
import com.aladin.finalproject_shoppingmallservice_4_team.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepository @Inject constructor(
    private val apiService: AladdinApiService
) {
    // 책 검색 API 호출 함수
    suspend fun searchBooks(query: String, maxResults: Int, sort: String): List<BookItem> {
        // api Key는 BuildConfig.API_KEY로 불러와야됌
        val response = apiService.searchBooks(BuildConfig.API_KEY, query, maxResults = maxResults, sort = sort)
        return response.items
    }
}