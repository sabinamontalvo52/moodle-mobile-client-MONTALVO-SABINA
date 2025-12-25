package com.example.moodlemobileclient.data.model.course

import com.example.moodlemobileclient.data.model.auth.EnrolledUser

data class CourseResponse(
    val id: Int,
    val fullname: String,
    val shortname: String?,
    val summary: String?,
    var teachers: List<EnrolledUser> = emptyList()
)
