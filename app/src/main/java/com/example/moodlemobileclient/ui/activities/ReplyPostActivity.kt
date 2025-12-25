package com.example.moodlemobileclient.ui.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.moodlemobileclient.R
import com.example.moodlemobileclient.data.repository.ForumRepository

class ReplyPostActivity : AppCompatActivity() {

    private var postId: Int = 0
    private lateinit var token: String

    private val forumRepository = ForumRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reply_post)

        postId = intent.getIntExtra("postId", 0)
        token = intent.getStringExtra("token") ?: ""

        if (postId == 0 || token.isEmpty()) {
            Toast.makeText(this, "Datos inválidos", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val etMessage = findViewById<EditText>(R.id.etReplyMessage)
        val btnSend = findViewById<Button>(R.id.btnSendReply)

        btnSend.setOnClickListener {
            val message = etMessage.text.toString().trim()

            if (message.isEmpty()) {
                etMessage.error = "Escribe tu respuesta"
                return@setOnClickListener
            }

            sendReply(message)
        }
    }

    private fun sendReply(message: String) {

        forumRepository.replyPost(
            token = token,
            postId = postId,
            subject = "Re:",
            message = message,
            onSuccess = {
                runOnUiThread {
                    setResult(RESULT_OK)
                    finish()
                }
            },
            onError = {
                runOnUiThread {
                    Toast.makeText(
                        this@ReplyPostActivity,
                        "Error al enviar respuesta",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        )
    }

}
