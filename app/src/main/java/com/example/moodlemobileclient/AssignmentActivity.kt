package com.example.moodlemobileclient

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class AssignmentActivity : AppCompatActivity() {

    private val apiToken = "a2a6f9e1c4b7809aae2b3891c91e15fd"

    private lateinit var txtTitle: TextView
    private lateinit var txtDescription: TextView
    private lateinit var txtDueDate: TextView
    private lateinit var btnSubmit: Button

    private var assignmentId = 0
    private var courseId = 0
    private var cmid = 0   // 🔥 OJO: ahora usamos cmid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assignment)

        txtTitle = findViewById(R.id.txtTitle)
        txtDescription = findViewById(R.id.txtDescription)
        txtDueDate = findViewById(R.id.txtDueDate)
        btnSubmit = findViewById(R.id.btnSubmit)

        courseId = intent.getIntExtra("courseId", 0)
        cmid = intent.getIntExtra("cmid", 0)

        loadAssignment()

        btnSubmit.setOnClickListener {
            Log.d("DEBUG_ASSIGN", "assignmentId enviado: $assignmentId")

            val intent = Intent(this, SubmitAssignmentActivity::class.java)
            intent.putExtra("assignmentId", assignmentId)
            startActivity(intent)
        }

    }

    private fun loadAssignment() {
        RetrofitClient.service.getAssignments(
            token = apiToken,
            function = "mod_assign_get_assignments",
            format = "json",
            courseId = courseId
        ).enqueue(object : Callback<AssignmentResponse> {

            override fun onResponse(
                call: Call<AssignmentResponse>,
                response: Response<AssignmentResponse>
            ) {
                if (!response.isSuccessful) {
                    Toast.makeText(this@AssignmentActivity, "Error al cargar tarea", Toast.LENGTH_SHORT).show()
                    return
                }

                val assignment = response.body()
                    ?.courses
                    ?.flatMap { it.assignments }
                    ?.firstOrNull { it.cmid == cmid } //cmid (id del modulo)

                if (assignment == null) {
                    Toast.makeText(this@AssignmentActivity, "Tarea no encontrada", Toast.LENGTH_SHORT).show()
                    return
                }
                assignmentId = assignment.id   // 🔥 ESTA LÍNEA ES LA CLAVE


                txtTitle.text = assignment.name
                txtDescription.text = assignment.intro ?: "Sin descripción"

                val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                val date = Date(assignment.duedate * 1000)
                txtDueDate.text = "Fecha límite: ${formatter.format(date)}"
            }

            override fun onFailure(call: Call<AssignmentResponse>, t: Throwable) {
                Toast.makeText(this@AssignmentActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
