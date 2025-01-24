package com.aladin.finalproject_shoppingmallservice_4_team.ui.myinfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aladin.finalproject_shoppingmallservice_4_team.ui.login.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AccountDeleteViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _passwordValidationResult = MutableLiveData<Boolean>()
    val passwordValidationResult: LiveData<Boolean> get() = _passwordValidationResult

    private val _accountDeactivationResult = MutableLiveData<Boolean>()
    val accountDeactivationResult: LiveData<Boolean> get() = _accountDeactivationResult

    fun validatePassword(userId: String, inputPassword: String) {
        viewModelScope.launch {
            val user = userRepository.getUserData(userId)
            _passwordValidationResult.postValue(user?.userPw == inputPassword)
        }
    }

    fun deactivateAccount(userId: String) {
        viewModelScope.launch {
            try {
                val isDeactivated = withContext(Dispatchers.IO) {
                    userRepository.updateUserState(userId, 1) // userState를 1로 변경
                }
                _accountDeactivationResult.postValue(isDeactivated)
            } catch (e: Exception) {
                _accountDeactivationResult.postValue(false)
            }
        }
    }
}