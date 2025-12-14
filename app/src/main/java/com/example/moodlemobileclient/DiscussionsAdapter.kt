package com.example.moodlemobileclient

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Adapter para mostrar las discusiones de un foro
class DiscussionsAdapter(
    private val context: Context,
    private val discussions: List<DiscussionItem>
) : RecyclerView.Adapter<DiscussionsAdapter.DiscussionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscussionViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_discussion, parent, false)
        return DiscussionViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiscussionViewHolder, position: Int) {
        val discussion = discussions[position]
        holder.bind(discussion)
    }

    override fun getItemCount(): Int = discussions.size

    inner class DiscussionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvSubject: TextView = itemView.findViewById(R.id.tvSubject)
        private val tvAuthor: TextView = itemView.findViewById(R.id.tvAuthor)
        private val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)

        fun bind(discussion: DiscussionItem) {
            tvSubject.text = discussion.subject
            tvAuthor.text = "Autor: ${discussion.authorName}"
            tvMessage.text = discussion.message
        }
    }
}
