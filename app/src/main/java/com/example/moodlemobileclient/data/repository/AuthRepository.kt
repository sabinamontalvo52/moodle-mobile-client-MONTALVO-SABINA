package com.example.moodlemobileclient.data.repository

import com.example.moodlemobileclient.services.RetrofitClient
import com.example.moodlemobileclient.data.model.auth.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepository {

    fun validateMoodleToken(
        token: String,
        callback: (Boolean) -> Unit
    ) {
        RetrofitClient.service
            .getSiteInfo(token)
            .enqueue(object : Callback<SiteInfoResponse> {

                override fun onResponse(
                    call: Call<SiteInfoResponse>,
                    response: Response<SiteInfoResponse>
                ) {
                    callback(response.isSuccessful)
                }

                override fun onFailure(call: Call<SiteInfoResponse>, t: Throwable) {
                    callback(false)
                }
            })
    }
}
