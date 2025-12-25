package com.example.moodlemobileclient.data.model.assignment

data class LastAttempt(
    val submission: Submission?,
    val submissiongroupmemberswhoneedtosubmit: List<Any>?,
    val submissionsenabled: Boolean?,
    val locked: Boolean?,
    val graded: Boolean?,
    val canedit: Boolean?,
    val caneditowner: Boolean?,
    val cansubmit: Boolean?,
    val extensionduedate: Long?,
    val timelimit: Int?,
    val blindmarking: Boolean?,
    val gradingstatus: String?,
    val usergroups: List<Any>?
)
