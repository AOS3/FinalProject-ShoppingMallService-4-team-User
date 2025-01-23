package com.aladin.finalproject_shoppingmallservice_4_team.ui.changePw

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ChangePwViewModel @Inject constructor(
    private val changePwRepository: ChangePwRepository
) :
    ViewModel() {
    // 현재 비밀번호
    val currentUserPassword = MutableLiveData("")

    // 새로운 비밀번호
    val newUserPassword = MutableLiveData("")

    // 새로운 비밀번호 재입력
    val newCheckUserPassword = MutableLiveData("")

    // 비밀번호 변경이 되었는지
    private val _isSuccessChangePw = MutableLiveData<Boolean>(false)
    val isSuccessChangePw: LiveData<Boolean> get() = _isSuccessChangePw

    // 비밀번호 변경
    fun changeUserPassword(userToken: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                changePwRepository.changeUserPassword(userToken,newUserPassword.value.toString().trim())
            }
            _isSuccessChangePw.postValue(true)

        }
    }
}