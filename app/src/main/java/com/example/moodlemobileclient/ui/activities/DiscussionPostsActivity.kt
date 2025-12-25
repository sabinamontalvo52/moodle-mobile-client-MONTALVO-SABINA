package com.example.moodlemobileclient.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moodlemobileclient.R
import com.example.moodlemobileclient.data.model.forum.*
import com.example.moodlemobileclient.data.repository.ForumRepository
import com.example.moodlemobileclient.ui.adapters.PostsAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class DiscussionPostsActivity : AppCompatActivity() {

    private lateinit var recyclerViewPosts: RecyclerView
    private lateinit var adapter: PostsAdapter
    private val postsList = mutableListOf<PostItem>()

    private val forumRepository = ForumRepository()

    private var discussionId: Int = 0
    private lateinit var token: String

    private var rootPostId: Int = 0
    private var rootMessage: String = ""
    private var rootAuthor: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_discussion_posts)

        discussionId = intent.getIntExtra("discussionId", 0)
        rootPostId = intent.getIntExtra("rootPostId", 0)
        rootMessage = intent.getStringExtra("rootMessage") ?: ""
        rootAuthor = intent.getStringExtra("rootAuthor") ?: ""
        token = intent.getStringExtra("token") ?: ""

        Log.d("ROOT_DEBUG", "rootPostId=$rootPostId author=$rootAuthor")
        Log.d("INTENT_DEBUG", "discussionId=$discussionId token=$token")

        if (discussionId == 0 || token.isEmpty()) {
            Toast.makeText(this, "Datos inválidos", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        recyclerViewPosts = findViewById(R.id.rvPosts)

        adapter = PostsAdapter(postsList) { postId ->
            val intent = Intent(this, ReplyPostActivity::class.java)
            intent.putExtra("postId", postId)
            intent.putExtra("token", token)
            startActivityForResult(intent, 100)
        }

        recyclerViewPosts.layoutManager = LinearLayoutManager(this)
        recyclerViewPosts.adapter = adapter

        fetchDiscussionPosts()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK) {
            fetchDiscussionPosts()
        }
        setResult(RESULT_OK)
    }

    private fun fetchDiscussionPosts() {
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    forumRepository.getDiscussionPosts(
                        token = token,
                        discussionId = discussionId
                    )
                }

                val posts = response.posts ?: emptyList()
                Log.d("FORUM_DEBUG", "Posts API = ${posts.size}")

                postsList.clear()

                // POST RAÍZ
                if (rootPostId != 0) {
                    postsList.add(
                        PostItem(
                            id = rootPostId,
                            subject = "",
                            message = rootMessage,
                            authorName = rootAuthor,
                            parentId = 0
                        )
                    )
                }

                // RÉPLICAS
                posts
                    .filter { it.parentid != null && it.parentid != 0 }
                    .forEach { post ->
                        postsList.add(
                            PostItem(
                                id = post.id,
                                subject = post.subject ?: "",
                                message = post.message ?: "",
                                authorName = post.author.fullname,
                                parentId = post.parentid ?: 0
                            )
                        )
                    }

                adapter.notifyDataSetChanged()

            } catch (e: Exception) {
                Toast.makeText(
                    this@DiscussionPostsActivity,
                    "Error al cargar la discusión",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
