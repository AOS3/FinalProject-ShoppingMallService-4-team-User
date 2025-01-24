package com.aladin.finalproject_shoppingmallservice_4_team.ui.notification

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
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


    // 알림 불러오기
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

    // 전체 삭제
    fun deleteAllNotificationData() = viewModelScope.launch {
        runCatching {
            notificationRepository.deleteNotificationData(notificationList.value!!)
        }.onSuccess {
            _notificationList.value = emptyList()
        }.onFailure {
            Log.d("NotificationViewModel", "error: $it")
        }
    }

    fun deleteSwipeData(deleteData: NotificationModel) = viewModelScope.launch {
        runCatching {
            notificationRepository.deleteNotificationOneData(deleteData)
        }.onSuccess {
            _notificationList.value = _notificationList.value!!.filterNot { it.notificationTitle == deleteData.notificationTitle }
        }.onFailure {
            Log.d("notificationViewModel", "error: $it")
        }
    }

    fun seeData(seeData: NotificationModel) = viewModelScope.launch {
        runCatching {
            notificationRepository.seeNotificationOneData(seeData)
        }.onSuccess {
            _notificationList.value = _notificationList.value!!.filterNot { it.notificationTitle == seeData.notificationTitle }
        }.onFailure {
            Log.d("notificationViewModel", "error: $it")
        }
    }
}