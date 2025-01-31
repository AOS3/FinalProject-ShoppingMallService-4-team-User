package com.aladin.finalproject_shoppingmallservice_4_team.model

data class BookCountModel (
    val bookCountISBN: String = "",
    val bookCountName: String = "",
    val bookCountAuthor: String = "",
    val bookCountQuality: Int = 0,
    val bookCountTotal: Int = 0,
    val bookCountTime: Long = System.currentTimeMillis(),
    val bookCountState: Int = 0,
)