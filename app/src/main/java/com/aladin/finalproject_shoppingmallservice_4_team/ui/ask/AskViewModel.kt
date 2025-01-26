package com.aladin.finalproject_shoppingmallservice_4_team.ui.ask

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aladin.finalproject_shoppingmallservice_4_team.model.AskModel
import com.aladin.finalproject_shoppingmallservice_4_team.repository.AskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AskViewModel @Inject constructor(
    private val askRepository: AskRepository
) : ViewModel() {

    private val _uploadResult = MutableLiveData<Boolean>()
    val uploadResult: LiveData<Boolean> get() = _uploadResult

    private val _attachmentUrl = MutableLiveData<String?>()
    val attachmentUrl: LiveData<String?> get() = _attachmentUrl

    fun uploadAsk(askModel: AskModel) {
        viewModelScope.launch {
            val result = askRepository.saveAsk(askModel)
            _uploadResult.postValue(result)
        }
    }

    fun uploadAttachment(uri: Uri) {
        viewModelScope.launch {
            val url = askRepository.uploadAttachment(uri)
            _attachmentUrl.postValue(url) // Firestore Storage URL 저장
        }
    }
}