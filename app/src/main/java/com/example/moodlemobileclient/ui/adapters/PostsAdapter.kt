package com.example.moodlemobileclient.ui.adapters

import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moodlemobileclient.R
import com.example.moodlemobileclient.data.model.forum.*

class PostsAdapter(
    private val posts: List<PostItem>,
    private val onReplyClick: (postId: Int) -> Unit
) : RecyclerView.Adapter<PostsAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: PostViewHolder,
        position: Int
    ) {
        holder.bind(posts[position])
    }

    override fun getItemCount(): Int = posts.size

    inner class PostViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        private val tvAuthor: TextView =
            itemView.findViewById(R.id.tvAuthor)

        private val tvMessage: TextView =
            itemView.findViewById(R.id.tvMessage)

        private val btnReply: TextView =
            itemView.findViewById(R.id.btnReply)

        fun bind(post: PostItem) {
            tvAuthor.text = post.authorName
            tvMessage.text = parseHtml(post.message)

            btnReply.setOnClickListener {
                onReplyClick(post.id)
            }
        }

        private fun parseHtml(html: String): CharSequence {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT)
            } else {
                Html.fromHtml(html)
            }
        }
    }
}
