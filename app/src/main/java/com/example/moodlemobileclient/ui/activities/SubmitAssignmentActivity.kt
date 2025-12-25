package com.example.moodlemobileclient.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.moodlemobileclient.data.repository.AssignmentRepository
import com.example.moodlemobileclient.utils.Constants
import com.example.moodlemobileclient.R
import com.example.moodlemobileclient.services.RetrofitClient
import com.example.moodlemobileclient.data.model.assignment.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class SubmitAssignmentActivity : AppCompatActivity() {

    private val apiToken = Constants.MOODLE_API_TOKEN
    private var selectedFileUri: Uri? = null
    private var assignmentId: Int = 0
    private var isTextExpanded = false

    private val assignmentRepository = AssignmentRepository()

    companion object {
        private const val PICK_FILE_REQUEST = 1001
    }

    private lateinit var txtSubmission: EditText
    private lateinit var btnChooseFile: Button
    private lateinit var btnSend: Button
    private lateinit var txtFileName: TextView
    private lateinit var headerText: LinearLayout
    private lateinit var ivArrowText: ImageView
    private lateinit var tvFilesTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submit_assignment)

        txtSubmission = findViewById(R.id.txtSubmission)
        btnChooseFile = findViewById(R.id.btnChooseFile)
        btnSend = findViewById(R.id.btnSend)
        txtFileName = findViewById(R.id.txtFileName)
        headerText = findViewById(R.id.headerTextOnline)
        ivArrowText = findViewById(R.id.ivArrowText)
        tvFilesTitle = findViewById(R.id.tvFilesTitle)

        assignmentId = intent.getIntExtra("assignmentId", 0)
        if (assignmentId == 0) {
            Toast.makeText(this, "Assignment ID inválido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // UI inicial
        txtSubmission.visibility = View.GONE
        headerText.visibility = View.GONE
        btnChooseFile.visibility = View.GONE
        txtFileName.visibility = View.GONE
        tvFilesTitle.visibility = View.GONE

        headerText.setOnClickListener {
            isTextExpanded = !isTextExpanded
            txtSubmission.visibility = if (isTextExpanded) View.VISIBLE else View.GONE
            ivArrowText.rotation = if (isTextExpanded) 180f else 0f
        }

        btnChooseFile.setOnClickListener { chooseFile() }

        btnSend.setOnClickListener {
            handleSubmit()
        }

        loadSubmissionConfig()
    }

    // ==================== SUBMIT ====================
    private fun handleSubmit() {
        getDraftItemId { draftId ->
            if (selectedFileUri != null) {
                uploadFileToDraft(selectedFileUri!!, draftId) {
                    saveSubmissionFiles(draftId)
                }
            } else {
                val text = txtSubmission.text.toString().trim()
                if (text.isEmpty()) {
                    Toast.makeText(this, "Escribe algo", Toast.LENGTH_SHORT).show()
                    return@getDraftItemId
                }
                saveSubmissionText(text)
            }
        }
    }


    // ==================== FILE PICKER ====================
    private fun chooseFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }
        startActivityForResult(intent, PICK_FILE_REQUEST)
    }



    private fun getFileNameFromUri(uri: Uri): String {
        var name = "Archivo seleccionado"
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex =
                cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst()) {
                name = cursor.getString(nameIndex)
            }
        }
        return name
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK) {
            selectedFileUri = data?.data
            txtFileName.text = "Archivo seleccionado ✔️"
        }

    }

    // ==================== DRAFT ====================
    private fun getDraftItemId(onSuccess: (Int) -> Unit) {
        assignmentRepository.getDraftItemId(
            token = Constants.MOODLE_API_TOKEN,
            onSuccess = { draftId ->
                onSuccess(draftId)
            },
            onError = {
                Toast.makeText(
                    this,
                    "No se pudo obtener draft",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }


    // ==================== ARCHIVOS ====================

    private fun uriToFile(uri: Uri): File {
        val inputStream = contentResolver.openInputStream(uri)
            ?: throw IllegalStateException("No se pudo abrir el archivo")

        val file = File(cacheDir, "upload_${System.currentTimeMillis()}")
        file.outputStream().use { inputStream.copyTo(it) }
        return file
    }

    private fun uploadFileToDraft(
        uri: Uri,
        draftId: Int,
        onSuccess: () -> Unit
    ) {
        val file = uriToFile(uri)

        val body = MultipartBody.Part.createFormData(
            "file",
            file.name,
            file.asRequestBody("application/octet-stream".toMediaTypeOrNull())
        )

        RetrofitClient.service.uploadFile(
            file = body,
            token = apiToken.toRequestBody("text/plain".toMediaTypeOrNull()),
            component = "user".toRequestBody("text/plain".toMediaTypeOrNull()),
            filearea = "draft".toRequestBody("text/plain".toMediaTypeOrNull()),
            itemid = draftId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        ).enqueue(object : Callback<ResponseBody> {

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    //Log.d("FLOW", "Archivo subido al draft OK")
                    onSuccess()
                } else {
                    Log.e("FLOW", "Upload error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("FLOW", "Upload error", t)
            }
        })
    }

    // ==================== SUBIR ARCHIVO ====================
    private fun saveSubmissionFiles(draftId: Int) {
        val mediaType = "text/plain".toMediaTypeOrNull()

        RetrofitClient.service.saveSubmissionFiles(
            token = apiToken.toRequestBody(mediaType),
            wsFunction = "mod_assign_save_submission".toRequestBody(mediaType),
            format = "json".toRequestBody(mediaType),

            assignmentId = assignmentId.toString().toRequestBody(mediaType),
            filesItemId = draftId.toString().toRequestBody(mediaType)
        ).enqueue(object : Callback<ResponseBody> {

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (!response.isSuccessful) {
                    Log.e("FLOW", "saveSubmissionFiles error")
                    return
                }
                submitForGrading()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("FLOW", "saveSubmissionFiles failure", t)
            }
        })
    }

    // ==================== SUBIR TEXTO ====================

    private fun saveSubmissionText(text: String) {
        val mediaType = "text/plain".toMediaTypeOrNull()

        RetrofitClient.service.saveSubmissionText(
            token = apiToken.toRequestBody(mediaType),
            wsFunction = "mod_assign_save_submission".toRequestBody(mediaType),
            format = "json".toRequestBody(mediaType),

            assignmentId = assignmentId.toString().toRequestBody(mediaType),

            text = text.toRequestBody(mediaType),
            textFormat = "1".toRequestBody(mediaType),

            //ItemId
            textItemId = "0".toRequestBody(mediaType)
        ).enqueue(object : Callback<ResponseBody> {

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (!response.isSuccessful) {
                    Log.e("FLOW", "saveSubmissionText error: ${response.code()}")
                    return
                }

                submitForGrading()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("FLOW", "saveSubmissionText failure", t)
            }
        })
    }

    // ==================== GUARDAR Y ENVIAR ====================

    private fun submitForGrading() {
        RetrofitClient.service.submitForGrading(
            token = apiToken,
            assignmentId = assignmentId
        ).enqueue(object : Callback<ResponseBody> {

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (!response.isSuccessful) {
                    Log.e("FLOW", "submitForGrading error")
                    return
                }

                Toast.makeText(
                    this@SubmitAssignmentActivity,
                    "Entrega enviada correctamente ✅",
                    Toast.LENGTH_LONG
                ).show()

                setResult(RESULT_OK)
                finish()

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("FLOW", "submitForGrading failure", t)
            }
        })
    }


    private fun loadSubmissionConfig() {
        RetrofitClient.service.getSubmissionStatus(token = apiToken,
            assignmentId = assignmentId)
            .enqueue(object : Callback<SubmissionStatusResponse> {

                override fun onResponse(
                    call: Call<SubmissionStatusResponse>,
                    response: Response<SubmissionStatusResponse>
                ) {
                    val plugins = response.body()
                        ?.lastattempt
                        ?.submission
                        ?.plugins ?: emptyList()

                    applySubmissionRules(plugins)
                }

                override fun onFailure(call: Call<SubmissionStatusResponse>, t: Throwable) {
                    Log.e("FLOW", "Error loading config", t)
                }
            })
    }


    private fun applySubmissionRules(plugins: List<SubmissionPlugin>) {
        val allowsText = plugins.any { it.type == "onlinetext" }
        val allowsFiles = plugins.any { it.type == "file" }

        if (allowsText) headerText.visibility = View.VISIBLE
        if (allowsFiles) {
            tvFilesTitle.visibility = View.VISIBLE
            btnChooseFile.visibility = View.VISIBLE
            txtFileName.visibility = View.VISIBLE
        }

        btnSend.isEnabled = allowsText || allowsFiles
    }
    private fun allowsOnlineText(plugins: List<SubmissionPlugin>) =
        plugins.any { it.type == "onlinetext" }

    private fun allowsFileUpload(plugins: List<SubmissionPlugin>) =
        plugins.any { it.type == "file" }

}


