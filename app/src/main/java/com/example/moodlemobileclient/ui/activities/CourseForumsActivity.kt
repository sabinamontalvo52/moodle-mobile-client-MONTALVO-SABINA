package com.example.moodlemobileclient.ui.activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moodlemobileclient.R
import com.example.moodlemobileclient.data.model.forum.*
import com.example.moodlemobileclient.data.repository.ForumRepository
import com.example.moodlemobileclient.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CourseForumsActivity : AppCompatActivity() {

    private val apiToken = Constants.MOODLE_API_TOKEN
    private val forumRepository = ForumRepository()

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_forums)

        recyclerView = findViewById(R.id.rvForums)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        val courseId = intent.getIntExtra("courseId", 0)
        val courseName = intent.getStringExtra("courseName") ?: "Curso"
        title = "$courseName - Foros"

        getCourseForums(courseId)
    }

    private fun getCourseForums(courseId: Int) {
        lifecycleScope.launch {
            try {
                val forumsResponseList = withContext(Dispatchers.IO) {
                    forumRepository.getForumsByCourse(
                        token = apiToken,
                        courseId = courseId
                    )
                }

                val forums: List<ForumItem> = forumsResponseList.map { resp ->
                    ForumItem(
                        id = resp.id,
                        name = resp.name ?: "Foro",
                        type = resp.type,
                        numdiscussions = resp.numdiscussions ?: 0,
                        cancreatediscussions = resp.cancreatediscussions ?: false
                    )
                }

                if (forums.isEmpty()) {
                    Toast.makeText(
                        this@CourseForumsActivity,
                        "No hay foros disponibles",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }



            } catch (e: Exception) {
                Log.e("COURSE_FORUMS", "Error al obtener foros", e)
                Toast.makeText(
                    this@CourseForumsActivity,
                    "Error al cargar foros",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
