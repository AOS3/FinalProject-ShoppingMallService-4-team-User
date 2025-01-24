package com.aladin.finalproject_shoppingmallservice_4_team.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude

data class SellingCartModel (
    // 테이블 이름 : SellingCartTable
    // 판매가
    var sellingCartSellingPrice:Int = 0,
    // 퓸질
    // 0 : 상, 1 : 중, 2 : 하
    var sellingCartQuality:Int = 0,
    // ISBN
    var sellingCartISBN:String = "",
    // 사용자 토큰 값
    var sellingCartUserToken:String = "",
    // 판매 장바구니에 들어간 시간
    var sellingCartTime:Long = 0L,
    // 판매 장바구니 상태
    // 0 : 판매 장바구니에 있는 상태
    // 1 : 판매 장바구니에서 체크 되어서 넘어가면 변하는 상태
    var sellingCartState:Int = 0,
    @Exclude @DocumentId var documentId: String = "" // Firestore 문서 ID
)