package com.example.moodlemobileclient.data.model.forum

data class ForumPost(
    val id: Int,
    val subject: String?,
    val message: String?,
    val discussionid: Int,
    val parentid: Int?,
    val hasparent: Boolean,
    val timecreated: Long,
    val author: Author
)
