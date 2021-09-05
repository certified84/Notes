package com.certified.notes.ui.Courses

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.certified.notes.R
import com.certified.notes.adapters.CourseRecyclerAdapter
import com.certified.notes.adapters.HomeNoteRecyclerAdapter
import com.certified.notes.model.BookMark
import com.certified.notes.model.Course
import com.certified.notes.model.Note
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView
import com.shawnlin.numberpicker.NumberPicker

class CoursesFragmentKt : Fragment(), PopupMenu.OnMenuItemClickListener {

    companion object {
        const val GRID_SPAN_COUNT = 2
    }

    private lateinit var recyclerCourses: RecyclerView
    private lateinit var viewModel: CoursesViewModel
    private lateinit var ivCoursePopupMenu: ImageView
    private lateinit var svSearchCourses: SearchView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_courses, container, false)

        recyclerCourses = view.findViewById(R.id.recycler_view_courses)
        ivCoursePopupMenu = view.findViewById(R.id.iv_course_popup_menu)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            svSearchCourses = view.findViewById(R.id.sv_search_database)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModelFactory = CoursesViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CoursesViewModel::class.java)
        ivCoursePopupMenu.setOnClickListener(this::showPopupMenu)

        init()
    }

    private fun init() {
        val courseLayoutManager =
            StaggeredGridLayoutManager(GRID_SPAN_COUNT, LinearLayoutManager.VERTICAL)
        val courseRecyclerAdapter = CourseRecyclerAdapter()
        viewModel.allCourses.observe(viewLifecycleOwner, courseRecyclerAdapter::submitList)
        recyclerCourses.adapter = courseRecyclerAdapter
        recyclerCourses.layoutManager = courseLayoutManager

        courseRecyclerAdapter.setOnCourseClickedListener(object :
            CourseRecyclerAdapter.OnCourseClickedListener {
            override fun onCourseClicked(course: Course) {
                val selection = arrayOf<CharSequence>(
                    "Edit Course",
                    "Delete Course",
                    "Show related Notes"
                )

                val builder = MaterialAlertDialogBuilder(requireContext())
                builder.setTitle(R.string.options)
                builder.setSingleChoiceItems(selection, -1) { dialog, which ->
                    when (which) {
                        0 -> {
                            launchEditCourseDialog(course)
                            dialog.dismiss()
                        }
                        1 -> {
                            launchDeleteCourseDialog(course)
                            dialog.dismiss()
                        }
                        2 -> {
                            launchRelatedNotesDialog(course)
                            dialog.dismiss()
                        }
                    }
                }
                builder.show()
            }
        })

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            svSearchCourses.isSubmitButtonEnabled
            svSearchCourses.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (query != null)
                        searchCourses(query, courseRecyclerAdapter)
                    return true
                }

                override fun onQueryTextChange(query: String?): Boolean {
                    if (query != null)
                        searchCourses(query, courseRecyclerAdapter)
                    return true
                }

            })
        }
    }

    private fun launchRelatedNotesDialog(course: Course) {
        val view = layoutInflater.inflate(R.layout.dialog_related_notes, null)
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val recyclerViewRelatedNotes: RecyclerView =
            view.findViewById(R.id.recycler_view_related_notes)
        val noteLayoutManager = LinearLayoutManager(requireContext())
        val noteRecyclerAdapter = HomeNoteRecyclerAdapter(1)

        viewModel.getNotesAt(course.courseCode)?.observe(viewLifecycleOwner) { notes ->
            if (notes.isNotEmpty()) {
                noteRecyclerAdapter.submitList(notes)
            } else {
                val handler = Handler(Looper.myLooper()!!)
                handler.postDelayed({
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.no_related_notes),
                        Toast.LENGTH_LONG
                    ).show()
                    bottomSheetDialog.dismiss()
                }, 500)
//                Toast.makeText(
//                    requireContext(),
//                    getString(R.string.no_related_notes),
//                    Toast.LENGTH_LONG
//                ).show()
//                bottomSheetDialog.dismiss()
            }
        }
        recyclerViewRelatedNotes.adapter = noteRecyclerAdapter
        recyclerViewRelatedNotes.layoutManager = noteLayoutManager

        noteRecyclerAdapter.setOnNoteClickedListener(object :
            HomeNoteRecyclerAdapter.OnNoteClickedListener {
            override fun onNoteClicked(note: Note) {
                bottomSheetDialog.dismiss()

                val view1 = layoutInflater.inflate(R.layout.dialog_new_note, null)
                val bottomSheetDialog1 = BottomSheetDialog(
                    requireContext(),
                    R.style.BottomSheetDialogTheme
                )

                val courseList = arrayListOf<String>()
                val adapterCourses = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    courseList
                )

                courseList.add(getString(R.string.select_a_course))
                courseList.add(getString(R.string.no_course))

                viewModel.allCourses.observe(viewLifecycleOwner) { courses ->
                    for (course1 in courses) {
                        courseList.add(course.courseTitle)
                    }
                    adapterCourses.notifyDataSetChanged()
                }

                val spinnerCourses: Spinner = view1.findViewById(R.id.spinner_courses)
                val etNoteTitle: EditText = view1.findViewById(R.id.et_note_title)
                val etNoteContent: EditText = view1.findViewById(R.id.et_note_content)
                val btnSave: MaterialButton = view1.findViewById(R.id.btn_save)
                val btnCancel: MaterialButton = view1.findViewById(R.id.btn_cancel)
                val tvNoteDialogTitle: TextView = view1.findViewById(R.id.tv_note_dialog_title)

                adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerCourses.adapter = adapterCourses

                tvNoteDialogTitle.text = getString(R.string.edit_note)
                etNoteTitle.setText(note.title)
                etNoteContent.setText(note.content)
                val coursePosition = if (note.courseCode != getString(R.string.nil)) {
                    adapterCourses.getPosition(note.courseCode?.let { viewModel.getCourseTitle(it) })
                } else
                    1

                spinnerCourses.setSelection(coursePosition)
                btnCancel.setOnClickListener { bottomSheetDialog.dismiss() }
                btnSave.setOnClickListener {
                    val courseTitle = spinnerCourses.selectedItem.toString()
                    val courseCode = if (courseTitle == getString(R.string.no_course))
                        getString(R.string.nil)
                    else
                        viewModel.getCourseCode(courseTitle)
                    val noteTitle = etNoteTitle.text.toString()
                    val noteContent = etNoteContent.text.toString()
                    if (noteTitle.isNotEmpty() && noteContent.isNotEmpty()) {
                        if (courseTitle != getString(R.string.select_a_course)) {
                            if (courseCode != note.courseCode || noteTitle != note.title || noteContent != note.content) {
                                val note1 = Note(courseCode, noteTitle, noteContent)
                                note1.id = note.id
                                viewModel.updateNote(note1)
                                viewModel.getBookMarkAt(note.id)
                                    ?.observe(viewLifecycleOwner) { bookMarks ->
                                        val noteId = note1.id
                                        for (bookMark in bookMarks) {
                                            val bookMark1 =
                                                BookMark(noteId, courseCode, noteTitle, noteContent)
                                            bookMark1.id = bookMark.id
                                            viewModel.updateBookMark(bookMark1)
                                        }
                                    }
                                bottomSheetDialog1.dismiss()
                            } else
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.note_not_changed),
                                    Toast.LENGTH_LONG
                                ).show()
                        } else
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.select_a_course),
                                Toast.LENGTH_LONG
                            ).show()
                    } else
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.all_fields_are_required),
                            Toast.LENGTH_LONG
                        ).show()
                }
                bottomSheetDialog1.setContentView(view1)
                bottomSheetDialog1.show()
            }
        })
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }

    private fun launchEditCourseDialog(course: Course) {
        val view = layoutInflater.inflate(R.layout.dialog_new_course, null)
        val bottomSheetDialog = BottomSheetDialog(requireContext())

        val tvCourseDialogTitle: MaterialTextView = view.findViewById(R.id.tv_course_dialog_title)
        val etCourseCode: EditText = view.findViewById(R.id.et_course_code)
        val etCourseTitle: EditText = view.findViewById(R.id.et_course_title)
        val numberPickerCourseUnit: NumberPicker = view.findViewById(R.id.number_picker_course_unit)
        val btnSave: MaterialButton = view.findViewById(R.id.btn_save)
        val btnCancel: MaterialButton = view.findViewById(R.id.btn_cancel)

        numberPickerCourseUnit.minValue = 1
        numberPickerCourseUnit.maxValue = 4
        numberPickerCourseUnit.value = course.courseUnit

        tvCourseDialogTitle.text = getString(R.string.edit_course)
        etCourseCode.setText(course.courseCode)
        etCourseTitle.setText(course.courseTitle)

        btnCancel.setOnClickListener { bottomSheetDialog.dismiss() }
        btnSave.text = getString(R.string.update)
        btnSave.setOnClickListener {
            val courseCode = etCourseCode.text.toString()
            val courseTitle = etCourseTitle.text.toString()
            val courseUnit = numberPickerCourseUnit.value
            val courseMark = course.courseMark
            val courseGrade = course.courseGrade
            val courseGradePoint = course.courseGradePoint
            if (courseCode.isNotEmpty() && courseTitle.isNotEmpty()) {
                if (courseCode != course.courseCode ||
                    courseTitle != course.courseTitle ||
                    courseUnit != course.courseUnit
                ) {
                    val course1 = Course(
                        courseCode,
                        courseTitle,
                        courseUnit,
                        courseMark,
                        courseGrade,
                        courseGradePoint
                    )
                    course1.id = course.id
                    viewModel.getNotesAt(course.courseCode)?.observe(viewLifecycleOwner) { notes ->
                        if (notes.isNotEmpty()) {
                            for (note in notes) {
                                val noteTitle = note.title
                                val noteContent = note.content
                                val note1 = Note(courseCode, noteTitle, noteContent)
                                note1.id = note.id
                                viewModel.updateNote(note1)

                                viewModel.getBookMarkAt(note.id)
                                    ?.observe(viewLifecycleOwner) { bookMarks ->
                                        if (bookMarks.isNotEmpty()) {
                                            for (bookMark in bookMarks) {
                                                val bookMark1 = BookMark(
                                                    note.id,
                                                    courseCode,
                                                    noteTitle,
                                                    noteContent!!
                                                )
                                                bookMark1.id = bookMark.id
                                                viewModel.updateBookMark(bookMark1)
                                            }
                                        }
                                    }
                            }
                        }
                    }
                    viewModel.updateCourse(course1)
                    bottomSheetDialog.dismiss()
                } else
                    Toast.makeText(
                        context,
                        getString(R.string.course_not_changed),
                        Toast.LENGTH_SHORT
                    ).show()
            } else
                Toast.makeText(
                    requireContext(),
                    getString(R.string.all_fields_are_required),
                    Toast.LENGTH_LONG
                ).show()
        }
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }

    private fun searchCourses(query: String, courseRecyclerAdapter: CourseRecyclerAdapter) {
        val searchQuery = "%$query%"
        viewModel.searchCourses(searchQuery)
            ?.observe(viewLifecycleOwner, courseRecyclerAdapter::submitList)
    }

    private fun showPopupMenu(view: View) {
        val menu = PopupMenu(requireContext(), view)
        menu.setOnMenuItemClickListener(this)
        menu.inflate(R.menu.course_menu)
        menu.show()
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        val id = item!!.itemId
        if (id == R.id.delete_all_courses) {
            launchDeleteDialog()
        }
        return true
    }

    private fun launchDeleteDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setTitle(getString(R.string.delete))
        builder.setMessage(getString(R.string.all_course_delete_dialog_message))
        builder.setIcon(R.drawable.ic_baseline_delete_24)
        builder.setPositiveButton(getString(R.string.yes)) { dialog1, _ ->
            viewModel.deleteAllCourses()
            viewModel.getDeletableNotes(getString(R.string.nil))?.observe(viewLifecycleOwner) {
                for (note in it)
                    viewModel.deleteNote(note)
            }
            viewModel.getDeletableBookmarks(getString(R.string.nil))?.observe(viewLifecycleOwner) {
                for (bookmark in it)
                    viewModel.deleteBookMark(bookmark)
            }
            dialog1.dismiss()
        }
        builder.setNegativeButton(getString(R.string.no)) { dialog1, _ -> dialog1.dismiss() }
        val alertDialog = builder.create()
        alertDialog.setOnShowListener {
            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black
                    )
                )
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black
                    )
                )
            } else {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.red
                    )
                )
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.red
                    )
                )
            }
        }
        alertDialog.show()
    }

    private fun launchDeleteCourseDialog(course: Course) {
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setIcon(R.drawable.ic_baseline_delete_24)
        builder.setTitle(getString(R.string.delete))
        builder.setMessage(getString(R.string.course_delete_dialog_message))
        builder.setPositiveButton(getString(R.string.yes)) { dialog, _ ->
            viewModel.deleteCourse(course)
            viewModel.getNotesAt(course.courseCode)?.observe(viewLifecycleOwner) { notes ->
                if (notes != null) {
                    for (note in notes) {
                        viewModel.deleteNote(note)
                        viewModel.getBookMarkAt(note.id)?.observe(viewLifecycleOwner) { bookMarks ->
                            if (bookMarks != null) {
                                for (bookMark in bookMarks)
                                    viewModel.deleteBookMark(bookMark)
                            }
                        }
                    }
                }
            }
            dialog.dismiss()
        }
        builder.setNegativeButton(getString(R.string.no)) { dialog, _ -> dialog.dismiss() }
        val alertDialog = builder.create()
        alertDialog.setOnShowListener {
            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black
                    )
                )
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black
                    )
                )
            } else {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.red
                    )
                )
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.red
                    )
                )
            }
        }
        alertDialog.show()
    }
}