package com.aladin.finalproject_shoppingmallservice_4_team.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude

data class SellingInquiryModel (
    // 테이블 이름 : SellingInquiryTable
    // 판매가
    var sellingInquiryPrice:Int = 0,
    // 최종 판매가
    var sellingInquiryFinalPrice:Int = 0,
    // 퓸질
    // 0 : 상, 1 : 중, 2 : 하
    var sellingInquiryQuality:Int = 0,
    // ISBN
    var sellingInquiryISBN:String = "",
    // 사용자 토큰 값
    var sellingInquiryUserToken:String = "",
    // 판매 승인 싱청 결과값
    // 0 : 승인 신청, 1 : 품질 검수, 2 : 검수 완료
    var sellingInquiryApprovalResult:Int = 0,
    // 판매 버튼을 누른 시간
    var sellingInquiryTime:Long = 0L,
    // 발송 방법
    var sellingInquiryShippingMethod:Int = 0,
    // 매입 불가 상품 처리 방법
    var sellingInquiryNonPurchaseableMethod:Int = 0,
    // 예금주
    var sellingInquiryDepositor:String = "",
    // 은행
    var sellingInquiryBankName:String = "",
    // 계좌 번호
    var sellingInquiryBankAccountNumber:String = "",
    // 도서명
    var sellingInquiryBookName:String = "",
    // 저자
    var sellingInquiryBookAuthor:String = "",
    // 판매 조회 상태
    var sellingInquiryState:Int = 0,
    // 관리자가 선택한 품질
    var sellingInquiryChoiceQuality:Int = 0,
    // 정상가
    var sellingInquiryOriginalPrice:Int = 0,
    // Firestore 문서 ID
    @Exclude @DocumentId var documentId: String = ""
)