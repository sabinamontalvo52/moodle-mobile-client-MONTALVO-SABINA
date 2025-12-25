package com.example.moodlemobileclient.data.model.assignment

data class Submission(
    val id: Int?,
    val userid: Int?,
    val attemptnumber: Int?,
    val timecreated: Long?,
    val timemodified: Long?,
    val timestarted: Long?,
    val status: String?,
    val groupid: Int?,
    val assignment: Int?,
    val latest: Int?,
    val plugins: List<SubmissionPlugin>?
)
