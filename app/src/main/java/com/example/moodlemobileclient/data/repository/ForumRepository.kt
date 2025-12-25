package com.example.moodlemobileclient.data.repository

import com.example.moodlemobileclient.services.RetrofitClient
import okhttp3.ResponseBody
import retrofit2.Response
import com.example.moodlemobileclient.data.model.forum.*

class ForumRepository {

    private val api = RetrofitClient.service

    // ================= FOROS POR CURSO =================
    suspend fun getForumsByCourse(
        token: String,
        courseId: Int
    ): List<ForumResponse> {
        return api.getForumsByCourse(
            token = token,
            courseId = courseId
        )
    }

    // ================= DISCUSIONES =================
    suspend fun getForumDiscussions(
        token: String,
        forumId: Int
    ): ForumDiscussionsResponse {
        return api.getForumDiscussions(
            token = token,
            forumId = forumId
        )
    }

    // ================= POSTS =================
    suspend fun getDiscussionPosts(
        token: String,
        discussionId: Int
    ): DiscussionPostsResponse {
        return api.getDiscussionPosts(
            token = token,
            discussionId = discussionId
        )
    }

    // ================= AÑADIR DISCUSIÓN =================
    suspend fun addDiscussion(
        token: String,
        forumId: Int,
        subject: String,
        message: String
    ): Response<ResponseBody> {
        return api.addDiscussion(
            token = token,
            forumId = forumId,
            subject = subject,
            message = message
        )
    }

    // ================= RESPONDER POST (CALLBACK) =================
    fun replyPost(
        token: String,
        postId: Int,
        subject: String,
        message: String,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        api.replyPost(
            token = token,
            postId = postId,
            subject = subject,
            message = message
        ).enqueue(object : retrofit2.Callback<Void> {

            override fun onResponse(
                call: retrofit2.Call<Void>,
                response: retrofit2.Response<Void>
            ) {
                if (response.isSuccessful) onSuccess()
                else onError()
            }

            override fun onFailure(call: retrofit2.Call<Void>, t: Throwable) {
                onError()
            }
        })
    }
}
