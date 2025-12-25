package com.example.moodlemobileclient.data.model.forum

data class PostItem(
    val id: Int,
    val subject: String,
    val message: String,
    val authorName: String,
    val parentId: Int,
    val repliesCount: Int = 0
)
