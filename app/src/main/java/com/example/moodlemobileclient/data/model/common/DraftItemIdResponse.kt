package com.example.moodlemobileclient.data.model.common

data class DraftItemIdResponse(
    val component: String,
    val contextid: Int,
    val userid: Int,
    val filearea: String,
    val itemid: Int,
    val warnings: List<Any>
)
