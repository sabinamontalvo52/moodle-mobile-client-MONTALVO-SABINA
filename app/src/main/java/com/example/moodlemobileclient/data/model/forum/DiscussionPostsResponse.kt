package com.example.moodlemobileclient.data.model.forum

data class DiscussionPostsResponse(
    val posts: List<ForumPost>?,
    val forumid: Int?,
    val courseid: Int?,
    val warnings: List<Any>?
)
