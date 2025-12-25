package com.example.moodlemobileclient.data.model.assignment

data class SubmissionStatusResponse(
    val gradingsummary: GradingSummary?,
    val lastattempt: LastAttempt?,
    val assignmentdata: AssignmentData?,
    val warnings: List<Any>?
)
