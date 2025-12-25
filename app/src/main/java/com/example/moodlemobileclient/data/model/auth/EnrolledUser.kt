package com.example.moodlemobileclient.data.model.auth

import com.google.gson.annotations.SerializedName

data class EnrolledUser(
    val id: Int,
    val username: String,
    val firstname: String,
    val lastname: String,
    val fullname: String,
    val email: String?,
    val roles: List<Role>,
    @SerializedName("enrolledcourses")
    val enrolledCourses: List<EnrolledCourse> = emptyList()
)
