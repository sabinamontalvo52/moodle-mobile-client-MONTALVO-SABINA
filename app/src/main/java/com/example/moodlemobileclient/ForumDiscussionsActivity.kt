package com.example.moodlemobileclient

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ForumDiscussionsActivity : AppCompatActivity() {

    private val apiToken = "a2a6f9e1c4b7809aae2b3891c91e15fd"

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvForumName: TextView
    private lateinit var btnAddDiscussion: Button

    private var forumId: Int = 0
    private var forumName: String = "Foro"

    private val discussionsList = mutableListOf<DiscussionItem>()
    private lateinit var discussionsAdapter: DiscussionsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum_discussions)

        recyclerView = findViewById(R.id.rvDiscussions)
        tvForumName = findViewById(R.id.tvForumName)
        btnAddDiscussion = findViewById(R.id.btnAddDiscussion)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        discussionsAdapter = DiscussionsAdapter(this, discussionsList)
        recyclerView.adapter = discussionsAdapter

        forumId = intent.getIntExtra("forumId", 0)
        forumName = intent.getStringExtra("forumName") ?: "Foro"

        title = forumName
        tvForumName.text = forumName

        btnAddDiscussion.setOnClickListener {
            val intent = Intent(this, AddDiscussionActivity::class.java)
            intent.putExtra("forumId", forumId)
            intent.putExtra("forumName", forumName)
            startActivity(intent)
        }

        getForumDiscussions()
    }

    private fun getForumDiscussions() {
        val service = RetrofitClient.service

        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    service.getForumDiscussions(
                        token = apiToken,
                        forumId = forumId
                    )
                }

                val discussions = response.discussions

                if (discussions.isNullOrEmpty()) {
                    Toast.makeText(
                        this@ForumDiscussionsActivity,
                        "No hay discusiones en este foro",
                        Toast.LENGTH_SHORT
                    ).show()
                    discussionsList.clear()
                    discussionsAdapter.notifyDataSetChanged()
                    return@launch
                }

                discussionsList.clear()
                discussionsList.addAll(
                    discussions.map {
                        DiscussionItem(
                            id = it.id,
                            subject = it.subject ?: "Sin título",
                            message = it.message ?: "",
                            authorName = it.userfullname ?: "Usuario"
                        )
                    }
                )

                discussionsAdapter.notifyDataSetChanged()

            } catch (e: Exception) {
                Log.e("FORUM_DISCUSSIONS", "Error real", e)
                Toast.makeText(
                    this@ForumDiscussionsActivity,
                    "Error al cargar discusiones",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


}
