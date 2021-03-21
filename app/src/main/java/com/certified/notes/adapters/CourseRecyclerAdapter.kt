package com.certified.notes.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.certified.notes.R
import com.certified.notes.model.Course

class CourseRecyclerAdapter :
    ListAdapter<Course, CourseRecyclerAdapter.ViewHolder>(DIFF_CALLBACK) {

    private lateinit var listener: onCourseClickedListener

    fun setOnCourseClickedListener(listener: onCourseClickedListener) {
        this.listener = listener
    }

    interface onCourseClickedListener {
        fun onCourseClicked(course: Course)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val courseCode: TextView
        val courseTitle: TextView

        init {
            courseCode = itemView.findViewById(R.id.tv_course_code)
            courseTitle = itemView.findViewById(R.id.tv_course_title)

            itemView.setOnClickListener{
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION)
                    listener.onCourseClicked(getItem(position))
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK: DiffUtil.ItemCallback<Course> =
            object : DiffUtil.ItemCallback<Course>() {
                override fun areItemsTheSame(oldItem: Course, newItem: Course): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: Course, newItem: Course): Boolean {
                    return oldItem.courseTitle == newItem.courseTitle &&
                            oldItem.courseCode == newItem.courseCode
                }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_courses, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val course = getItem(position)
        holder.courseTitle.text = course.courseTitle
        holder.courseCode.text = course.courseCode
    }
}