package com.aladin.finalproject_shoppingmallservice_4_team.model

import android.os.Parcelable

data class HomeBookModel (
    val image: String,
    val bookName: String,
    val bookWriter: String,
    val bookPrice: Int,
    val bookPublisher: String,
    val bookPubDate: String,
    val bookCategory: String,
    val bookDescription: String,
)