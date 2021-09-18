package com.certified.notes.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.certified.notes.R
import com.certified.notes.model.Course

class HomeCourseRecyclerAdapter(val navController: NavController) : ListAdapter<Course, HomeCourseRecyclerAdapter.ViewHolder>(
    DIFF_CALLBACK
) {

    private lateinit var listener: OnCourseClickedListener

    fun setOnCourseClickedListener(listener: OnCourseClickedListener) {
        this.listener = listener
    }

    interface OnCourseClickedListener {
        fun onCourseClicked()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val courseCode: TextView = itemView.findViewById(R.id.tv_course_code)
        val courseTitle: TextView = itemView.findViewById(R.id.tv_course_title)

        init {

            itemView.setOnClickListener { navController.navigate(R.id.coursesFragment) }
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
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_courses_home, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val course = getItem(position)
        holder.courseTitle.text = course.courseTitle
        holder.courseCode.text = course.courseCode
    }
}