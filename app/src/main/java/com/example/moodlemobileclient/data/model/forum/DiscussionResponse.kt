package com.example.moodlemobileclient.data.model.forum

data class DiscussionResponse(
    val id: Int,
    val discussion: Int,
    val subject: String?,
    val message: String?,
    val userfullname: String?,
    val created: Long,
    val modified: Long?,
    val timemodified: Long?,
    val numreplies: Int
)
