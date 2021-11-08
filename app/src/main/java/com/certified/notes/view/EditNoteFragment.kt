package com.certified.notes.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.certified.notes.R
import com.certified.notes.databinding.FragmentEditNoteBinding
import com.certified.notes.model.Note
import com.certified.notes.util.Extensions.showToast
import com.certified.notes.view.Notes.NotesViewModel
import com.certified.notes.view.Notes.NotesViewModelFactory
import com.google.firebase.auth.FirebaseUser

class EditNoteFragment(val note: Note?, val user: FirebaseUser?) : DialogFragment() {

    private lateinit var binding: FragmentEditNoteBinding
    private lateinit var viewModel: NotesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditNoteBinding.inflate(layoutInflater)
        val viewModelFactory = NotesViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(NotesViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            if (note != null) {
                tvDialogTitle.text = "Edit NOTE"
                etNoteTitle.setText(note.title)
                etNoteContent.setText(note.content)
            } else
                tvDialogTitle.text = "ADD NOTE"

            val courseList = ArrayList<String>()
            val adapterCourses = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                courseList
            )

            courseList.add(getString(R.string.select_a_course))
            courseList.add(getString(R.string.no_course))
            viewModel.allCourses.observe(viewLifecycleOwner) { courses ->
                for (course in courses)
                    courseList.add(course.courseTitle)
                adapterCourses.notifyDataSetChanged()
            }

            adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCourses.adapter = adapterCourses

            if (note != null)
                viewModel.getCourseTitle(note.courseCode)
                    ?.observe(viewLifecycleOwner) { courseTitle ->
                        val coursePosition =
                            if (note.courseCode != getString(com.certified.notes.R.string.nil))
                                adapterCourses.getPosition(courseTitle)
                            else 1
                        coursePosition.let { spinnerCourses.setSelection(it) }
                    }
            else
                spinnerCourses.setSelection(0)

            btnClose.setOnClickListener { dismiss() }
            btnUpdate.setOnClickListener {

                val courseCode = spinnerCourses.selectedItem.toString()
                val noteTitle = etNoteTitle.text.toString().trim()
                val noteContent = etNoteContent.text.toString().trim()

                if (noteTitle.isNotBlank() && noteContent.isNotBlank()) {
                    if (courseCode != getString(R.string.select_a_course)) {
                        if (user == null && note != null) {
                            val newNote = Note(courseCode, noteTitle, noteContent)
                            newNote.id = note.id
                            viewModel.updateNote(newNote)
                            dismiss()
                        } else if (user == null && note == null) {
                            val newNote = Note(courseCode, noteTitle, noteContent)
                            viewModel.insertNote(newNote)
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