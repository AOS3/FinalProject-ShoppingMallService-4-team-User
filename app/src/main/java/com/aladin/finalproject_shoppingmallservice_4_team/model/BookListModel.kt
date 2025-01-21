package com.aladin.finalproject_shoppingmallservice_4_team.model

data class BookListModel(
    val usedBookAuthor: String = "",
    val usedBookISBN: String = "",
    val usedBookQuality: Int = 0,
    val usedBookSellingPrice: Int = 0,
    val usedBookState: Int = 0,
    val usedBookTime: Long = 0L,
    val usedBookTitle: String = "",
    var cover: String? = null,
    val isbn13: String? = null,
)
