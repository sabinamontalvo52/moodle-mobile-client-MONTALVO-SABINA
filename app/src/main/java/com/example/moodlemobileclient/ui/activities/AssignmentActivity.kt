package com.example.moodlemobileclient.ui.activities

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
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.example.moodlemobileclient.utils.Constants
import com.example.moodlemobileclient.R
import com.example.moodlemobileclient.services.RetrofitClient
import com.example.moodlemobileclient.data.model.assignment.AssignmentResponse
import com.example.moodlemobileclient.data.model.assignment.SubmissionStatusResponse


class AssignmentActivity : AppCompatActivity() {

    private val apiToken = Constants.MOODLE_API_TOKEN

    private lateinit var txtTitle: TextView
    private lateinit var txtDescription: TextView
    private lateinit var txtDueDate: TextView
    private lateinit var btnSubmit: Button

    private lateinit var txtSubmittedFiles: TextView

    private lateinit var txtSubmissionStatus: TextView
    private lateinit var txtGradingStatus: TextView
    private lateinit var txtLastModified: TextView

    private var assignmentId = 0
    private var courseId = 0
    private var cmid = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assignment)


        txtTitle = findViewById(R.id.txtTitle)
        txtDescription = findViewById(R.id.txtDescription)
        txtDueDate = findViewById(R.id.txtDueDate)
        btnSubmit = findViewById(R.id.btnSubmit)
        txtSubmissionStatus = findViewById(R.id.txtSubmissionStatus)
        txtGradingStatus = findViewById(R.id.txtGradingStatus)
        txtLastModified = findViewById(R.id.txtLastModified)

        txtSubmittedFiles = findViewById(R.id.txtSubmittedFiles)

        courseId = intent.getIntExtra("courseId", 0)
        cmid = intent.getIntExtra("cmid", 0)

        loadAssignment()

        btnSubmit.setOnClickListener {
            Log.d("DEBUG_ASSIGN", "assignmentId enviado: $assignmentId")

            val intent = Intent(this, SubmitAssignmentActivity::class.java)
            intent.putExtra("assignmentId", assignmentId)
            startActivityForResult(intent, 2001)

        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 2001 && resultCode == RESULT_OK) {
            Log.d("DEBUG_ASSIGN", "Regresó de SubmitAssignmentActivity → refrescando estado")
            loadSubmissionStatus()
        }
    }

   override fun onResume() {
        super.onResume()
    }

    private fun loadSubmissionStatus() {
        RetrofitClient.service.getSubmissionStatus(
            token = apiToken,
            assignmentId = assignmentId
        ).enqueue(object : Callback<SubmissionStatusResponse> {

            override fun onResponse(
                call: Call<SubmissionStatusResponse>,
                response: Response<SubmissionStatusResponse>
            ) {
                if (!response.isSuccessful) return
                val layoutHeader = findViewById<LinearLayout>(R.id.layoutTextSubmissionHeader)
                val txtSubmittedText = findViewById<TextView>(R.id.txtSubmittedText)
                val ivArrow = findViewById<ImageView>(R.id.ivTextArrow)

                //expandir/contraer
                var isExpanded = false

                layoutHeader.setOnClickListener {
                    isExpanded = !isExpanded

                    txtSubmittedText.visibility =
                        if (isExpanded) View.VISIBLE else View.GONE

                    ivArrow.setImageResource(
                        if (isExpanded) R.drawable.ic_arrow_down
                        else R.drawable.ic_arrow_right
                    )
                }

                val attempt = response.body()?.lastattempt
                val submission = attempt?.submission

                // ---------- ARCHIVOS ----------
                val filePlugin = submission
                    ?.plugins
                    ?.firstOrNull { it.type == "file" }

                val files = filePlugin
                    ?.fileareas
                    ?.firstOrNull { it.area == "submission_files" }
                    ?.files

                if (!files.isNullOrEmpty()) {
                    val fileNames = files.joinToString("\n") {
                        "📎 ${it.filename}"
                    }
                    txtSubmittedFiles.text = "Archivos enviados:\n$fileNames"
                } else {
                    txtSubmittedFiles.text = "Archivos enviados: ninguno"
                }

                // ---------- TEXTO ----------
                val textPlugin = submission
                    ?.plugins
                    ?.firstOrNull { it.type == "onlinetext" }

                val submittedText = textPlugin
                    ?.editorfields
                    ?.firstOrNull()
                    ?.text

                if (!submittedText.isNullOrBlank()) {
                    txtSubmittedText.text = submittedText
                    layoutHeader.visibility = View.VISIBLE
                } else {
                    layoutHeader.visibility = View.GONE
                    txtSubmittedText.visibility = View.GONE
                }

                // ---------- EXPANDIR / CONTRAER ----------

                layoutHeader.setOnClickListener {
                    isExpanded = !isExpanded

                    txtSubmittedText.visibility =
                        if (isExpanded) View.VISIBLE else View.GONE

                    ivArrow.setImageResource(
                        if (isExpanded) R.drawable.ic_arrow_down
                        else R.drawable.ic_arrow_right
                    )
                }
                // Estado de la entrega
                txtSubmissionStatus.text =
                    "Estado de la entrega: ${submission?.status ?: "No entregado"}"

                // Estado de la calificación
                txtGradingStatus.text =
                    "Estado de la calificación: ${attempt?.gradingstatus ?: "No calificado"}"

                // Última modificación
                submission?.timemodified?.let {
                    val date = Date(it * 1000)
                    val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    txtLastModified.text =
                        "Última modificación: ${formatter.format(date)}"
                }

                // Ocultar botón si ya fue entregada
                if (submission?.status == "submitted") {
                    btnSubmit.visibility = View.GONE
                } else {
                    btnSubmit.visibility = View.VISIBLE
                }

            }

            override fun onFailure(call: Call<SubmissionStatusResponse>, t: Throwable) {
                Log.e("ASSIGNMENT", "Error estado entrega", t)
            }
        })
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
                assignmentId = assignment.id
                txtTitle.text = assignment.name
                txtDescription.text = assignment.intro ?: "Sin descripción"
                val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                val date = Date(assignment.duedate * 1000)
                txtDueDate.text = "Fecha límite: ${formatter.format(date)}"
                loadSubmissionStatus()
            }

            override fun onFailure(call: Call<AssignmentResponse>, t: Throwable) {
                Toast.makeText(this@AssignmentActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
