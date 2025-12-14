package com.example.moodlemobileclient

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

    private val apiToken = "a2a6f9e1c4b7809aae2b3891c91e15fd"
    private var selectedFileUri: Uri? = null
    private var assignmentId = 0

    companion object {
        private const val PICK_FILE_REQUEST = 1001
    }

    private lateinit var txtSubmission: EditText
    private lateinit var btnChooseFile: Button
    private lateinit var btnSend: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submit_assignment)

        txtSubmission = findViewById(R.id.txtSubmission)
        btnChooseFile = findViewById(R.id.btnChooseFile)
        btnSend = findViewById(R.id.btnSend)

        assignmentId = intent.getIntExtra("assignmentId", 0)
        Log.d("FLOW", "assignmentId recibido: $assignmentId")

        btnChooseFile.setOnClickListener { chooseFile() }

        btnSend.setOnClickListener {
            val text = txtSubmission.text.toString()
            if (text.isBlank() && selectedFileUri == null) {
                Toast.makeText(this, "Escribe tu entrega o selecciona un archivo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 🔹 Paso 1: Obtener draftItemId siempre
            getDraftItemId { draftId ->
                if (selectedFileUri != null) {
                    // 🔹 Paso 2: Subir archivo al draft
                    uploadFileToDraft(selectedFileUri!!, draftId) {
                        // 🔹 Paso 3: Guardar la entrega con texto y archivo
                        saveAndSubmit(text, draftId)
                    }
                } else {
                    // Solo texto
                    saveAndSubmit(text, draftId)
                }
            }
        }
    }

    // ==================== OBTENER DRAFT ====================
    private fun getDraftItemId(onSuccess: (String) -> Unit) {
        RetrofitClient.service.getDraftItemId(apiToken)
            .enqueue(object : Callback<DraftItemIdResponse> {
                override fun onResponse(
                    call: Call<DraftItemIdResponse>,
                    response: Response<DraftItemIdResponse>
                ) {
                    val draftId = response.body()?.itemid?.toString()
                    if (draftId != null) {
                        Log.d("FLOW", "Draft obtenido: $draftId")
                        onSuccess(draftId)
                    } else {
                        Toast.makeText(this@SubmitAssignmentActivity, "No se pudo obtener draft", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<DraftItemIdResponse>, t: Throwable) {
                    Log.e("FLOW", "Error al obtener draft", t)
                    Toast.makeText(this@SubmitAssignmentActivity, "Error de conexión al obtener draft", Toast.LENGTH_SHORT).show()
                }
            })
    }

    // ==================== FILE PICKER ====================
    private fun chooseFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        startActivityForResult(intent, PICK_FILE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK) {
            selectedFileUri = data?.data
            Toast.makeText(this, "Archivo seleccionado ✔️", Toast.LENGTH_SHORT).show()
        }
    }

    // ==================== SUBIDA DE ARCHIVO ====================
    private fun uriToFile(uri: Uri): File {
        val inputStream = contentResolver.openInputStream(uri)!!
        val file = File(cacheDir, "upload_${System.currentTimeMillis()}")
        file.outputStream().use { inputStream.copyTo(it) }
        return file
    }

    private fun uploadFileToDraft(uri: Uri, draftItemId: String, onSuccess: () -> Unit) {
        val file = uriToFile(uri)

        val body = MultipartBody.Part.createFormData(
            "file",
            file.name,
            file.asRequestBody("application/octet-stream".toMediaTypeOrNull())
        )

        RetrofitClient.service.uploadFile(
            file = body,
            token = apiToken.toRequestBody("text/plain".toMediaTypeOrNull()),
            filearea = "draft".toRequestBody("text/plain".toMediaTypeOrNull()),
            itemid = draftItemId.toRequestBody("text/plain".toMediaTypeOrNull()),
            filepath = "/".toRequestBody("text/plain".toMediaTypeOrNull()),
            filename = file.name.toRequestBody("text/plain".toMediaTypeOrNull())
        ).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d("FLOW", "Archivo subido OK")
                onSuccess()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("FLOW", "Error upload", t)
                Toast.makeText(this@SubmitAssignmentActivity, "Error al subir archivo", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // ==================== GUARDAR Y ENVIAR ====================
    private fun saveAndSubmit(text: String, draftItemId: String) {

        // 1️⃣ INICIAR SUBMISSION (OBLIGATORIO)
        RetrofitClient.service.startSubmission(
            token = apiToken,
            assignmentId = assignmentId
        ).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                Log.d("FLOW", "start_submission OK")

                // 2️⃣ GUARDAR SUBMISSION
                RetrofitClient.service.saveSubmission(
                    token = apiToken,
                    assignmentId = assignmentId,
                    text = text,
                    draftItemId = draftItemId
                ).enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                        Log.d("FLOW", "save_submission OK")

                        // 3️⃣ ENVIAR PARA CALIFICAR
                        RetrofitClient.service.submitForGrading(
                            token = apiToken,
                            assignmentId = assignmentId
                        ).enqueue(object : Callback<ResponseBody> {
                            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                                Toast.makeText(
                                    this@SubmitAssignmentActivity,
                                    "Entrega enviada correctamente ✅",
                                    Toast.LENGTH_LONG
                                ).show()
                                finish()
                            }

                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                Log.e("FLOW", "submit_for_grading error", t)
                            }
                        })
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.e("FLOW", "save_submission error", t)
                    }
                })
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("FLOW", "start_submission error", t)
            }
        })
    }


}
