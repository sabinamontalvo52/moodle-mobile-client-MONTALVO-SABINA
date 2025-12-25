package com.example.moodlemobileclient.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moodlemobileclient.data.model.course.*
import com.example.moodlemobileclient.R

class CourseActivitiesAdapter(
    private val activities: List<CourseActivityItem>,
    private val onActivityClick: (CourseActivityItem) -> Unit // Lambda para manejar clicks
) : RecyclerView.Adapter<CourseActivitiesAdapter.ActivityViewHolder>() {

    inner class ActivityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvActivityName: TextView = itemView.findViewById(R.id.tvActivityName)
        val tvActivityType: TextView = itemView.findViewById(R.id.tvActivityType)
        val ivActivityIcon: ImageView = itemView.findViewById(R.id.ivActivityIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_activity, parent, false)
        return ActivityViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        val activity = activities[position]

        holder.tvActivityName.text = activity.name ?: "Sin nombre"
        holder.tvActivityType.text = activity.modname ?: "Sin tipo"

        val iconRes = when (activity.modname) {
            "assign" -> R.drawable.ic_activity_assignment
            "forum" -> R.drawable.ic_activity_forum
            else -> R.drawable.ic_activity_default
        }

        holder.ivActivityIcon.setImageResource(iconRes)

        holder.itemView.setOnClickListener {
            onActivityClick(activity)
        }
    }


    override fun getItemCount(): Int = activities.size
}
