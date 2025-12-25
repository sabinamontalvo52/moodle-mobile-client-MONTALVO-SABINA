package com.example.moodlemobileclient.data.model.assignment

data class GradingSummary(
    val participantcount: Int?,
    val submissiondraftscount: Int?,
    val submissionsenabled: Boolean?,
    val submissionssubmittedcount: Int?,
    val submissionsneedgradingcount: Int?,
    val warnofungroupedusers: String?
)
