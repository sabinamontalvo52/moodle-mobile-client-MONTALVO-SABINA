package com.example.moodlemobileclient.data.model.forum

data class ForumResponse(
    val id: Int,
    val course: Int,
    val type: String?,
    val name: String?,
    val intro: String?,
    val cmid: Int?,
    val numdiscussions: Int?,
    val cancreatediscussions: Boolean?
)
