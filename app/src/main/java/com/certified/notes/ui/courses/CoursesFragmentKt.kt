package com.certified.notes.ui.courses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.certified.notes.R
import com.certified.notes.adapters.CourseRecyclerAdapter
import com.certified.notes.adapters.HomeNoteRecyclerAdapter
import com.certified.notes.model.Course
import com.certified.notes.model.Note
import com.certified.notes.util.Extensions.showToast
import com.certified.notes.ui.EditNoteFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.shawnlin.numberpicker.NumberPicker

class CoursesFragmentKt : Fragment(), PopupMenu.OnMenuItemClickListener {

    companion object {
        private const val GRID_SPAN_COUNT = 2
    }

    private lateinit var recyclerCourses: RecyclerView
    private lateinit var viewModel: CoursesViewModel
    private lateinit var ivCoursePopupMenu: ImageView
    private lateinit var svSearchCourses: SearchView
    private lateinit var courseCode: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_courses, container, false)

        recyclerCourses = view.findViewById(R.id.recycler_view_courses)
        ivCoursePopupMenu = view.findViewById(R.id.iv_course_popup_menu)

        svSearchCourses = view.findViewById(R.id.sv_search_database)

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
            GridLayoutManager(context, GRID_SPAN_COUNT, LinearLayoutManager.VERTICAL, false)
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

    private fun launchRelatedNotesDialog(course: Course) {
        val view = layoutInflater.inflate(
            R.layout.dialog_related_notes,
            ConstraintLayout(requireContext())
        )
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val recyclerViewRelatedNotes: RecyclerView =
            view.findViewById(R.id.recycler_view_related_notes)
        val noteLayoutManager = LinearLayoutManager(requireContext())
        val noteRecyclerAdapter = HomeNoteRecyclerAdapter(1, null)

        viewModel.getNotesAt(course.courseCode)?.observe(viewLifecycleOwner) { notes ->
            if (notes.isNotEmpty()) {
                noteRecyclerAdapter.submitList(notes)
            } else {
//                val handler = Handler(Looper.myLooper()!!)
//                handler.postDelayed({
//                    showToast(getString(R.string.no_related_notes))
//                    bottomSheetDialog.dismiss()
//                }, 500)
                showToast(getString(R.string.no_related_notes))
                bottomSheetDialog.dismiss()
            }
        }
        recyclerViewRelatedNotes.adapter = noteRecyclerAdapter
        recyclerViewRelatedNotes.layoutManager = noteLayoutManager

        noteRecyclerAdapter.setOnNoteClickedListener(object :
            HomeNoteRecyclerAdapter.OnNoteClickedListener {
            override fun onNoteClicked(note: Note) {
                bottomSheetDialog.dismiss()
                launchNoteDialog(note, Firebase.auth.currentUser)
            }
        })
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }

    private fun launchEditCourseDialog(course: Course) {
        val view =
            layoutInflater.inflate(R.layout.dialog_new_course, ConstraintLayout(requireContext()))
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
            if (courseCode.isNotEmpty() && courseTitle.isNotEmpty()) {
                if (courseCode != course.courseCode ||
                    courseTitle != course.courseTitle ||
                    courseUnit != course.courseUnit
                ) {
                    viewModel.getNotesAt(course.courseCode)?.observe(viewLifecycleOwner) { notes ->
                        if (notes.isNotEmpty()) {
                            for (note in notes) {
                                val noteTitle = note.title
                                val noteContent = note.content
                                val note1 = note.copy(
                                    courseCode = courseCode,
                                    title = noteTitle,
                                    content = noteContent
                                )
                                note1.id = note.id
                                viewModel.updateNote(note1)

                                viewModel.getBookMarkWith(note.id)
                                    ?.observe(viewLifecycleOwner) {
                                        if (it != null) {
                                            val bookMark = it.copy(
                                                noteId = note.id,
                                                courseCode = courseCode,
                                                noteTitle = noteTitle,
                                                noteContent = noteContent!!
                                            )
                                            bookMark.id = it.id
                                            viewModel.updateBookMark(bookMark)
                                        }
                                    }
                            }
                        }
                    }
                    val course1 = course.copy(
                        courseCode = courseCode,
                        courseTitle = courseTitle,
                        courseUnit = courseUnit
                    )
                    course1.id = course.id
                    viewModel.updateCourse(course1)
                    bottomSheetDialog.dismiss()
                } else
                    showToast(getString(R.string.course_not_changed))
            } else
                showToast(getString(R.string.all_fields_are_required))
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
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.accent
                )
            )
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.accent
                )
            )
        }
        alertDialog.show()
    }

    private fun launchNoteDialog(note: Note, currentUser: FirebaseUser?) {
        val fragmentManager = requireActivity().supportFragmentManager
        val completeOrderFragment =
            EditNoteFragment(note = note, which = "note", user = currentUser)
        val transaction = fragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .add(android.R.id.content, completeOrderFragment)
            .addToBackStack(null)
            .commit()
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
                        viewModel.getBookMarkWith(note.id)
                            ?.observe(viewLifecycleOwner) {
                                if (it != null)
                                    viewModel.deleteBookMark(it)
                            }
                    }
                }
            }
            dialog.dismiss()
        }
        builder.setNegativeButton(getString(R.string.no)) { dialog, _ -> dialog.dismiss() }
        val alertDialog = builder.create()
        alertDialog.setOnShowListener {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.accent
                )
            )
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.accent
                )
            )
        }
        alertDialog.show()
    }
}