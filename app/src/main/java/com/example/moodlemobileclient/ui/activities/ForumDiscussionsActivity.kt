package com.example.moodlemobileclient.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moodlemobileclient.R
import com.example.moodlemobileclient.data.model.forum.*
import com.example.moodlemobileclient.data.repository.ForumRepository
import com.example.moodlemobileclient.ui.adapters.DiscussionsAdapter
import com.example.moodlemobileclient.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ForumDiscussionsActivity : AppCompatActivity() {

    private val apiToken = Constants.MOODLE_API_TOKEN
    private val forumRepository = ForumRepository()

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvForumName: TextView
    private lateinit var btnAddDiscussion: Button

    private var forumId: Int = 0
    private var forumName: String = "Foro"

    private val discussionsList = mutableListOf<DiscussionResponse>()
    private lateinit var discussionsAdapter: DiscussionsAdapter
    private lateinit var addDiscussionLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum_discussions)

        recyclerView = findViewById(R.id.rvDiscussions)
        tvForumName = findViewById(R.id.tvForumName)
        btnAddDiscussion = findViewById(R.id.btnAddDiscussion)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        discussionsAdapter = DiscussionsAdapter(discussionsList) { discussion ->
            val intent = Intent(this, DiscussionPostsActivity::class.java)
            intent.putExtra("discussionId", discussion.discussion)
            intent.putExtra("rootPostId", discussion.id)
            intent.putExtra("rootMessage", discussion.message ?: "")
            intent.putExtra("rootAuthor", discussion.userfullname ?: "")
            intent.putExtra("token", apiToken)
            startActivity(intent)
        }

        recyclerView.adapter = discussionsAdapter

        forumId = intent.getIntExtra("forumId", 0)
        forumName = intent.getStringExtra("forumName") ?: "Foro"

        if (forumId == 0) {
            Toast.makeText(this, "Forum ID inválido", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        title = forumName
        tvForumName.text = forumName

        addDiscussionLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    getForumDiscussions()
                }
            }

        btnAddDiscussion.setOnClickListener {
            val intent = Intent(this, AddDiscussionActivity::class.java)
            intent.putExtra("forumId", forumId)
            addDiscussionLauncher.launch(intent)
        }

        getForumDiscussions()
    }

    private fun getForumDiscussions() {
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    forumRepository.getForumDiscussions(
                        token = apiToken,
                        forumId = forumId
                    )
                }

                Log.d("FORUM_JSON", response.toString())

                val discussions = response.discussions
                    ?.sortedBy { it.created }
                    .orEmpty()

                discussionsList.clear()
                discussionsList.addAll(discussions)
                discussionsAdapter.notifyDataSetChanged()

            } catch (e: Exception) {
                Log.e("FORUM_DISCUSSIONS", "Error al cargar discusiones", e)
                Toast.makeText(
                    this@ForumDiscussionsActivity,
                    "Error al cargar discusiones",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
