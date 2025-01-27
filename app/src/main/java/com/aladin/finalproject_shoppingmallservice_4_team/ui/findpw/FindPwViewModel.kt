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

    private val _userCheckResult = MutableLiveData<FindPwResult>()
    val userCheckResult: LiveData<FindPwResult> = _userCheckResult

    private val _passwordUpdateResult = MutableLiveData<Boolean>()
    val passwordUpdateResult: LiveData<Boolean> = _passwordUpdateResult

    fun checkUserIdAndPhone(userId: String, phoneNumber: String) {
        viewModelScope.launch {
            try {
                val userList = userRepository.selectUserDataByUserId(userId)
                val user = userList.firstOrNull { it.userPhoneNumber == phoneNumber }

                if (user == null) {
                    // ID와 전화번호가 일치하지 않음
                    _userCheckResult.value = FindPwResult.NOT_FOUND
                } else if (user.userState == 1) {
                    // 탈퇴한 회원
                    _userCheckResult.value = FindPwResult.DEACTIVATED
                } else {
                    // 정상 회원
                    _userCheckResult.value = FindPwResult.SUCCESS
                }
            } catch (e: Exception) {
                // 오류 발생
                _userCheckResult.value = FindPwResult.ERROR
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

// 비밀번호 찾기 상태를 나타내는 enum class
enum class FindPwResult {
    SUCCESS,        // 정상 회원
    DEACTIVATED,    // 탈퇴 회원
    NOT_FOUND,      // 사용자 정보 없음
    ERROR           // 기타 오류
}