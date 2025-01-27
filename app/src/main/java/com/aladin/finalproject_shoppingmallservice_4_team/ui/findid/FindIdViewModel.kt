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

    val userIdLiveData = MutableLiveData<String?>() // 성공한 경우 userId 저장
    val errorMessageLiveData = MutableLiveData<String?>() // 에러 메시지 저장

    fun findUserId(userName: String, phoneNumber: String) {
        // 이전 상태 초기화
        resetState()

        viewModelScope.launch {
            try {
                // 이름과 전화번호로 userId 조회
                val userId = userRepository.findUserIdByNameAndPhone(userName, phoneNumber)

                if (userId == null) {
                    // 사용자 정보 없음
                    errorMessageLiveData.postValue("해당 정보를 가진 사용자를 찾을 수 없습니다.")
                } else {
                    // userId를 통해 사용자 상세 정보 조회
                    val user = userRepository.getUserById(userId)

                    if (user == null) {
                        errorMessageLiveData.postValue("사용자 정보를 불러오는 데 실패했습니다.")
                    } else if (user.userState == 1) {
                        // 탈퇴 회원
                        errorMessageLiveData.postValue("탈퇴한 회원입니다.")
                    } else {
                        // 정상 회원
                        userIdLiveData.postValue(userId)
                    }
                }
            } catch (e: Exception) {
                errorMessageLiveData.postValue("아이디 찾기 중 오류가 발생했습니다.")
            }
        }
    }

    private fun resetState() {
        userIdLiveData.postValue(null)
        errorMessageLiveData.postValue(null)
    }
}