package com.aladin.finalproject_shoppingmallservice_4_team.ui.myinfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aladin.finalproject_shoppingmallservice_4_team.model.UserModel
import com.aladin.finalproject_shoppingmallservice_4_team.ui.login.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyInfoViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel(){

    private val _userInfo = MutableLiveData<UserModel>()
    val userInfo : LiveData<UserModel> get() = _userInfo

    private val _updateResult = MutableLiveData<Pair<Boolean, UserModel?>>()
    val updateResult : LiveData<Pair<Boolean, UserModel?>> get() = _updateResult


    fun updateUserInfo(userId: String, newName: String, newAddress: String) {
        viewModelScope.launch {
            try {
                val isUpdated = userRepository.updateUserInfo(userId, newName, newAddress)
                if (isUpdated) {
                    val updatedUser = userRepository.getUserById(userId) // 업데이트된 사용자 정보 가져오기
                    _updateResult.value = Pair(true, updatedUser)
                } else {
                    _updateResult.value = Pair(false, null)
                }
            } catch (e: Exception) {
                _updateResult.value = Pair(false, null)
            }
        }
    }
}