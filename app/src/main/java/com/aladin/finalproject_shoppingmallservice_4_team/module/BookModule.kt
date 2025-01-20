package com.aladin.finalproject_shoppingmallservice_4_team.module

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.aladin.apiTestApplication.network.AladdinApiService
import com.aladin.finalproject_shoppingmallservice_4_team.util.provideJsonConverterFactory
import com.aladin.finalproject_shoppingmallservice_4_team.util.provideOkHttpClient
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BookModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        return Retrofit.Builder()
            .baseUrl("https://www.aladin.co.kr/")
            .addConverterFactory(provideJsonConverterFactory())
            .client(provideOkHttpClient())
            .build()
    }

    @Provides
    @Singleton
    fun provideAladdinApiService(retrofit: Retrofit): AladdinApiService {
        return retrofit.create(AladdinApiService::class.java)
    }

    @Provides
    @Singleton
    fun sharedPreferencesProvider(app: Application): SharedPreferences {
        return app.getSharedPreferences("preferences_name", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

}