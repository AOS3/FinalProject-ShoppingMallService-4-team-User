package com.aladin.finalproject_shoppingmallservice_4_team.util

import android.content.Context
import android.os.Bundle
import android.text.TextUtils.replace
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.aladin.finalproject_shoppingmallservice_4_team.R
import com.bumptech.glide.Glide
import java.util.concurrent.TimeUnit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter

fun Fragment.replaceMainFragment(
    fragment: Fragment,
    isAddToBackStack: Boolean,
    dataBundle: Bundle? = null,
    backStackTag: String? = null
) {
    // bundle 객체가 null이 아니라면
    dataBundle?.let {
        fragment.arguments = it
    }
    parentFragmentManager.commit {
        replace(R.id.fragmentContainerView, fragment)
        if (isAddToBackStack) {
            addToBackStack(backStackTag)
        }
    }
}

fun Fragment.replaceSubFragment(
    fragment: Fragment,
    isAddToBackStack: Boolean,
    dataBundle: Bundle? = null,
    backStackTag: String? = null
) {
    // bundle 객체가 null이 아니라면
    dataBundle?.let {
        fragment.arguments = it
    }
    parentFragmentManager.commit {
        replace(R.id.fragmentContainerView_main_sub, fragment)
        if (isAddToBackStack) {
            addToBackStack(backStackTag)
        }
    }
}

fun Fragment.clearAllBackStack() {
    parentFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
}

fun Fragment.removeFragment() {
    parentFragmentManager.popBackStack()
}

fun TextView.showSoftInput() {
    val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    this.requestFocus() // TextField에 포커스 설정

    // UI 준비 후 키보드 올리기
    this.postDelayed({
        inputManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }, 200) // 안전성을 위해 약간의 지연 추가
}

fun TextView.hideSoftInput() {
    val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    // 키보드 내리기
    inputManager.hideSoftInputFromWindow(this.windowToken, 0)
    this.clearFocus() // 포커스 해제
}

// OkHttpClient 생성
// OkHttpClient는 HTTP 요청을 보내는 데 사용되는 클라이언트로, 여러 가지 설정을 통해 요청의 성능과 안정성을 향상시킬 수 있습니다.
// 이 함수는 연결 시간, 쓰기 및 읽기 타임아웃을 설정하고, 로그 인터셉터를 추가하여 요청/응답 로그를 출력합니다.
fun provideOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder().apply {
        connectTimeout(5, TimeUnit.MINUTES) // Connect timeout 설정 (연결 시간이 너무 오래 걸릴 경우 예외 발생)
        writeTimeout(5, TimeUnit.MINUTES) // Write timeout 설정 (데이터 전송 시간이 오래 걸릴 경우 예외 발생)
        readTimeout(5, TimeUnit.MINUTES) // Read timeout 설정 (서버로부터 응답 시간이 너무 오래 걸릴 경우 예외 발생)
        addInterceptor(provideLoggingInterceptor()) // 로그 인터셉터 추가 (HTTP 요청과 응답 로그를 출력)
    }.build()
}

// JSON Converter 설정
// JSON 데이터와 Kotlin 객체를 변환하기 위한 Converter를 제공합니다.
// 이 함수에서는 JSON 파싱시 발생할 수 있는 오류를 무시하고, 기본값 설정 및 null 필드를 처리하는 방법을 설정합니다.
fun provideJsonConverterFactory(): Converter.Factory {
    return Json {
        isLenient = true // JSON 파싱 시 느슨한 규칙을 적용 (불필요한 공백 등 무시)
        ignoreUnknownKeys = true // 알 수 없는 키를 무시 (서버에서 전달된 예상 외의 키를 무시하여 오류를 방지)
        coerceInputValues = true // 기본값을 설정 (값이 없을 경우 기본값을 사용)
        explicitNulls = false // JSON 데이터에서 null 값을 처리하지 않음 (null 값을 포함하지 않도록 설정)
    }.asConverterFactory("application/json".toMediaType()) // JSON을 처리할 수 있도록 변환
}

// HTTP 로깅 인터셉터 제공
// 이 함수는 요청 및 응답에 대한 로그를 출력하기 위한 HTTP 로깅 인터셉터를 생성합니다.
// API 호출 시 전송되는 데이터와 응답 데이터를 확인할 수 있어 디버깅에 유용합니다.
fun provideLoggingInterceptor(): HttpLoggingInterceptor {
    return HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT).apply {
        level = HttpLoggingInterceptor.Level.BODY // 요청/응답 본문 전체를 출력 (디버깅용)
    }
}

fun ImageView.loadImage(uri: String) {
    Glide.with(context)
        .load(uri)
        .into(this)
}

fun ImageView.loadBannerImage(uri: Int) {
    Glide.with(context)
        .load(uri)
        .centerCrop()
        .into(this)
}

fun Number.toCommaString(): String {
    return String.format("%,d", this)
}