package com.aladin.finalproject_shoppingmallservice_4_team.model

data class NoticeModel(
    val noticeTitle: String = "", // 제목
    val noticeContent: String = "", // 내용
    val noticeDate: String = "", // 작성 날짜
    val noticeState: Int = 0 // 공지사항 상태값
)