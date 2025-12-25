package com.example.moodlemobileclient.data.model.forum

data class ForumItem(
    val id: Int,
    val name: String,
    val type: String?,
    val numdiscussions: Int,
    val cancreatediscussions: Boolean
)
