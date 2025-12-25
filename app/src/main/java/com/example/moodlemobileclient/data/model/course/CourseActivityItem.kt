package com.example.moodlemobileclient.data.model.course

data class CourseActivityItem(
    val id: Int,
    val name: String,
    val modname: String,
    val url: String,
    val modicon: String,
    val cmid: Int?,
    val instance: Int,
    val dates: List<ActivityDate>?
)
