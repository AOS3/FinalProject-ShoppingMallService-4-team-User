package com.aladin.finalproject_shoppingmallservice_4_team.ui.findpw

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aladin.finalproject_shoppingmallservice_4_team.ui.login.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


// 이거 findPwViewModel -> FindPwViewModel로 이름 바꿈 .. 잘못 입력했다

@HiltViewModel
class FindPwViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userIdCheckResult = MutableLiveData<Boolean?>()
    val userIdCheckResult: LiveData<Boolean?> = _userIdCheckResult

    private val _passwordUpdateResult = MutableLiveData<Boolean>()
    val passwordUpdateResult: LiveData<Boolean> = _passwordUpdateResult

    fun checkUserIdAndPhone(userId: String, phoneNumber: String) {
        viewModelScope.launch {
            try {
                val userList = userRepository.selectUserDataByUserId(userId)
                val isMatch = userList.any { it.userPhoneNumber == phoneNumber }
                _userIdCheckResult.value = isMatch
            } catch (e: Exception) {
                _userIdCheckResult.value = null
            }
        }
    }

    fun updatePassword(userId: String, newPassword: String) {
        viewModelScope.launch {
            try {
                val isSuccess = userRepository.updateUserPassword(userId, newPassword)
                _passwordUpdateResult.value = isSuccess
            } catch (e: Exception) {
                _passwordUpdateResult.value = false
            }
        }
    }

}