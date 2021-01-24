package com.certified.notes.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.preference.PreferenceManager
import com.certified.notes.R
import com.certified.notes.model.Course
import com.certified.notes.model.Note
import com.certified.notes.model.Todo
import com.certified.notes.room.NotesViewModel
import com.certified.notes.util.PreferenceKeys
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

    private lateinit var builder: MaterialAlertDialogBuilder
    private lateinit var alertDialog: AlertDialog
    private lateinit var inflater: LayoutInflater

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        isDarkModeEnabled()

        notesViewModel = NotesViewModel(application)
        navController = Navigation.findNavController(this, R.id.fragment)
        builder = MaterialAlertDialogBuilder(this)
        inflater = this.layoutInflater

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

    private fun isDarkModeEnabled() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val isDarkModeEnabled = preferences.getBoolean(PreferenceKeys.DARK_MODE, false)
        if (isDarkModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fab -> {
                if (viewBlur.visibility == View.VISIBLE)
                    hideViews()
                else
                    showViews()
            }
            R.id.fab_add_course -> {
                hideViews()
                launchCourseDialog()
            }
            R.id.fab_add_todo -> {
                hideViews()
                launchTodoDialog()
            }
            R.id.fab_add_note -> {
                hideViews()
                launchNoteDialog()
            }
            R.id.view -> {
                hideViews()
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
        picker.maxValue = 4

        tvCourseDialogTitle.text = getString(R.string.add_course)
        btnCancel.setOnClickListener { alertDialog.dismiss() }
        btnSave.setOnClickListener {
            val courseCode: String = etCourseCode.text.toString()
            val courseTitle: String = etCourseTitle.text.toString()
            val courseUnit: Int = picker.value
            val MARK_NOT_SET = 0
            val GRADE_POINT_NOT_SET = 0
            if (courseCode.isNotEmpty() && courseTitle.isNotEmpty()) {
                val course = Course(courseCode, courseTitle, courseUnit, MARK_NOT_SET, "F", GRADE_POINT_NOT_SET)
                notesViewModel.insertCourse(course)
                alertDialog.dismiss()
            } else
                Toast.makeText(this, getString(R.string.all_fields_are_required), Toast.LENGTH_LONG).show()
        }
        alertDialog.show()
    }

    private fun launchTodoDialog() {
        val view = inflater.inflate(R.layout.dialog_new_todo, null)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.background = getDrawable(R.drawable.alert_dialog_bg)
        }
        builder.setTitle(getString(R.string.add_todo))
        alertDialog = builder.create()
        alertDialog.setView(view)

        val etTodo: EditText = view.findViewById(R.id.et_todo)
        val btnSave: MaterialButton = view.findViewById(R.id.btn_save)
        val btnCancel: MaterialButton = view.findViewById(R.id.btn_cancel)

        btnCancel.setOnClickListener { alertDialog.dismiss() }
        btnSave.setOnClickListener { v ->
            val todoContent: String = etTodo.text.toString()
            if (todoContent.isNotEmpty()) {
                val todo = Todo(todoContent, false)
                notesViewModel.insertTodo(todo)
                alertDialog.dismiss()
            } else
                Toast.makeText(this, getString(R.string.add_a_todo), Toast.LENGTH_LONG).show()
        }
        alertDialog.show()
    }

    private fun launchNoteDialog() {
        val view = inflater.inflate(R.layout.dialog_new_note, null)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.background = getDrawable(R.drawable.alert_dialog_bg)
        }
        alertDialog = builder.create()
        alertDialog.setView(view)

        val spinnerCourses: Spinner = view.findViewById(R.id.spinner_courses)
        val etNoteTitle: EditText = view.findViewById(R.id.et_note_title)
        val etNoteContent: EditText = view.findViewById(R.id.et_note_content)
        val btnSave: MaterialButton = view.findViewById(R.id.btn_save)
        val btnCancel: MaterialButton = view.findViewById(R.id.btn_cancel)
        val tvNoteDialogTitle: TextView = view.findViewById(R.id.tv_note_dialog_title)

        val courseList = arrayListOf<String>()
        val adapterCourses = ArrayAdapter(this, android.R.layout.simple_spinner_item, courseList)
        notesViewModel.allCourses.observe(this, Observer { courses: List<Course> ->
            courseList.add(getString(R.string.select_a_course))
            courseList.add(getString(R.string.no_course))
            for (course in courses) {
                courseList.add(course.courseTitle)
            }
            adapterCourses.notifyDataSetChanged()
        })

        adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCourses.adapter = adapterCourses
        tvNoteDialogTitle.text = getString(R.string.add_note)

        btnCancel.setOnClickListener { alertDialog.dismiss() }
        btnSave.setOnClickListener { v ->
            val courseTitle = spinnerCourses.selectedItem.toString()
            var courseCode = "NIL"
            if (courseTitle != getString(R.string.no_course) || courseTitle != getString(R.string.select_a_course))
                courseCode = notesViewModel.getCourseCode(courseTitle)
            val noteTitle = etNoteTitle.text.toString()
            val noteContent = etNoteContent.text.toString()

            if (noteTitle.isNotEmpty() && noteContent.isNotEmpty()) {
                if (courseTitle != getString(R.string.select_a_course)) {
                    val note = Note(courseCode, noteTitle, noteContent)
                    notesViewModel.insertNote(note)
                    alertDialog.dismiss()
                    Toast.makeText(this, getString(R.string.note_saved), Toast.LENGTH_LONG).show()
                } else
                    Toast.makeText(this, getString(R.string.select_a_course), Toast.LENGTH_LONG).show()
            } else
                Toast.makeText(this, getString(R.string.all_fields_are_required), Toast.LENGTH_LONG).show()
        }
        alertDialog.show()
    }
}