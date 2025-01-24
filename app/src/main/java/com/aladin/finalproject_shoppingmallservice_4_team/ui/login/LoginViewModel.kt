package com.aladin.finalproject_shoppingmallservice_4_team.ui.login

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aladin.finalproject_shoppingmallservice_4_team.BookApplication
import com.aladin.finalproject_shoppingmallservice_4_team.model.UserModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val application: Application // 어플리케이션 객체를 주입받음
) : ViewModel() {

    // val bookApplication = context as BookApplication

    // 아이디 입력 요소
    val textFieldUserLoginIdValue = MutableLiveData("")
    // 비밀번호 입력 요소
    val textFieldUserLoginPasswordValue = MutableLiveData("")
    // 자동 로그인 입력 요소
    val checkBoxAutoLoginValue = MutableLiveData(false)
    // 로그인 결과 값
    val loginResult = MutableLiveData<Boolean>()
    // 로그인 에러 메시지
    val loginErrorMessage = MutableLiveData("")

    private val _autoLoginTokenResult = MutableLiveData<Boolean>()
    val autoLoginTokenResult: LiveData<Boolean> get() = _autoLoginTokenResult

    // buttonUserLoginFindId - onClick
    fun buttonUserLoginFindId(){

    }

    // buttonUserLoginFindPw - onClick
    fun buttonUserLoginFindPw(){

    }

    // buttonUserLogin - onClick
    fun buttonUserLogin() {
        val userId = textFieldUserLoginIdValue.value.orEmpty().trim() // 입력값 공백 제거
        val password = textFieldUserLoginPasswordValue.value.orEmpty().trim()

        if (userId.isBlank()) {
            loginErrorMessage.value = "아이디를 입력해주세요."
            return
        }

        if (password.isBlank()) {
            loginErrorMessage.value = "비밀번호를 입력해주세요."
            return
        }

        viewModelScope.launch {
            try {
                Log.d("test100", "로그인 요청 시작 - 입력 아이디: $userId, 입력 비밀번호: $password")

                val userList: List<UserModel> = userRepository.selectUserDataByUserId(userId)
                Log.d("test100", "Firestore에서 매핑된 사용자 리스트: ${userList.map { "userId=${it.userId}, userPw=${it.userPw}" }}")

                val user = userList.firstOrNull()

                when {
                    user == null -> {
                        loginErrorMessage.value = "존재하지 않는 아이디입니다."
                        Log.d("test100", "로그인 실패 - 아이디 없음")
                    }

                    user.userPw.trim() != password -> { // Firestore 비밀번호 공백 제거 후 비교
                        loginErrorMessage.value = "비밀번호가 일치하지 않습니다."
                        Log.d(
                            "test100",
                            "로그인 실패 - 비밀번호 불일치. 입력 비밀번호: $password, Firestore 비밀번호: ${user.userPw.trim()}"
                        )
                    }

                    else -> {
                        // 로그인 성공 시 사용자 정보 저장
                        val bookApplication = application as BookApplication
                        bookApplication.loginUserModel = user // 사용자 정보를 Application 객채에 저장

                        loginResult.value = true
                        // 사용자 정보 로그 출력
                        Log.d("test100", """
                        로그인 성공!
                        사용자 정보:
                        ID: ${user.userId}
                        이름: ${user.userName}
                        주소: ${user.userAddress}
                        전화번호: ${user.userPhoneNumber}
                        상태: ${user.userState}
                    """.trimIndent())
                    }
                }
            } catch (e: Exception) {
                loginErrorMessage.value = "로그인 중 오류가 발생했습니다."
                Log.e("test100", "로그인 요청 중 오류: ${e.message}", e)
            }
        }
    }

    fun generateAutoLoginToken(): String {
        return UUID.randomUUID().toString()
    }

    // 자동 로그인 토큰 저장
    suspend fun saveAutoLoginToken(userId: String, autoLoginToken: String): Boolean {
        return try {
            userRepository.updateUserAutoLoginToken(userId, autoLoginToken)
        } catch (e: Exception) {
            Log.e("LoginViewModel", "자동 로그인 토큰 저장 오류: ${e.message}", e)
            false
        }
    }
}