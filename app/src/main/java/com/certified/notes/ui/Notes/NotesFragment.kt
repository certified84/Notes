package com.certified.notes.ui.Notes

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.certified.notes.R
import com.certified.notes.adapters.NoteRecyclerAdapter
import com.certified.notes.model.BookMark
import com.certified.notes.model.Note
import com.certified.notes.util.PreferenceKeys
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView
import io.sulek.ssml.SSMLLinearLayoutManager

class NotesFragment : Fragment(), PopupMenu.OnMenuItemClickListener {

    private lateinit var recyclerNotes: RecyclerView
    private lateinit var viewModel: NotesViewModel
    private lateinit var ivNotePopupMenu: ImageView
    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var defValues: MutableSet<String>
    private lateinit var noteIds: MutableSet<String>
    private lateinit var svSearchNotes: SearchView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notes, container, false)

        recyclerNotes = view.findViewById(R.id.recycler_view_notes)
        ivNotePopupMenu = view.findViewById(R.id.iv_note_popup_menu)

        svSearchNotes = view.findViewById(R.id.sv_search_database)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModelFactory = NotesViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(NotesViewModel::class.java)
        preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        ivNotePopupMenu.setOnClickListener(this::showPopupMenu)

        defValues = HashSet()
        defValues.add("-1")
        noteIds = HashSet(preferences.getStringSet(PreferenceKeys.NOTE_IDS, defValues))

        init()
    }

    private fun init() {
        val noteLayoutManager = LinearLayoutManager(requireContext())
        val noteRecyclerAdapter = NoteRecyclerAdapter(requireContext())

        viewModel.allNotes.observe(viewLifecycleOwner, noteRecyclerAdapter::submitList)
        recyclerNotes.adapter = noteRecyclerAdapter
        recyclerNotes.layoutManager = SSMLLinearLayoutManager(requireContext())

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

        noteRecyclerAdapter.setOnNoteClickedListener(object :
            NoteRecyclerAdapter.OnNoteClickedListener {
            override fun onNoteClick(note: Note?) {
                val view = layoutInflater.inflate(
                    R.layout.dialog_new_note,
                    ConstraintLayout(requireContext())
                )
                val bottomSheetDialog = BottomSheetDialog(requireContext())

                val spinnerCourses: Spinner = view.findViewById(R.id.spinner_courses)
                val etNoteTitle: EditText = view.findViewById(R.id.et_note_title)
                val etNoteContent: EditText = view.findViewById(R.id.et_note_content)
                val btnSave: MaterialButton = view.findViewById(R.id.btn_save)
                val btnCancel: MaterialButton = view.findViewById(R.id.btn_cancel)
                val tvNoteDialogTitle: MaterialTextView =
                    view.findViewById(R.id.tv_note_dialog_title)

                adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerCourses.adapter = adapterCourses

                tvNoteDialogTitle.text = getString(R.string.edit_note)
                etNoteTitle.setText(note?.title)
                etNoteContent.setText(note?.content)
                val coursePosition = if (note?.courseCode != getString(R.string.nil))
                    adapterCourses.getPosition(note?.courseCode?.let { viewModel.getCourseTitle(it) })
                else 1

                spinnerCourses.setSelection(coursePosition)

                btnCancel.setOnClickListener { bottomSheetDialog.dismiss() }
                btnSave.text = getString(R.string.update)
                btnSave.setOnClickListener {
                    val courseTitle = spinnerCourses.selectedItem.toString()
                    val courseCode =
                        if (courseTitle == getString(R.string.no_course)) getString(R.string.nil)
                        else viewModel.getCourseCode(courseTitle)
                    val noteTitle = etNoteTitle.text.toString().trim()
                    val noteContent = etNoteContent.text.toString().trim()

                    if (noteTitle.isNotEmpty() && noteContent.isNotEmpty()) {
                        if (courseTitle != getString(R.string.select_a_course)) {
//                            if (courseTitle != getString(R.string.no_course)) {
                            if (courseCode != note?.courseCode || noteTitle != note.title || noteContent != note.content) {
                                val note1 = Note(courseCode, noteTitle, noteContent)
                                note1.id = note!!.id
                                viewModel.updateNote(note1)
                                viewModel.getBookMarkAt(note.id)
                                    ?.observe(viewLifecycleOwner) { bookMarks ->
                                        if (bookMarks != null) {
                                            val noteId = note1.id
                                            for (bookMark in bookMarks) {
                                                val bookMark1 = BookMark(
                                                    noteId,
                                                    courseCode,
                                                    noteTitle,
                                                    noteContent
                                                )
                                                bookMark1.id = bookMark.id
                                                viewModel.updateBookMark(bookMark1)
                                            }
                                        }
                                    }
                                noteRecyclerAdapter.notifyDataSetChanged()
                                bottomSheetDialog.dismiss()
                            }
//                            } else {
//                                if (note != null) {
//                                    if (courseCode != getString(R.string.nil) || noteTitle != note.title || noteContent != note?.content) {
//                                        val note1 = Note(courseCode, noteTitle, noteContent)
//                                        note1.id = note.id
//                                        viewModel.updateNote(note1)
//                                        viewModel.getBookMarkAt(note.id)?.observe(viewLifecycleOwner) { bookMarks ->
//                                            if (bookMarks != null) {
//                                                val noteId = note1.id
//                                                for(bookMark in bookMarks) {
//                                                    val bookMark1 = BookMark(noteId, courseCode, noteTitle, noteContent)
//                                                    bookMark1.id = bookMark.id
//                                                    viewModel.updateBookMark(bookMark1)
//                                                }
//                                            }
//                                        }
//                                        bottomSheetDialog.dismiss()
//                                    } else
//                                        Toast.makeText(
//                                            requireContext(),
//                                            getString(R.string.note_not_changed),
//                                            Toast.LENGTH_LONG
//                                        ).show()
//                                }
//                            }
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
                bottomSheetDialog.setContentView(view)
                bottomSheetDialog.show()
            }
        })

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        val builder = MaterialAlertDialogBuilder(requireContext())
                        builder.setTitle(getString(R.string.delete))
                        builder.setMessage(getString(R.string.note_delete_dialog_message))
                        builder.setPositiveButton(getString(R.string.delete)) { dialog1, _ ->
                            val note = noteRecyclerAdapter.getNoteAt(viewHolder.adapterPosition)

                            note?.let { viewModel.deleteNote(it) }
                            note?.id?.let { viewModel.deleteBookMarkedNote(it) }
                            noteIds.remove(note?.id.toString())

                            editor = preferences.edit()
                            editor.putStringSet(PreferenceKeys.NOTE_IDS, noteIds)
                            editor.apply()

                            dialog1.dismiss()
                        }
                        builder.setNegativeButton(getString(R.string.cancel)) { dialog1, _ ->
                            noteRecyclerAdapter.notifyDataSetChanged()
                            dialog1.dismiss()
                        }
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

                    ItemTouchHelper.RIGHT -> {
                        val note = noteRecyclerAdapter.getNoteAt(viewHolder.adapterPosition)
                        if (note != null) {
                            val noteId = note.id
                            val courseCode = note.courseCode
                            val noteTitle = note.title
                            val noteContent = note.content
                            val bookMark = BookMark(noteId, courseCode!!, noteTitle, noteContent!!)

                            viewModel.getBookMarkAt(noteId)
                                ?.observe(viewLifecycleOwner) { bookMarks ->
                                    if (bookMarks != null) {
                                        viewModel.insertBookMark(bookMark)
                                        noteIds.add(noteId.toString())
                                        editor = preferences.edit()
                                        editor.putStringSet(PreferenceKeys.NOTE_IDS, noteIds)
                                        editor.apply()
                                    }
                                }
                        }
                        noteRecyclerAdapter.notifyDataSetChanged()
                    }
                }
            }
        }).attachToRecyclerView(recyclerNotes)
    }

    private fun showPopupMenu(view: View) {
        val menu = PopupMenu(requireContext(), view)
        menu.inflate(R.menu.note_menu)
        menu.setOnMenuItemClickListener(this)
        menu.show()
    }

    private fun searchNotes(query: String, noteRecyclerAdapter: NoteRecyclerAdapter) {
        val searchQuery = "%$query%"
        viewModel.searchNotes(searchQuery)
            ?.observe(viewLifecycleOwner, noteRecyclerAdapter::submitList)
    }

    private fun launchDeleteDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setTitle(getString(R.string.delete))
        builder.setMessage(getString(R.string.note_delete_dialog_message))
        builder.setIcon(R.drawable.ic_baseline_delete_24)
        builder.setPositiveButton(getString(R.string.yes)) { dialog, _ ->
            viewModel.deleteAllNotes()
            viewModel.deleteAllBookMarks()
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

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.delete_all_notes -> launchDeleteDialog()
        }
        return true
    }
}