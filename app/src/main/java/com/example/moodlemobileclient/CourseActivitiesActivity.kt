package com.example.moodlemobileclient


import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale
import android.content.Intent


class CourseActivitiesActivity : AppCompatActivity() {

    private val apiToken = "a2a6f9e1c4b7809aae2b3891c91e15fd"
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_activities)

        recyclerView = findViewById(R.id.rvActivities)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        val courseId = intent.getIntExtra("courseId", 0)
        val courseName = intent.getStringExtra("courseName") ?: "Curso"
        title = courseName
        getCourseActivities(courseId, courseName)
    }

    private fun getCourseActivities(courseId: Int, courseName: String) {
        val service = RetrofitClient.service

        service.getCourseActivities(
            token = apiToken,
            function = "core_course_get_contents",
            format = "json",
            courseId = courseId.toString()
        ).enqueue(object : Callback<List<CourseSection>> {

            override fun onResponse(
                call: Call<List<CourseSection>>,
                response: Response<List<CourseSection>>
            ) {
                if (!response.isSuccessful) {
                    Toast.makeText(
                        this@CourseActivitiesActivity,
                        "Error al cargar actividades",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }

                val sections = response.body().orEmpty()
                val activities = sections.flatMap { it.modules }

                recyclerView.adapter = CourseActivitiesAdapter(
                    context = this@CourseActivitiesActivity,
                    activities = activities,
                    courseId = courseId,
                    courseName = courseName
                ) { activity ->

                    when (activity.modname.lowercase(Locale.getDefault())) {

                        "forum" -> {

                            val forumId = activity.instance

                            val intent = Intent(
                                this@CourseActivitiesActivity,
                                ForumDiscussionsActivity::class.java
                            )
                            intent.putExtra("forumId", forumId)
                            intent.putExtra("forumName", activity.name)

                            Log.d(
                                "FORUM_DEBUG",
                                "Abriendo foro: ${activity.name} | forumId=$forumId"
                            )

                            startActivity(intent)
                        }

                        "assign" -> {
                            val intent = Intent(
                                this@CourseActivitiesActivity,
                                AssignmentActivity::class.java
                            )
                            intent.putExtra("courseId", courseId)
                            intent.putExtra("cmid", activity.id)
                            startActivity(intent)}

                        else -> {
                            Toast.makeText(
                                this@CourseActivitiesActivity,
                                "Actividad no soportada",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<CourseSection>>, t: Throwable) {
                Log.e("COURSE_ACTIVITIES", "Falla en la petición", t)
                Toast.makeText(
                    this@CourseActivitiesActivity,
                    "Error al cargar actividades",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}
