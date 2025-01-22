package com.aladin.finalproject_shoppingmallservice_4_team.model

data class UsedBookInventoryModel (
    val usedBookSellingPrice:Int = 0,
    val usedBookQuality:Int = 0,
    val usedBookISBN:String = "",
    val usedBookTitle:String = "",
    val usedBookAuthor:String = "",
    val usedBookTime:Long = 0L,
    val usedBookState:Int = 0,
)