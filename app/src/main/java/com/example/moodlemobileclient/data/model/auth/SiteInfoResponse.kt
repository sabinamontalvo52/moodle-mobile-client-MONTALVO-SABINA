package com.example.moodlemobileclient.data.model.auth

data class SiteInfoResponse(
    val sitename: String,
    val username: String?,
    val firstname: String?,
    val lastname: String?,
    val userid: Int?
)
