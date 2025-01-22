package com.aladin.finalproject_shoppingmallservice_4_team.ui.notification

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aladin.apiTestApplication.dto.RecommendBookItem
import com.aladin.finalproject_shoppingmallservice_4_team.model.NotificationModel
import com.aladin.finalproject_shoppingmallservice_4_team.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
): ViewModel() {

    // UI에서 사용할 데이터 (LiveData)
    private val _notificationList = MutableLiveData<List<NotificationModel>>()
    val notificationList: LiveData<List<NotificationModel>> get() = _notificationList


    // 알림 목록을 가져왔는지
    private val _isLoadNotificationList = MutableLiveData<Boolean>(false)
    val isLoadNotificationList: LiveData<Boolean> get() = _isLoadNotificationList


    // 책 목록에 따라 추천 검색
    fun loadNotificationData(userToken: String) = viewModelScope.launch {
        runCatching {
            notificationRepository.loadNotificationData(userToken)
        }.onSuccess {
            _notificationList.value = it
            _isLoadNotificationList.postValue(true)
        }.onFailure {
            Log.d("NotificaitionViewModel", "error: $it")
        }
    }
}