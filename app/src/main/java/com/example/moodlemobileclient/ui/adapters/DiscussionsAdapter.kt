package com.example.moodlemobileclient.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moodlemobileclient.R
import com.example.moodlemobileclient.data.model.forum.*

class DiscussionsAdapter(
    private val discussions: List<DiscussionResponse>,
    private val onDiscussionClick: (DiscussionResponse) -> Unit
) : RecyclerView.Adapter<DiscussionsAdapter.DiscussionViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DiscussionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_forum_discussion, parent, false)
        return DiscussionViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: DiscussionViewHolder,
        position: Int
    ) {
        holder.bind(discussions[position])
    }

    override fun getItemCount(): Int = discussions.size

    inner class DiscussionViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        private val tvTitle: TextView =
            itemView.findViewById(R.id.tvDiscussionTitle)

        private val tvAuthor: TextView =
            itemView.findViewById(R.id.tvStartedBy)

        private val tvReplies: TextView =
            itemView.findViewById(R.id.tvReplies)

        fun bind(discussion: DiscussionResponse) {
            tvTitle.text = discussion.subject.orEmpty().ifBlank { "Sin título" }
            tvAuthor.text = discussion.userfullname.orEmpty().ifBlank { "Autor desconocido" }
            tvReplies.text = discussion.numreplies.toString()

            itemView.setOnClickListener {
                onDiscussionClick(discussion)
            }
        }
    }
}
