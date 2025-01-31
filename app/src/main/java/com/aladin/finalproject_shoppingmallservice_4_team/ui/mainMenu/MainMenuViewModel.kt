package com.aladin.finalproject_shoppingmallservice_4_team.ui.mainMenu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainMenuViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _userName = MutableLiveData<String?>()
    val userName: LiveData<String?> get() = _userName

    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> get() = _isLoggedIn

    // 로그인 상태 및 사용자 이름 확인
    fun checkUserLoginStatus(userToken: String) {
        if (userToken.isEmpty()) {
            _isLoggedIn.value = false
            _userName.value = null
        } else {
            viewModelScope.launch {
                firestore.collection("UserTable")
                    .whereEqualTo("userToken", userToken)
                    .get()
                    .addOnSuccessListener { result ->
                        if (!result.isEmpty) {
                            val document = result.documents.first()
                            val userName = document.getString("userName")
                            _userName.value = userName
                            _isLoggedIn.value = true
                        } else {
                            _userName.value = null
                            _isLoggedIn.value = false
                        }
                    }
                    .addOnFailureListener {
                        _userName.value = null
                        _isLoggedIn.value = false
                    }
            }
        }
    }
}