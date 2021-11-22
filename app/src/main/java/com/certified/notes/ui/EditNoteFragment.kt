package com.certified.notes.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.certified.notes.R
import com.certified.notes.databinding.FragmentEditNoteBinding
import com.certified.notes.model.BookMark
import com.certified.notes.model.Note
import com.certified.notes.util.Extensions.showToast
import com.certified.notes.ui.bookMarks.BookMarksViewModel
import com.certified.notes.ui.bookMarks.BookMarksViewModelFactory
import com.certified.notes.ui.notes.NotesViewModel
import com.certified.notes.ui.notes.NotesViewModelFactory
import com.google.firebase.auth.FirebaseUser

class EditNoteFragment(
    val note: Note? = null,
    val bookMark: BookMark? = null,
    val which: String,
    val user: FirebaseUser?
) : DialogFragment() {

    private lateinit var binding: FragmentEditNoteBinding
    private lateinit var notesViewModel: NotesViewModel
    private lateinit var bookMarksViewModel: BookMarksViewModel
    private var courseCode = "Not set"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditNoteBinding.inflate(layoutInflater)
        val notesViewModelFactory = NotesViewModelFactory(requireActivity().application)
        val bookMarksViewModelFactory = BookMarksViewModelFactory(requireActivity().application)
        notesViewModel =
            ViewModelProvider(this, notesViewModelFactory).get(NotesViewModel::class.java)
        bookMarksViewModel =
            ViewModelProvider(this, bookMarksViewModelFactory).get(BookMarksViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnClose.setOnClickListener { dismiss() }
            when (which) {
                "note" -> {
                    val courseList = ArrayList<String>()
                    val adapterCourses = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        courseList
                    )

                    courseList.add(getString(R.string.select_a_course))
                    courseList.add(getString(R.string.no_course))
                    notesViewModel.allCourses.observe(viewLifecycleOwner) { courses ->
                        for (course in courses)
                            courseList.add(course.courseTitle)
                        adapterCourses.notifyDataSetChanged()

                        adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinnerCourses.adapter = adapterCourses
                    }

                    if (note != null) {
                        tvDialogTitle.text = "Edit NOTE"
                        etNoteTitle.setText(note.title)
                        etNoteContent.setText(note.content)
                        notesViewModel.getCourseTitle(note.courseCode)
                            ?.observe(viewLifecycleOwner) { courseTitle ->
                                val coursePosition =
                                    if (note.courseCode != getString(R.string.nil))
                                        adapterCourses.getPosition(courseTitle)
                                    else 1
                                coursePosition.let { spinnerCourses.setSelection(it) }
                            }
                    } else
                        tvDialogTitle.text = "ADD NOTE"

                    btnUpdate.setOnClickListener {

                        val noteTitle = etNoteTitle.text.toString().trim()
                        val noteContent = etNoteContent.text.toString().trim()

                        if (noteTitle.isNotBlank() && noteContent.isNotBlank()) {
                            if (spinnerCourses.selectedItemPosition != 0 && courseCode != "Not set") {
                                if (spinnerCourses.selectedItemPosition == 1)
                                    courseCode = getString(R.string.nil)
                                else
                                    notesViewModel.getCourseCode(spinnerCourses.selectedItem.toString())
                                        ?.observe(viewLifecycleOwner) { code -> courseCode = code }
                                if (user == null && note != null) {
                                    updateNote(note.id, courseCode, noteTitle, noteContent, "note")
                                    updateBookMark(
                                        note.id,
                                        courseCode,
                                        noteTitle,
                                        noteContent,
                                        "note"
                                    )
                                    dismiss()
                                } else if (user == null && note == null) {
                                    val newNote = Note(courseCode, noteTitle, noteContent)
                                    notesViewModel.insertNote(newNote)
                                    dismiss()
                                }
                            } else
                                showToast("Please select a course")
                        } else
                            showToast("All fields are required")
                    }
                }
                "bookMark" -> {
                    val courseList = ArrayList<String>()
                    val adapterCourses = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        courseList
                    )

                    courseList.add(getString(R.string.select_a_course))
                    courseList.add(getString(R.string.no_course))
                    bookMarksViewModel.allCourses.observe(viewLifecycleOwner) { courses ->
                        for (course in courses)
                            courseList.add(course.courseTitle)
                        adapterCourses.notifyDataSetChanged()

                        adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinnerCourses.adapter = adapterCourses
                    }

                    if (bookMark != null) {
                        tvDialogTitle.text = "Edit NOTE"
                        etNoteTitle.setText(bookMark.noteTitle)
                        etNoteContent.setText(bookMark.noteContent)
                        bookMarksViewModel.getCourseTitle(bookMark.courseCode)
                            ?.observe(viewLifecycleOwner) { courseTitle ->
                                val coursePosition =
                                    if (bookMark.courseCode != getString(R.string.nil))
                                        adapterCourses.getPosition(courseTitle)
                                    else 1
                                coursePosition.let { spinnerCourses.setSelection(it) }
                            }
                        courseCode = bookMark.courseCode
                    } else
                        spinnerCourses.setSelection(0)

                    btnUpdate.setOnClickListener {

                        val noteTitle = etNoteTitle.text.toString().trim()
                        val noteContent = etNoteContent.text.toString().trim()

                        if (noteTitle.isNotBlank() && noteContent.isNotBlank()) {
                            if (spinnerCourses.selectedItemPosition != 0 && courseCode != "Not set") {
                                if (spinnerCourses.selectedItemPosition == 1)
                                    courseCode = getString(R.string.nil)
                                else
                                    bookMarksViewModel.getCourseCode(spinnerCourses.selectedItem.toString())
                                        ?.observe(viewLifecycleOwner) { code -> courseCode = code }

                                if (user == null && bookMark != null) {
                                    updateNote(
                                        bookMark.noteId,
                                        courseCode,
                                        noteTitle,
                                        noteContent,
                                        "bookMark"
                                    )
                                    val newBookMark = bookMark.copy(
                                        courseCode = courseCode,
                                        noteTitle = noteTitle,
                                        noteContent = noteContent
                                    )
                                    newBookMark.id = bookMark.id
                                    bookMarksViewModel.updateBookMark(newBookMark)
                                    updateBookMark(
                                        bookMark.noteId,
                                        courseCode,
                                        noteTitle,
                                        noteContent,
                                        "bookMark"
                                    )
                                    dismiss()
                                }
                            } else
                                showToast("Please select a course")
                        } else
                            showToast("All fields are required")
                    }
                }
            }
        }
    }

    private fun updateNote(
        id: Int,
        courseCode: String,
        noteTitle: String,
        noteContent: String,
        which: String
    ) {
        val newNote = Note(courseCode, noteTitle, noteContent)
        newNote.id = id
        when (which) {
            "note" -> notesViewModel.updateNote(newNote)
            "bookMark" -> bookMarksViewModel.updateNote(newNote)
        }
    }

    private fun updateBookMark(
        noteId: Int,
        courseCode: String,
        noteTitle: String,
        noteContent: String,
        which: String
    ) {
        when (which) {
            "note" -> notesViewModel.getBookMarkWith(noteId)
                ?.observe(viewLifecycleOwner) { bookMark ->
                    notesViewModel.updateBookMark(
                        bookMark.copy(
                            noteId = noteId,
                            courseCode = courseCode,
                            noteTitle = noteTitle,
                            noteContent = noteContent
                        )
                    )
                }
            "bookMark" -> {
                val newBookMark = bookMark!!.copy(
                    courseCode = courseCode,
                    noteTitle = noteTitle,
                    noteContent = noteContent
                )
                newBookMark.id = bookMark.id
                bookMarksViewModel.updateBookMark(newBookMark)
            }
        }
    }
}