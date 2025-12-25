package com.example.moodlemobileclient.data.model.forum

data class Author(
    val id: Int,
    val fullname: String,
    val isdeleted: Boolean,
    val urls: AuthorUrls
)
