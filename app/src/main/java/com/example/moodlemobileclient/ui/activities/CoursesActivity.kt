package com.example.moodlemobileclient.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moodlemobileclient.utils.Constants
import com.example.moodlemobileclient.ui.adapters.CoursesAdapter
import com.example.moodlemobileclient.R
import com.example.moodlemobileclient.services.RetrofitClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.moodlemobileclient.data.model.course.*
import com.example.moodlemobileclient.data.model.auth.*

class CoursesActivity : AppCompatActivity() {

    private val apiToken = Constants.MOODLE_API_TOKEN
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    private lateinit var rvCourses: RecyclerView
    private lateinit var btnLogout: Button
    private lateinit var coursesAdapter: CoursesAdapter
    private val coursesList = mutableListOf<CourseResponse>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_courses)

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance()

        // Configurar Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Referencias
        rvCourses = findViewById(R.id.rvCourses)
        rvCourses.layoutManager = LinearLayoutManager(this)
        rvCourses.setHasFixedSize(true)
        coursesAdapter = CoursesAdapter(coursesList) { course ->
            val intent = Intent(this, CourseActivitiesActivity::class.java)
            intent.putExtra("courseId", course.id)
            intent.putExtra("courseName", course.fullname)
            startActivity(intent)
        }
        rvCourses.adapter = coursesAdapter

        btnLogout = findViewById(R.id.btnLogout)
        btnLogout.setOnClickListener { logout() }

        getCourses()
    }

    private fun logout() {
        googleSignInClient.signOut().addOnCompleteListener {
            auth.signOut()
            val intent = Intent(this, WelcomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun getCourses() {
        val service = RetrofitClient.service

        service.getUserCourses(apiToken).enqueue(object : Callback<List<CourseResponse>> {
            override fun onResponse(
                call: Call<List<CourseResponse>>,
                response: Response<List<CourseResponse>>
            ) {
                if (!response.isSuccessful) {
                    Toast.makeText(this@CoursesActivity, "Error al obtener cursos", Toast.LENGTH_SHORT).show()
                    Log.e("COURSES", "Error en respuesta: ${response.code()} ${response.errorBody()?.string()}")
                    return
                }

                val courses = response.body() ?: emptyList()
                if (courses.isEmpty()) {
                    Toast.makeText(this@CoursesActivity, "No hay cursos disponibles", Toast.LENGTH_SHORT).show()
                    return
                }

                coursesList.clear()
                coursesList.addAll(courses)
                coursesAdapter.notifyDataSetChanged()

                // ======================================================
                //  OBTENER DOCENTES PARA CADA CURSO
                // ======================================================
                coursesList.forEach { course ->

                    // LOG CORRECTO
                    Log.d(
                        "DEBUG_COURSEID",
                        "Enviando courseid = ${course.id} (tipo=${course.id::class.java.name})"
                    )

                    // Enviar el id REAL, como Int
                    val courseId = course.id

                    service.getEnrolledUsersJson(apiToken, courseId = courseId)
                        .enqueue(object : Callback<JsonElement> {
                            override fun onResponse(
                                call: Call<JsonElement>,
                                response: Response<JsonElement>
                            ) {
                                if (!response.isSuccessful) {
                                    Log.e("COURSES", "Error HTTP al obtener docentes: ${response.code()}")
                                    return
                                }

                                val json = response.body()
                                if (json == null) {
                                    Log.e("COURSES", "Respuesta vacía de Moodle para curso ${course.fullname}")
                                    return
                                }

                                // Si Moodle devolvió error
                                if (json.isJsonObject && json.asJsonObject.has("exception")) {
                                    val error = json.asJsonObject.get("message").asString
                                    Log.e("COURSES", "Error Moodle curso ${course.fullname}: $error")
                                    return
                                }

                                if (json.isJsonArray) {
                                    val users = Gson().fromJson(json.asJsonArray, Array<EnrolledUser>::class.java).toList()

                                    users.forEach { user ->
                                        Log.d(
                                            "USERS_ROLES",
                                            "Curso: ${course.fullname}, Usuario: ${user.fullname}, Roles: ${
                                                user.roles?.joinToString { it.shortname }
                                                    ?: "sin roles"
                                            }"
                                        )
                                    }

                                    val teachers = users.filter { user ->
                                        user.roles?.any {
                                            it.shortname.equals("editingteacher", true) ||
                                                    it.shortname.equals("teacher", true)
                                        } == true
                                    }

                                    course.teachers = teachers
                                    coursesAdapter.notifyDataSetChanged()

                                    Log.d(
                                        "COURSES",
                                        "Docentes encontrados para ${course.fullname}: ${teachers.map { it.fullname }}"
                                    )
                                } else {
                                    Log.e("COURSES", "Formato inesperado de respuesta Moodle para curso ${course.fullname}: $json")
                                }
                            }

                            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                                Log.e("COURSES", "Fallo al obtener docentes: $t")
                            }
                        })
                }

                Log.d("COURSES", "Cursos recibidos: ${coursesList.size}")
            }

            override fun onFailure(call: Call<List<CourseResponse>>, t: Throwable) {
                Toast.makeText(this@CoursesActivity, "Fallo al cargar cursos: $t", Toast.LENGTH_LONG).show()
                Log.e("COURSES", "Fallo al cargar cursos", t)
            }
        })
    }







}
