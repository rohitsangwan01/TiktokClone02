package com.pvaindia.tiktokclone0.viewmodel

import androidx.lifecycle.MutableLiveData
import com.pvaindia.tiktokclone0.AppConfig
import com.pvaindia.tiktokclone0.api.CmsService
import com.pvaindia.tiktokclone0.api.ContentsApi
import com.pvaindia.tiktokclone0.api.ContentsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContentsRepository {

    companion object {
        @JvmStatic
        val instance: ContentsRepository by lazy { ContentsRepository() }
    }

    private val contentsApi: ContentsApi = CmsService.createContentsService(AppConfig.API_URL)

    fun getContents(apiKey: String): MutableLiveData<ContentsResponse> {
        val contents: MutableLiveData<ContentsResponse> = MutableLiveData()

        contentsApi.getContents(apiKey).enqueue(object : Callback<ContentsResponse?> {
            override fun onResponse(
                call: Call<ContentsResponse?>,
                response: Response<ContentsResponse?>
            ) {
                if (response.isSuccessful) {
                    contents.value = response.body()
                }
            }

            override fun onFailure(
                call: Call<ContentsResponse?>,
                t: Throwable
            ) {
                contents.value = null
            }
        })
        return contents
    }
}