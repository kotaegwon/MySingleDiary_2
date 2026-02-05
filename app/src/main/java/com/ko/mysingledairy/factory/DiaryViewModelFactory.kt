package com.ko.mysingledairy.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ko.mysingledairy.repository.DiaryRepository
import com.ko.mysingledairy.viewmodel.DiaryViewModel

class DiaryViewModelFactory(private val repo: DiaryRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(DiaryViewModel::class.java)) {

            return DiaryViewModel(repo) as T
        }

        throw IllegalArgumentException()
    }
}
