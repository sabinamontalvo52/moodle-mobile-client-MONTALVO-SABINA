package com.example.moodlemobileclient.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moodlemobileclient.R
import com.example.moodlemobileclient.data.model.course.*

class CoursesAdapter(
    private val courses: List<CourseResponse>,
    private val onClick: (CourseResponse) -> Unit
) : RecyclerView.Adapter<CoursesAdapter.CourseViewHolder>() {

    inner class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCourseName: TextView = itemView.findViewById(R.id.tvCourseName)
        val tvCourseShortName: TextView = itemView.findViewById(R.id.tvCourseShortName)
        val tvTeachers: TextView = itemView.findViewById(R.id.tvTeachers)

        init {
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onClick(courses[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_course, parent, false)
        return CourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = courses[position]

        // Mostrar datos del curso
        holder.tvCourseName.text = course.fullname
        holder.tvCourseShortName.text = "Short name: ${course.shortname}"
        holder.tvTeachers.text = "Docentes: ${
            course.teachers?.joinToString { it.fullname } ?: "Sin docentes"
        }"
    }

    override fun getItemCount(): Int = courses.size
}