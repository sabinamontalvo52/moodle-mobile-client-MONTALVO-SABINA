package com.example.moodlemobileclient.data.model.result

import com.example.moodlemobileclient.data.model.common.MoodleException

sealed class DiscussionPostsResult {
    data class Error(val error: MoodleException) : DiscussionPostsResult()
}
