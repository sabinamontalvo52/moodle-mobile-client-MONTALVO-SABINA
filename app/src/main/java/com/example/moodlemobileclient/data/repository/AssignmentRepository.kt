package com.example.moodlemobileclient.data.repository

import com.example.moodlemobileclient.data.model.common.*
import com.example.moodlemobileclient.services.RetrofitClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AssignmentRepository {

    fun getDraftItemId(
        token: String,
        onSuccess: (Int) -> Unit,
        onError: () -> Unit
    ) {
        RetrofitClient.service.getDraftItemId(token)
            .enqueue(object : Callback<DraftItemIdResponse> {

                override fun onResponse(
                    call: Call<DraftItemIdResponse>,
                    response: Response<DraftItemIdResponse>
                ) {
                    val draftId = response.body()?.itemid
                    if (draftId != null && draftId > 0) {
                        onSuccess(draftId)
                    } else {
                        onError()
                    }
                }

                override fun onFailure(call: Call<DraftItemIdResponse>, t: Throwable) {
                    onError()
                }
            })
    }


    fun submitForGrading(
        token: String,
        assignmentId: Int,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        RetrofitClient.service.submitForGrading(
            token = token,
            assignmentId = assignmentId
        ).enqueue(object : Callback<ResponseBody> {

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                onError()
            }
        })
    }
}
