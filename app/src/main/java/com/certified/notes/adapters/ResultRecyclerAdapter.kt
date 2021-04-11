package com.certified.notes.adapters

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.certified.notes.R
import com.certified.notes.model.Course
import com.certified.notes.room.NotesViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ResultRecyclerAdapter(val context: Context, private val viewModel: NotesViewModel) :
    ListAdapter<Course, ResultRecyclerAdapter.ViewHolder>(DIFF_CALLBACK) {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val courseCode: TextView = itemView.findViewById(R.id.tv_course_code)
        val courseUnit: TextView = itemView.findViewById(R.id.tv_course_unit)
        val courseMark: TextView = itemView.findViewById(R.id.tv_course_mark)
        val courseGrade: TextView = itemView.findViewById(R.id.tv_course_grade)

    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Course> =
            object : DiffUtil.ItemCallback<Course>() {
                override fun areItemsTheSame(oldItem: Course, newItem: Course): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: Course, newItem: Course): Boolean {
                    return oldItem.courseUnit == newItem.courseUnit &&
                            oldItem.courseCode == newItem.courseCode
                }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_result, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val course = getItem(position)
        holder.courseCode.text = course.courseCode
        holder.courseUnit.text = course.courseUnit.toString()
        holder.courseMark.text = course.courseMark.toString()
        holder.courseGrade.text = course.courseGrade

        holder.itemView.setOnClickListener {
            val view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_result, null)

            val builder = MaterialAlertDialogBuilder(context)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                builder.background = ContextCompat.getDrawable(context, R.drawable.alert_dialog_bg)
            builder.setTitle(context.getString(R.string.enter_score_for) + course.courseCode)

            val alertDialog = builder.create()
            alertDialog.setOnShowListener {
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
            }
            alertDialog.setView(view)

            val etCourseMark: EditText = view.findViewById(R.id.et_course_mark)
            val btnCancel: MaterialButton = view.findViewById(R.id.btn_cancel)
            val btnSave: MaterialButton = view.findViewById(R.id.btn_save)

            val validMark = mutableListOf<Int>()
            for (i in 0..100)
                validMark.add(i)

            etCourseMark.setText(holder.courseMark.text)
            btnCancel.setOnClickListener { alertDialog.dismiss() }
            btnSave.text = context.getString(R.string.update)
            btnSave.setOnClickListener {
                val courseCode = course.courseCode
                val courseTitle = course.courseTitle
                val courseUnit = course.courseUnit
                val courseMark = etCourseMark.text.toString().toInt()
                val courseGrade: String?
                val courseGradePoint: Int?

                when (courseMark) {
                    in 70..100 -> {
                        courseGrade = "A"
                        courseGradePoint = 5
                    }
                    in 60..69 -> {
                        courseGrade = "B"
                        courseGradePoint = 4
                    }
                    in 50..59 -> {
                        courseGrade = "C"
                        courseGradePoint = 3
                    }
                    in 45..49 -> {
                        courseGrade = "D"
                        courseGradePoint = 2
                    }
                    else -> {
                        courseGrade = "F"
                        courseGradePoint = 0
                    }
                }

                if (etCourseMark.text.toString().isNotEmpty()) {
                    if (courseMark in validMark) {
                        if (courseMark != course.courseMark) {
                            val course1 = Course(
                                courseCode,
                                courseTitle,
                                courseUnit,
                                courseMark,
                                courseGrade,
                                courseGradePoint
                            )
                            course1.id = course.id
                            viewModel.updateCourse(course1)

                            holder.courseGrade.text = courseGrade
                            holder.courseMark.text = courseMark.toString()

                            alertDialog.dismiss()
                        } else
                            Toast.makeText(
                                context,
                                context.getString(R.string.score_not_changed),
                                Toast.LENGTH_LONG
                            ).show()
                    } else
                        Toast.makeText(
                            context,
                            context.getString(R.string.please_input_a_valid_score),
                            Toast.LENGTH_LONG
                        ).show()
                } else
                    Toast.makeText(
                        context,
                        context.getString(R.string.please_input_a_score),
                        Toast.LENGTH_LONG
                    ).show()
            }
            alertDialog.show()
        }
    }
}