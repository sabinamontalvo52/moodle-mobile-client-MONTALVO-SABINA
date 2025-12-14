package com.example.moodlemobileclient

import com.google.gson.JsonElement
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface MoodleApiService {

    // Obtener los cursos de un usuario
    @GET("webservice/rest/server.php")
    fun getUserCourses(
        @Query("wstoken") token: String,
        @Query("wsfunction") function: String = "core_enrol_get_users_courses",
        @Query("moodlewsrestformat") format: String = "json",
        @Query("userid") userId: Int = 2
    ): Call<List<CourseResponse>>

    // Obtener usuarios inscritos en un curso
    @GET("webservice/rest/server.php")
    fun getEnrolledUsersJson(
        @Query("wstoken") token: String,
        @Query("wsfunction") function: String = "core_enrol_get_enrolled_users",
        @Query("moodlewsrestformat") format: String = "json",
        @Query("courseid") courseId: Int
    ): Call<JsonElement>

    // Información del sitio Moodle
    @GET("webservice/rest/server.php")
    fun getSiteInfo(
        @Query("wstoken") token: String,
        @Query("wsfunction") function: String = "core_webservice_get_site_info",
        @Query("moodlewsrestformat") format: String = "json"
    ): Call<SiteInfoResponse>

    // Obtener las actividades de un curso
    @GET("webservice/rest/server.php")
    fun getCourseActivities(
        @Query("wstoken") token: String,
        @Query("wsfunction") function: String = "core_course_get_contents",
        @Query("moodlewsrestformat") format: String = "json",
        @Query("courseid") courseId: String
    ): Call<List<CourseSection>>

    // ==================== TAREAS ====================

    // ==================== OBTENER TAREAS ====================
    @GET("webservice/rest/server.php")
    fun getAssignments(
        @Query("wstoken") token: String,
        @Query("wsfunction") function: String = "mod_assign_get_assignments",
        @Query("moodlewsrestformat") format: String = "json",
        @Query("courseids[0]") courseId: Int
    ): Call<AssignmentResponse>

    // ==================== OBTENER DRAFT ID ====================
    @GET("webservice/rest/server.php")
    fun getDraftItemId(
        @Query("wstoken") token: String,
        @Query("wsfunction") function: String = "core_files_get_unused_draft_itemid",
        @Query("moodlewsrestformat") format: String = "json"
    ): Call<DraftItemIdResponse>

    // ==================== SUBIR ARCHIVO AL DRAFT ====================
    @Multipart
    @POST("webservice/upload.php")
    fun uploadFile(
        @Part file: MultipartBody.Part,
        @Part("token") token: RequestBody,
        @Part("filearea") filearea: RequestBody,
        @Part("itemid") itemid: RequestBody,
        @Part("filepath") filepath: RequestBody,
        @Part("filename") filename: RequestBody
    ): Call<ResponseBody>

    // ==================== ENVIAR ENTREGA (CLAVE) ====================
      @FormUrlEncoded
    @POST("webservice/rest/server.php")
    fun saveSubmission(
        @Field("wstoken") token: String,
        @Field("wsfunction") function: String = "mod_assign_save_submission",
        @Field("moodlewsrestformat") format: String = "json",
        @Field("assignmentid") assignmentId: Int,
        @Field("plugindata[onlinetext_editor][text]") text: String,
        @Field("plugindata[onlinetext_editor][format]") textFormat: Int = 1,
        @Field("plugindata[files_filemanager][itemid]") draftItemId: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("webservice/rest/server.php")
    fun startSubmission(
        @Field("wstoken") token: String,
        @Field("wsfunction") function: String = "mod_assign_start_submission",
        @Field("moodlewsrestformat") format: String = "json",
        @Field("assignmentid") assignmentId: Int
    ): Call<ResponseBody>

    @GET("webservice/rest/server.php")
    fun getSubmissionStatus(
        @Query("wstoken") token: String,
        @Query("wsfunction") function: String = "mod_assign_get_submission_status",
        @Query("moodlewsrestformat") format: String = "json",
        @Query("assignid") assignmentId: Int
    ): Call<SubmissionStatusResponse>

    @FormUrlEncoded
    @POST("webservice/rest/server.php")
    fun submitForGrading(
        @Field("wstoken") token: String,
        @Field("wsfunction") function: String = "mod_assign_submit_for_grading",
        @Field("moodlewsrestformat") format: String = "json",
        @Field("assignmentid") assignmentId: Int
    ): Call<ResponseBody>

    //@Multipart
    //@POST("webservice/rest/server.php")
    //fun saveSubmissionWithFormat(
        //@Part("wstoken") token: String,
        //@Part("assignmentid") assignmentId: RequestBody,
      //  @Part("plugindata[onlinetext_editor][text]") text: RequestBody,
    //    @Part("plugindata[files_filemanager][itemid]") draftItemId: RequestBody,
  //      @Part("plugindata[onlinetext_editor][format]") textFormat: RequestBody
//): Call<ResponseBody>


// ======================= FOROS =======================

    // Obtener foros de un curso
    @GET("webservice/rest/server.php")
    suspend fun getForumsByCourse(
        @Query("wstoken") token: String,
        @Query("wsfunction") function: String = "mod_forum_get_forums_by_courses",
        @Query("moodlewsrestformat") format: String = "json",
        @Query("courseids[0]") courseId: Int
    ): List<ForumResponse>


    // Obtener discusiones de un foro (CORRECTO)
    @GET("webservice/rest/server.php")
    suspend fun getForumDiscussions(
        @Query("wstoken") token: String,
        @Query("wsfunction") function: String = "mod_forum_get_forum_discussions",
        @Query("moodlewsrestformat") format: String = "json",
        @Query("forumid") forumId: Int
    ): ForumDiscussionsResponse


    // Obtener posts (réplicas) de una discusión
    @GET("webservice/rest/server.php")
    suspend fun getDiscussionPosts(
        @Query("wstoken") token: String,
        @Query("wsfunction") function: String = "mod_forum_get_discussion_posts",
        @Query("moodlewsrestformat") format: String = "json",
        @Query("discussionid") discussionId: Int
    ): DiscussionPostsResponse

    // Añadir discusión

    @POST("webservice/rest/server.php")
    @FormUrlEncoded
    suspend fun addDiscussion(
        @Field("wstoken") token: String,
        @Field("wsfunction") function: String = "mod_forum_add_discussion",
        @Field("moodlewsrestformat") format: String = "json",
        @Field("forumid") forumId: Int,
        @Field("subject") subject: String,
        @Field("message") message: String
    ): ResponseBody






}
