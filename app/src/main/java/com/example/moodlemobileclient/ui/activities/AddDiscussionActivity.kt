package com.example.moodlemobileclient.ui.activities

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.moodlemobileclient.R
import com.example.moodlemobileclient.data.repository.ForumRepository
import com.example.moodlemobileclient.utils.Constants
import kotlinx.coroutines.launch

class AddDiscussionActivity : AppCompatActivity() {

    private val apiToken = Constants.MOODLE_API_TOKEN
    private val forumRepository = ForumRepository()

    private lateinit var etDiscussionSubject: EditText
    private lateinit var etDiscussionMessage: EditText
    private lateinit var btnSubmitDiscussion: Button

    private var forumId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_discussion)

        etDiscussionSubject = findViewById(R.id.etDiscussionSubject)
        etDiscussionMessage = findViewById(R.id.etDiscussionMessage)
        btnSubmitDiscussion = findViewById(R.id.btnSubmitDiscussion)

        forumId = intent.getIntExtra("forumId", 0)

        if (forumId == 0) {
            Toast.makeText(this, "Forum ID inválido", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        btnSubmitDiscussion.setOnClickListener {
            val subject = etDiscussionSubject.text.toString().trim()
            val message = etDiscussionMessage.text.toString().trim()

            if (subject.isEmpty() || message.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                submitDiscussion(subject, message)
            }
        }
    }

    private fun submitDiscussion(subject: String, message: String) {
        lifecycleScope.launch {
            try {
                val response = forumRepository.addDiscussion(
                    token = apiToken,
                    forumId = forumId,
                    subject = subject,
                    message = message
                )

                if (response.isSuccessful) {
                    val rawBody = response.body()?.string()
                    Log.d("ADD_DISCUSSION", "Respuesta Moodle: $rawBody")

                    Toast.makeText(
                        this@AddDiscussionActivity,
                        "Discusión creada correctamente ✅",
                        Toast.LENGTH_LONG
                    ).show()

                    setResult(RESULT_OK)
                    finish()

                } else {
                    Log.e(
                        "ADD_DISCUSSION",
                        "Error HTTP ${response.code()} - ${response.errorBody()?.string()}"
                    )

                    Toast.makeText(
                        this@AddDiscussionActivity,
                        "Error al crear la discusión",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: Exception) {
                Log.e("ADD_DISCUSSION", "Error de conexión", e)

                Toast.makeText(
                    this@AddDiscussionActivity,
                    "Error de conexión con Moodle",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
