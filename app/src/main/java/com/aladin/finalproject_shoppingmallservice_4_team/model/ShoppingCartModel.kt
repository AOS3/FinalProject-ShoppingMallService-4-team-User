package com.aladin.finalproject_shoppingmallservice_4_team.model

data class ShoppingCartModel(
    val shoppingCartSellingPrice: Int = 0,
    val shoppingCartQuality: Int = 0,
    val shoppingCartISBN: String = "",
    val shoppingCartUserToken: String = "",
    val shoppingCartTime: Long = 0L,
    val shoppingCartState: Int = 0,
    val shoppingCartBookQualityCount: Int = 0,

    var shoppingCartBookCoverImage: String = "",
    var shoppingCartBookTitle: String = "",
    var shoppingCartBookAuthor: String = "",

    var isChecked: Boolean = false
)