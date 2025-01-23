package com.aladin.finalproject_shoppingmallservice_4_team.ui.likeList


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aladin.finalproject_shoppingmallservice_4_team.model.LikeListModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LikeListViewModel @Inject constructor(private val likeListRepository: LikeListRepository) :
    ViewModel() {

    // 찜목록 리스트를 가져왔는지
    private val _isLoadLikeList = MutableLiveData<Boolean>(false)
    val isLoadLikeList: LiveData<Boolean> get() = _isLoadLikeList

    // 찜목록 리스트
    private val _likeListBookList = MutableLiveData<List<LikeListModel>>()
    val likeListBookList: LiveData<List<LikeListModel>> get() = _likeListBookList



    // 로그인 안하고 들어왔을 떄 처리용
    fun dismissProgressDialog() {
        _isLoadLikeList.value = true
    }

    fun showProgressDialog() {
        _isLoadLikeList.value = false
    }

    // 찜목록 리스트 가져오기
    fun gettingLikeListBookData(userToken: String) {
        viewModelScope.launch {
            val (likeListBookList, bookDataApiList) = withContext(Dispatchers.IO) {
                likeListRepository.gettingLikeListDataFromFireBaseStore(userToken)
            }

            withContext(Dispatchers.IO) {
                // 가져온 리스트들 Model에 업데이트
                for (i in likeListBookList.indices) {
                    // getOrNull로 가져온 값이 null일 경우 안전하게 처리
                    likeListBookList[i].likeListBookCoverImage =
                        bookDataApiList.first.getOrNull(i) ?: "" // 기본값을 설정
                    likeListBookList[i].likeListBookTitle =
                        bookDataApiList.second.getOrNull(i) ?: "Unknown Title" // 기본값 설정
                    likeListBookList[i].likeListBookAuthor =
                        bookDataApiList.third.getOrNull(i) ?: "Unknown Author" // 기본값 설정
                    likeListBookList[i].likeListBookPrice =
                        (bookDataApiList.fourth.getOrNull(i) ?: 0) // 기본값 설정
                }
            }

            withContext(Dispatchers.Default) {
                // 업데이트된 리스트 전달
                _likeListBookList.postValue(likeListBookList)
            }

            // 가져온 리스트가 비어있지 않다면
            if (likeListBookList.isNotEmpty()) {
                _isLoadLikeList.postValue(true)
            } else {
                _isLoadLikeList.postValue(true)
            }
        }
    }

    // 찜목록 삭제
    fun deleteLikeListData(userToken: String, isbnNumber: String): Boolean {
        viewModelScope.launch {
            // 프로그레스 다이얼로그 표시 (UI 스레드에서 실행)
            withContext(Dispatchers.Main) {
                showProgressDialog()
            }

            // 데이터 삭제 (IO 작업, 비동기적으로 처리)
            likeListRepository.deleteLikeListData(userToken, isbnNumber)

            // 데이터 다시 불러오기 (IO 작업, 비동기적으로 처리)
            gettingLikeListBookData(userToken)

            // 로딩 완료 처리 (UI 스레드에서 실행)
            withContext(Dispatchers.Main) {
                _isLoadLikeList.postValue(true)
            }
        }
        return true
    }
}