package com.example.moodlemobileclient.data.model.course

data class CourseSection(
    val id: Int,
    val name: String,
    val section: Int,
    val modules: List<CourseActivityItem>,
    var isExpanded: Boolean = false
)
