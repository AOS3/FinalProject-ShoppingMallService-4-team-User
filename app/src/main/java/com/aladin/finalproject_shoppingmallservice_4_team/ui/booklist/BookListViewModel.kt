package com.aladin.finalproject_shoppingmallservice_4_team.ui.booklist

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BookListViewModel @Inject constructor(private val bookListRepository: BookListRepository) :
    ViewModel() {

}