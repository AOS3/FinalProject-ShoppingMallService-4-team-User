package com.aladin.finalproject_shoppingmallservice_4_team.model

data class LikeListModel (
    val likeListISBN:String = "",
    val likeListUserToken:String = "",
    val likeListTime:Long = 0L,
    val likeListState:Int = 0,

    var likeListBookCoverImage: String = "",
    var likeListBookTitle: String = "",
    var likeListBookAuthor: String = "",
    var likeListBookPrice: Int = 0,
)