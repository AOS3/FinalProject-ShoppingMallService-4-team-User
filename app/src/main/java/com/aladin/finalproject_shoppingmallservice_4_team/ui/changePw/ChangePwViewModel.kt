package com.aladin.finalproject_shoppingmallservice_4_team.ui.changePw

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChangePwViewModel
    (
    val changePwRepository: ChangePwRepository
) :
    ViewModel() {
    // 현재 비밀번호
    val currentUserPassword = MutableLiveData("")

    // 새로운 비밀번호
    val newUserPassword = MutableLiveData("")

    // 새로운 비밀번호 재입력
    val newCheckUserPassword = MutableLiveData("")

    // 현재 비밀번호와 똑같은지 확인 하는 함수
    fun checkCurrentPassword(currentPassword:String):Boolean {
        return changePwRepository.isUserPasswordMatch(currentPassword)
    }

    // 비밀번호 변경 메서드
    fun changeUserPw(newPassword:String) {
        changePwRepository.changeUserPassword(newPassword)
    }
}