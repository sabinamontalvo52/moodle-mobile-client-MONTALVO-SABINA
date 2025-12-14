package com.example.moodlemobileclient

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddDiscussionActivity : AppCompatActivity() {

    private val apiToken = "a2a6f9e1c4b7809aae2b3891c91e15fd"

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
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.service.addDiscussion(
                        token = apiToken,
                        forumId = forumId,
                        subject = subject,
                        message = message
                    )
                }

                val raw = response.string()
                Log.d("ADD_DISCUSSION", "Respuesta Moodle: $raw")

                if (raw.contains("exception", ignoreCase = true)) {
                    Toast.makeText(
                        this@AddDiscussionActivity,
                        "Moodle rechazó la discusión",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        this@AddDiscussionActivity,
                        "Discusión creada correctamente ✅",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }

            } catch (e: Exception) {
                Log.e("ADD_DISCUSSION", "Error real", e)
                Toast.makeText(
                    this@AddDiscussionActivity,
                    "Error de conexión",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


}
