package com.example.moodlemobileclient.data.model.assignment

data class Assignment(
    val id: Int,
    val cmid: Int,
    val name: String,
    val intro: String?,
    val duedate: Long,
    val configs: List<AssignConfig>?
)
