package com.aladin.apiTestApplication.network

import com.aladin.finalproject_shoppingmallservice_4_team.util.provideJsonConverterFactory
import com.aladin.finalproject_shoppingmallservice_4_team.util.provideOkHttpClient
import retrofit2.Retrofit

//object AladdinRetrofitBuilder {
//    private const val BASE_URL = "https://www.aladin.co.kr/"
//
//    // Retrofit 인스턴스 생성
//    // Retrofit은 HTTP 통신을 간단히 처리할 수 있는 라이브러리입니다.
//    // 여기서는 Aladin API와 통신할 기본 설정을 포함한 Retrofit 객체를 생성합니다.
//    private val retrofit: Retrofit by lazy {
//        Retrofit.Builder()
//            .baseUrl(BASE_URL) // 기본 URL 설정 (Aladin API 주소)
//            .addConverterFactory(provideJsonConverterFactory()) // JSON 데이터를 Kotlin 객체로 변환하기 위한 Converter 추가
//            .client(provideOkHttpClient()) // HTTP 요청을 처리할 OkHttpClient 설정
//            .build()
//    }
//
//    // API Service 인터페이스 생성
//    // Retrofit을 사용하여 API 요청 메소드를 정의한 인터페이스를 구현합니다.
//    val apiService: AladdinApiService by lazy {
//        retrofit.create(AladdinApiService::class.java)
//    }
//}