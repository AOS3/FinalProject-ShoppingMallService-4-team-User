package com.aladin.finalproject_shoppingmallservice_4_team.model

data class OrderInquiryModel (
    val orderInquiryPrice:Int=0,
    val orderInquiryQuality:Int=0,
    val orderInquiryISBN:String="",
    val orderInquiryUserToken:String = "",
    val orderInquiryDeliveryResult:Int=0,
    val orderInquiryTime:Long=0L,
    val orderInquiryNumber:String="",
    val orderInquiryTitle:String="",
    val orderInquiryAuthor:String="",
    val orderInquiryState:Int=0,
    val orderInquiryName:String="",
    val orderInquiryPhoneNumber:String="",
    val orderInquiryAddress:String="",
    val orderInquiryTotal:Int=0,
)