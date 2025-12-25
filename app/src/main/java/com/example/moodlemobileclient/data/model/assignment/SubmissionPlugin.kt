package com.example.moodlemobileclient.data.model.assignment

data class SubmissionPlugin(
    val type: String?,
    val name: String?,
    val fileareas: List<FileArea>?,
    val editorfields: List<EditorField>?
)
