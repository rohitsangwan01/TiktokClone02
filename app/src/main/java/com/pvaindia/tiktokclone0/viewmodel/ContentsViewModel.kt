package com.pvaindia.tiktokclone0.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pvaindia.tiktokclone0.AppConfig
import com.pvaindia.tiktokclone0.api.ContentsResponse

class ContentsViewModel(application: Application) : AndroidViewModel(application) {

    private val mutableLiveData: MutableLiveData<ContentsResponse>
    private val contentsRepository: ContentsRepository = ContentsRepository.instance

    val contents: LiveData<ContentsResponse>
        get() = mutableLiveData

    init {
        mutableLiveData = contentsRepository.getContents(AppConfig.API_KEY)
    }
}