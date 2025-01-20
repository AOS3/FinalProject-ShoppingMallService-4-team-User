package com.aladin.finalproject_shoppingmallservice_4_team.ui.notice


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aladin.finalproject_shoppingmallservice_4_team.model.NoticeModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class NoticeViewModel @Inject constructor(
    private val noticeRepository: NoticeRepository
) : ViewModel() {

    // 공지사항 목록
    private val _noticeList = MutableLiveData<List<NoticeModel>>()
    val noticeList: LiveData<List<NoticeModel>> get() = _noticeList

    // 공지사항 목록을 가져왔는지
    private val _isLoadNoticeList = MutableLiveData<Boolean>(false)
    val isLoadNoticeList: LiveData<Boolean> get() = _isLoadNoticeList

    // 공지사항 데이터 가져오기
    fun gettingNoticeData() {
        viewModelScope.launch {
            val noticeList = withContext(Dispatchers.IO) {
                noticeRepository.gettingNoticeDataList()
            }
            _noticeList.postValue(noticeList)

            // 가져온 리스트가 비어있지 않다면
            if (noticeList.isNotEmpty()) {
                _isLoadNoticeList.postValue(true)
            } else {
                _isLoadNoticeList.postValue(true)
            }
        }
    }
}
