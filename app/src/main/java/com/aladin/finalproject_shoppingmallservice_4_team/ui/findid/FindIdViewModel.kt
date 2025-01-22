package com.aladin.finalproject_shoppingmallservice_4_team.ui.findid

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aladin.finalproject_shoppingmallservice_4_team.ui.login.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FindIdViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    val userIdLiveData = MutableLiveData<String?>()
    val errorMessageLiveData = MutableLiveData<String>()

    fun findUserId(userName: String, phoneNumber: String) {
        viewModelScope.launch {
            val userId = userRepository.findUserIdByNameAndPhone(userName, phoneNumber)
            if (userId != null) {
                userIdLiveData.postValue(userId) // 검색 결과를 LiveData에 저장
            } else {
                errorMessageLiveData.postValue("해당 정보를 가진 사용자를 찾을 수 없습니다.")
            }
        }
    }
}