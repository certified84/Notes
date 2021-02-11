package com.certified.notes.ui

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NotificationCompat
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
import com.shawnlin.numberpicker.NumberPicker

class MainActivity : AppCompatActivity(), View.OnClickListener {

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

    private val PRIMARY_CHANNEL_ID = "primary_notification_channel"
    private lateinit var notifyManager: NotificationManager
    private val NOTES_NOTIFICATION_ID = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        isDarkModeEnabled()
        createNotificationChannel()
        isFirstOpen()

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

    private fun isDarkModeEnabled() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val isDarkModeEnabled = preferences.getBoolean(PreferenceKeys.DARK_MODE, false)
        if (isDarkModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val w = window
                w.statusBarColor = getColor(R.color.midWhite)
            }
        }
    }

    private fun isFirstOpen() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val isFirstOpen = preferences.getBoolean(PreferenceKeys.FIRST_TIME_OPEN, true)
        if (isFirstOpen) {
            val alertDialogBuilder = MaterialAlertDialogBuilder(this)
            alertDialogBuilder.setTitle(getString(R.string.welcome_to_notes))
            alertDialogBuilder.setMessage(
                "Hey there, thank you for downloading Notes your personal tool for managing your semester courses, notes and todos." +
                        "\nMake sure to add your semester courses in other to link your notes to them"
            )
            alertDialogBuilder.setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                val editor = preferences.edit()
                editor.putBoolean(PreferenceKeys.FIRST_TIME_OPEN, false)
                editor.apply()
                dialog.dismiss()
            }
//            alertDialogBuilder.setNegativeButton(getString(R.string.no)) { _, _ ->
//                finish()
//            }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        } else
            sendNotification()
    }

    private fun createNotificationChannel() {
        notifyManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                PRIMARY_CHANNEL_ID,
                getString(R.string.notes_notification),
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.notes_reminder_notification)
            notifyManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun getNotificationBuilder(): NotificationCompat.Builder? {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val notificationPendingIntent = PendingIntent.getActivity(
            this, NOTES_NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        val message = getString(R.string.notification_message)
        return NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
            .setDefaults(Notification.DEFAULT_ALL)
            .setSmallIcon(R.drawable.ic_notes)
            .setContentTitle(getString(R.string.notes_reminder))
            .setColor(resources.getColor(R.color.colorAccent))
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setTicker("Notes")
            .setAutoCancel(true)
    }

    private fun sendNotification() {
        val notifyBuilder = getNotificationBuilder()
        notifyManager.notify(NOTES_NOTIFICATION_ID, notifyBuilder!!.build())
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
        val inflater: LayoutInflater = layoutInflater
        val view = inflater.inflate(R.layout.dialog_new_course, null)
        val builder = MaterialAlertDialogBuilder(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            builder.background = getDrawable(R.drawable.alert_dialog_bg)

        val alertDialog = builder.create()
        alertDialog.setOnShowListener {
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
        }
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
                val course = Course(
                    courseCode,
                    courseTitle,
                    courseUnit,
                    MARK_NOT_SET,
                    "F",
                    GRADE_POINT_NOT_SET
                )
                notesViewModel.insertCourse(course)
                alertDialog.dismiss()
            } else
                Toast.makeText(this, getString(R.string.all_fields_are_required), Toast.LENGTH_LONG)
                    .show()
        }
        alertDialog.show()
    }

    private fun launchTodoDialog() {
        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.dialog_new_todo, null)
        val builder = MaterialAlertDialogBuilder(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            builder.background = getDrawable(R.drawable.alert_dialog_bg)

        val alertDialog = builder.create()
        alertDialog.setOnShowListener {
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
        }
        alertDialog.setView(view)

        val tvTodoDialogTitle: MaterialTextView = view.findViewById(R.id.tv_todo_dialog_title)
        val etTodo: EditText = view.findViewById(R.id.et_todo)
        val btnSave: MaterialButton = view.findViewById(R.id.btn_save)
        val btnCancel: MaterialButton = view.findViewById(R.id.btn_cancel)

        tvTodoDialogTitle.text = getString(R.string.add_todo)
        btnCancel.setOnClickListener { alertDialog.dismiss() }
        btnSave.setOnClickListener {
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
        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.dialog_new_note, null)
        val builder = MaterialAlertDialogBuilder(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.background = getDrawable(R.drawable.alert_dialog_bg)
        }
        val alertDialog = builder.create()
        alertDialog.setOnShowListener {
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
        }
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
        btnSave.setOnClickListener {
            val courseTitle = spinnerCourses.selectedItem.toString()
            val courseCode =
                if (courseTitle == getString(R.string.no_course)) "NIL" else notesViewModel.getCourseCode(
                    courseTitle
                )
            val noteTitle = etNoteTitle.text.toString()
            val noteContent = etNoteContent.text.toString()

            if (noteTitle.isNotEmpty() && noteContent.isNotEmpty()) {
                if (courseTitle != getString(R.string.select_a_course)) {
                    val note = Note(courseCode, noteTitle, noteContent)
                    notesViewModel.insertNote(note)
                    alertDialog.dismiss()
                    Toast.makeText(this, getString(R.string.note_saved), Toast.LENGTH_LONG).show()
                } else
                    Toast.makeText(this, getString(R.string.select_a_course), Toast.LENGTH_LONG)
                        .show()
            } else
                Toast.makeText(this, getString(R.string.all_fields_are_required), Toast.LENGTH_LONG)
                    .show()
        }
        alertDialog.show()
    }
}