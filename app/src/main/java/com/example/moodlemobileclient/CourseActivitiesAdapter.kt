package com.example.moodlemobileclient

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CourseActivitiesAdapter(
    private val context: Context,
    private val activities: List<CourseActivityItem>,
    private val courseId: Int,
    private val courseName: String,
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

        // Cargar ícono con Glide
        val iconUrl = activity.modicon ?: ""
        Glide.with(holder.itemView.context)
            .load(iconUrl)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_delete)
            .into(holder.ivActivityIcon)

        // Click usando lambda
        holder.itemView.setOnClickListener {
            onActivityClick(activity)
        }
    }

    override fun getItemCount(): Int = activities.size
}
