package com.example.moodlemobileclient.ui.activities


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
import com.example.moodlemobileclient.utils.Constants
import com.example.moodlemobileclient.R
import com.example.moodlemobileclient.data.model.course.*
import com.example.moodlemobileclient.services.RetrofitClient
import com.example.moodlemobileclient.ui.adapters.SectionAdapter


class CourseActivitiesActivity : AppCompatActivity() {

    private val apiToken = Constants.MOODLE_API_TOKEN
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_activities)

        recyclerView = findViewById(R.id.rvSections)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val courseId = intent.getIntExtra("courseId", 0)
        val courseName = intent.getStringExtra("courseName") ?: "Curso"
        title = courseName

        getCourseActivities(courseId, courseName)
    }

    private fun getCourseActivities(courseId: Int, courseName: String) {
        RetrofitClient.service.getCourseActivities(
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

                recyclerView.adapter = SectionAdapter(

                    sections = sections,

                    ) { activity ->

                    when (activity.modname.lowercase(Locale.getDefault())) {

                        "forum" -> {
                            val intent = Intent(
                                this@CourseActivitiesActivity,
                                ForumDiscussionsActivity::class.java
                            )
                            intent.putExtra("forumId", activity.instance)
                            intent.putExtra("forumName", activity.name)
                            startActivity(intent)
                        }

                        "assign" -> {
                            val intent = Intent(
                                this@CourseActivitiesActivity,
                                AssignmentActivity::class.java
                            )
                            intent.putExtra("courseId", courseId)
                            intent.putExtra("cmid", activity.id)
                            startActivity(intent)
                        }

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
