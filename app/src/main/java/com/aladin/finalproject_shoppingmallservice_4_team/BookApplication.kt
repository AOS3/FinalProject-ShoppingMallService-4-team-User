package com.aladin.finalproject_shoppingmallservice_4_team

import android.app.Application
import com.aladin.finalproject_shoppingmallservice_4_team.model.UserModel
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BookApplication: Application() {
    // 로그인한 사용자 객체
    lateinit var loginUserModel : UserModel
}