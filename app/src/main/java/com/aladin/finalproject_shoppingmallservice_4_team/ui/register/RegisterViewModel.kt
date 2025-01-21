package com.aladin.finalproject_shoppingmallservice_4_team.ui.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aladin.finalproject_shoppingmallservice_4_team.ui.login.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    val isIdAvailable = MutableLiveData<Boolean>()
    val idCheckErrorMessage = MutableLiveData<String>()

    fun checkUserIdAvailability(userId: String) {
        viewModelScope.launch {
            try {
                if (userId.isBlank()) {
                    idCheckErrorMessage.value = "아이디를 입력해주세요."
                    isIdAvailable.value = false
                    return@launch
                }

                val available = userRepository.isUserIdAvailable(userId)
                isIdAvailable.value = available
                idCheckErrorMessage.value = if (available) {
                    "사용 가능한 아이디입니다."
                } else {
                    "이미 존재하는 아이디입니다."
                }
            } catch (e: Exception) {
                idCheckErrorMessage.value = "아이디 확인 중 오류가 발생했습니다."
                isIdAvailable.value = false
            }
        }
    }

}