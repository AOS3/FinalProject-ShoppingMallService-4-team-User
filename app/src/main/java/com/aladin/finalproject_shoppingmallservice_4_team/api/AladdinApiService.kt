package com.aladin.apiTestApplication.network

import com.aladin.apiTestApplication.dto.BookResponse
import com.aladin.apiTestApplication.dto.RecommendBookResponse
import retrofit2.http.GET
import retrofit2.http.Query

// api 데이터 모델 정리
interface AladdinApiService {

    @GET("ttb/api/ItemSearch.aspx")
    suspend fun searchBooks(
        @Query("TTBKey") apiKey: String,
        // apiKey값

        @Query("Query") query: String,
        // 검색어

        @Query("QueryType") queryType: String = "Keyword",
        // Keyword (기본값) : 제목+저자
        // Title : 제목검색
        // Author : 저자검색
        // Publisher : 출판사검색

        @Query("MaxResults") maxResults: Int = 100,
        // 최대로 뜨는 검색 값 10개

        @Query("Sort") sort: String = "Title",
        // 정렬 순서
        // Accuracy(기본값): 관련도
        // PublishTime : 출간일
        // Title : 제목
        // SalesPoint : 판매량
        // CustomerRating 고객평점
        // MyReviewCount :마이리뷰갯수



        @Query("Cover") cover : String = "Mid",
        // 표시 이미지 크기
        // Big : 큰 크기 : 너비 200px
        // MidBig : 중간 큰 크기 : 너비 150px
        // Mid(기본값) : 중간 크기 : 너비 85px
        // Small : 작은 크기 : 너비 75px
        // Mini : 매우 작은 크기 : 너비 65px
        // None : 없음


        @Query("Output") output: String = "JS",
        // JSON 데이터를 받기위해서 XML과 JS 중 JS 선택

        @Query("Version") version: Int = 20131101
        // 최신버전의 날짜 버전 20131101 작성
    ): BookResponse

    ////////////////////////////////////////////////////////////

    @GET("ttb/api/ItemList.aspx")
    suspend fun recommendedBooks(
        @Query("TTBKey") apiKey: String,
        @Query("QueryType") queryType:String,
        // ItemNewAll : 신간 전체 리스트
        // ItemNewSpecial : 주목할 만한 신간 리스트
        // ItemEditorChoice : 편집자 추천 리스트
        // (카테고리로만 조회 가능 - 국내도서/음반/외서만 지원)
        // Bestseller : 베스트셀러
        // BlogBest : 블로거 베스트셀러 (국내도서만 조회 가능)
        @Query("MaxResults") maxResults: Int = 100,
        @Query("Start") start:Int = 1,
        @Query("SearchTarget") searchTarget: String = "Book",
        @Query("Output") output: String = "JS",
        @Query("Cover") cover : String = "Mid",
        @Query("Version") version: Int = 20131101,
    ): RecommendBookResponse
}