package com.certified.notes.ui

import android.os.Build
import android.os.Bundle
import android.text.TextUtils.isEmpty
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.certified.notes.R
import com.certified.notes.model.Course
import com.certified.notes.room.NotesViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textview.MaterialTextView

class MainActivityKt : AppCompatActivity(), View.OnClickListener {

    private lateinit var notesViewModel: NotesViewModel

    private lateinit var tvFabTodoTitle: TextView
    private lateinit var tvFabNoteTitle: TextView
    private lateinit var tvFabCourseTitle: TextView

    private lateinit var fab: FloatingActionButton
    private lateinit var fabAddNote: FloatingActionButton
    private lateinit var fabAddCourse: FloatingActionButton
    private lateinit var fabAddTodo: FloatingActionButton

    private lateinit var viewBlur: View
    private lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView

    private val builder = MaterialAlertDialogBuilder(this)
    private lateinit var alertDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        notesViewModel = NotesViewModel(application)
        navController = Navigation.findNavController(this, R.id.fragment)

        bottomNavigationView = findViewById(R.id.smoothBottomBar)

        NavigationUI.setupWithNavController(bottomNavigationView, navController)

        fab = findViewById(R.id.fab)
        fabAddCourse = findViewById(R.id.fab_add_course)
        fabAddNote = findViewById(R.id.fab_add_note)
        fabAddTodo = findViewById(R.id.fab_add_todo)

        tvFabTodoTitle = findViewById(R.id.tv_fab_todo_title)
        tvFabNoteTitle = findViewById(R.id.tv_fab_note_title)
        tvFabCourseTitle = findViewById(R.id.tv_fab_course_title)

        viewBlur = findViewById(R.id.view)

        fab.setOnClickListener(this)
        fabAddCourse.setOnClickListener(this)
        fabAddNote.setOnClickListener(this)
        fabAddTodo.setOnClickListener(this)

        viewBlur.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val id: Int = v!!.id

        when {
            id == R.id.fab -> {
                if (viewBlur.visibility == View.VISIBLE)
                    hideViews()
                else
                    showViews()
            }

            id == R.id.fab_add_course -> {
                hideViews()
                launchCourseDialog()
            }
        }
    }

    private fun showViews() {
        viewBlur.visibility = View.VISIBLE
        fabAddTodo.visibility = View.VISIBLE
        fabAddNote.visibility = View.VISIBLE
        fabAddCourse.visibility = View.VISIBLE
        tvFabTodoTitle.visibility = View.VISIBLE
        tvFabNoteTitle.visibility = View.VISIBLE
        tvFabCourseTitle.visibility = View.VISIBLE
    }

    private fun hideViews() {
        viewBlur.visibility = View.GONE
        fabAddCourse.visibility = View.GONE
        fabAddNote.visibility = View.GONE
        fabAddTodo.visibility = View.GONE
        tvFabTodoTitle.visibility = View.GONE
        tvFabNoteTitle.visibility = View.GONE
        tvFabCourseTitle.visibility = View.GONE
    }

    private fun launchCourseDialog() {
        val inflater = this.layoutInflater
        val view = inflater.inflate(R.layout.dialog_new_course, null)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            builder.background = getDrawable(R.drawable.alert_dialog_bg)

        alertDialog = builder.create()
        alertDialog.setView(view)

        val tvCourseDialogTitle: MaterialTextView = view.findViewById(R.id.tv_course_dialog_title)
        val etCourseCode: EditText = view.findViewById(R.id.et_course_code)
        val etCourseTitle: EditText = view.findViewById(R.id.et_course_title)
        val picker: NumberPicker = view.findViewById(R.id.number_picker_course_unit)
        val btnSave: MaterialButton = view.findViewById(R.id.btn_save)
        val btnCancel: MaterialButton = view.findViewById(R.id.btn_cancel)

        picker.minValue = 1
        picker.maxValue = 2

        tvCourseDialogTitle.text = getString(R.string.add_course)
        btnCancel.setOnClickListener { alertDialog.dismiss() }
        btnSave.setOnClickListener(View.OnClickListener {
            val courseCode: String = etCourseCode.text.toString()
            val courseTitle: String = etCourseTitle.text.toString()
            val courseUnit: Int = picker.value
            val MARK_NOT_SET = 0
            val GRADE_POINT_NOT_SET = 0
            if (!isEmpty(courseCode) && !isEmpty(courseTitle)) {
                val course = Course(courseCode, courseTitle, courseUnit, MARK_NOT_SET, "F", GRADE_POINT_NOT_SET)
                notesViewModel.insertCourse(course)
                alertDialog.dismiss()
            } else
                Toast.makeText(this, "All fields are required", Toast.LENGTH_LONG).show()
        })
    }
}