package com.aladin.finalproject_shoppingmallservice_4_team.model

import com.google.firebase.firestore.Exclude

data class ShoppingCartModel(
    val shoppingCartSellingPrice: Int = 0,
    val shoppingCartQuality: Int = 0,
    val shoppingCartISBN: String = "",
    val shoppingCartUserToken: String = "",
    val shoppingCartTime: Long = 0L,
    val shoppingCartState: Int = 0,
    val shoppingCartBookQualityCount: Int = 0,

    @Exclude
    var shoppingCartBookCoverImage: String = "",
    @Exclude
    var shoppingCartBookTitle: String = "",
    @Exclude
    var shoppingCartBookAuthor: String = "",
    @Exclude
    var isChecked: Boolean = false
)