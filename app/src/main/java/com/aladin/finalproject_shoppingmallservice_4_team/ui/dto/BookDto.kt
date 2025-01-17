package com.aladin.apiTestApplication.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


// 책 검색
@Serializable
data class BookResponse(
    @SerialName("item")
    val items:List<BookItem>
)

@Serializable
data class BookItem(
    @SerialName("title")
    val title: String,
    @SerialName("author")
    val author: String,
    @SerialName("pubDate")
    val pubDate:String,
    @SerialName("publisher")
    val publisher: String,
    @SerialName("priceStandard")
    val priceStandard: Int,
    @SerialName("priceSales")
    val priceSales: Int,
    @SerialName("cover")
    val cover:String,
    @SerialName("description")
    val description: String,
)

/////////////////////////////////////////////////////////////////////////////////////////////////////

// 책 추천
@Serializable
data class RecommendBookResponse(
    val title: String,
    val link: String,
    val logo: String,
    val pubDate: String,
    val totalResults: Int,
    val startIndex: Int,
    val itemsPerPage: Int,
    val query: String,
    val version: String,
    val searchCategoryId: Int,
    val searchCategoryName: String,
    @SerialName("item")
    val items: List<RecommendBookItem>
)

@Serializable
data class RecommendBookItem(
    val itemId: String,
    val title: String,
    val link: String,
    val author: String,
    val pubDate: String,
    val description: String,
    val isbn: String,
    val isbn13: String,
    val priceSales: Int,
    val priceStandard: Int,
    val mallType: String,
    val stockStatus: String?,
    val mileage: Int,
    val cover: String,
    val categoryId: Int,
    val categoryName: String,
    val publisher: String,
    val salesPoint: Int,
    val adult: Boolean,
    val fixedPrice: Boolean,
    val customerReviewRank: Int,
    val seriesInfo: RecommendSeriesInfo? = null,
)

@Serializable
data class RecommendSeriesInfo(
    val seriesId: Int,
    val seriesLink: String,
    val seriesName: String
)