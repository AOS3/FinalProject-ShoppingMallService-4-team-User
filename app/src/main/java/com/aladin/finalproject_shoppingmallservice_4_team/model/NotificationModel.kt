package com.aladin.finalproject_shoppingmallservice_4_team.model

data class NotificationModel (
    val notificationContent: String = "",
    val notificationSee: Int = 0,
    val notificationState: Int = 0,
    val notificationTime: Long = 0L,
    val notificationTitle: String = "",
    val notificationUserToken: String = "",
)