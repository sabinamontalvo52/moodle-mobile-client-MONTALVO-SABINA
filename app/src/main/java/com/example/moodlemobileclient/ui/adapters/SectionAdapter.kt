package com.example.moodlemobileclient.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moodlemobileclient.data.model.course.*
import com.example.moodlemobileclient.R

class SectionAdapter(
    private val sections: List<CourseSection>,
    private val onActivityClick: (CourseActivityItem) -> Unit
) : RecyclerView.Adapter<SectionAdapter.SectionViewHolder>() {

    inner class SectionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvSectionName: TextView = view.findViewById(R.id.tvSectionName)
        val ivArrow: ImageView = view.findViewById(R.id.ivArrow)
        val sectionHeader: View = view.findViewById(R.id.sectionHeader)
        val rvActivities: RecyclerView = view.findViewById(R.id.rvActivities)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_section, parent, false)
        return SectionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        val section = sections[position]

        // Nombre de sección
        holder.tvSectionName.text =
            if (section.section == 0) "General" else section.name

        // Estado expandido
        holder.rvActivities.visibility =
            if (section.isExpanded) View.VISIBLE else View.GONE

        holder.ivArrow.animate()
            .rotation(if (section.isExpanded) 180f else 0f)
            .setDuration(200)
            .start()

        // Recycler interno (ACTIVIDADES)
        holder.rvActivities.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = CourseActivitiesAdapter(
                activities = section.modules,
                onActivityClick = onActivityClick
            )
            setHasFixedSize(true)
        }

        // Click header → expandir / colapsar
        holder.sectionHeader.setOnClickListener {
            sections.forEachIndexed { index, s ->
                s.isExpanded = index == position && !s.isExpanded
            }
            notifyDataSetChanged()
        }

    }
        override fun getItemCount(): Int = sections.size
    }
