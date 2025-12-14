package com.example.moodlemobileclient
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

data class SiteInfoResponse(
    val sitename: String,
    val username: String?,
    val firstname: String?,
    val lastname: String?,
    val userid: Int?
)

data class UploadedFileResponse(
    val itemid: Int,
    val filepath: String?,
    val filename: String?,
    val fileurl: String?
)


data class CourseResponse(
    val id: Int,
    val fullname: String,
    val shortname: String?,
    val summary: String?, // otros campos Moodle
    var teachers: List<EnrolledUser> = emptyList()
)

data class UserRole(
    val roleid: Int,
    val shortname: String
)

data class Role(
    val roleid: Int,
    val name: String?,
    val shortname: String,
    val sortorder: Int
)

data class EnrolledCourse(
    val id: Int,
    val fullname: String,
    val shortname: String
)

data class ActivitySection(
    val id: Int,
    val name: String,
    val section: Int,
    val modules: List<ActivityModule>
)

data class ActivityModule(
    val id: Int,
    val name: String,
    val modname: String,   // "assign", "forum", "resource", etc.
    val url: String
)

data class CourseActivityItem(
    val id: Int,              // cmid
    val name: String,
    val modname: String,
    val url: String,
    val modicon: String,
    val cmid: Int?,
    val instance: Int,        // ID FORO
    val dates: List<ActivityDate>?
)

data class ActivityDate(
    val label: String,
    val timestamp: Long,
    val dataid: String
)

data class CourseForum(
    val id: Int,
    val name: String,
    val type: String,
    val numdiscussions: Int,
    val cancreatediscussions: Boolean
)

data class CourseSection(
    val id: Int,
    val name: String,
    val section: Int,
    val modules: List<CourseActivityItem>
)

data class ForumResponse(
    val id: Int,
    val course: Int,
    val type: String?,
    val name: String?,
    val intro: String?,
    val cmid: Int?,
    val numdiscussions: Int?,
    val cancreatediscussions: Boolean?
)

data class ForumItem(
    val id: Int,
    val name: String,
    val type: String?,
    val numdiscussions: Int,
    val cancreatediscussions: Boolean
)

data class DiscussionResponse(
    val id: Int,
    val name: String?,
    val subject: String?,
    val message: String?,
    val userfullname: String?,
    val timecreated: Long?
)


data class DiscussionItem(
    val id: Int,
    val subject: String,
    val message: String,
    val authorName: String
)
data class ForumDiscussionsResponse(
    val discussions: List<DiscussionResponse>?
)

data class DiscussionPostsResponse(
    val posts: List<ForumPost>?
)

data class ForumPost(
    val id: Int,
    val subject: String?,
    val message: String?,
    val parentid: Int?,
    val author: Author
)

data class Author(
    val fullname: String
)


data class AssignmentResponse(
    val courses: List<AssignmentCourse>
)

data class AssignmentCourse(
    val id: Int,
    val assignments: List<Assignment>
)

data class Assignment(
    val id: Int,
    val cmid: Int,
    val name: String,
    val intro: String?,
    val duedate: Long
)

data class SaveSubmissionResponse(
    val status: Boolean,
    val warnings: List<Any>?
)

data class GenericResponse(
    val status: Boolean? = true
)


data class FileUploadResponse(
    val component: String,
    val filearea: String,
    val itemid: Int,
    val filepath: String,
    val filename: String,
    val filesize: Int,
    val fileurl: String,
    val mimetype: String,
    val status: Boolean,
    val warnings: List<Any>
)

data class DraftItemResponse(
    val component: String,
    val contextid: Int,
    val userid: Int,
    val filearea: String,
    val itemid: Int,
    val warnings: List<Any>
)

data class SubmitResponse(
    val status: Boolean,
    val warnings: List<Any>?
)

data class DraftItemIdResponse(
    val component: String,
    val contextid: Int,
    val userid: Int,
    val filearea: String,
    val itemid: Int,
    val warnings: List<Any>
)

data class SubmissionStatusResponse(
    val gradingsummary: GradingSummary?,
    val lastattempt: LastAttempt?,
    val assignmentdata: AssignmentData?,
    val warnings: List<Any>?
)

data class GradingSummary(
    val participantcount: Int?,
    val submissiondraftscount: Int?,
    val submissionsenabled: Boolean?,
    val submissionssubmittedcount: Int?,
    val submissionsneedgradingcount: Int?,
    val warnofungroupedusers: String?
)

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

data class Submission(
    val id: Int?,
    val userid: Int?,
    val attemptnumber: Int?,
    val timecreated: Long?,
    val timemodified: Long?,
    val timestarted: Long?,
    val status: String?, // 🔥 "new", "draft", "submitted"
    val groupid: Int?,
    val assignment: Int?,
    val latest: Int?,
    val plugins: List<SubmissionPlugin>?
)

data class SubmissionPlugin(
    val type: String?,
    val name: String?,
    val fileareas: List<FileArea>?,
    val editorfields: List<EditorField>?
)


data class FileArea(
    val area: String?,
    val files: List<MoodleFile>?
)

data class MoodleFile(
    val filename: String?,
    val filepath: String?,
    val filesize: Int?,
    val fileurl: String?,
    val timemodified: Long?
)

data class EditorField(
    val name: String?,
    val description: String?,
    val text: String?,
    val format: Int?
)

data class AssignmentData(
    val attachments: Attachments?
)

data class Attachments(
    val intro: List<Any>?
)
