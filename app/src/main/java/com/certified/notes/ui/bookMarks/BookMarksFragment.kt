package com.certified.notes.ui.bookMarks

import android.content.DialogInterface
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.certified.notes.R
import com.certified.notes.adapters.BookMarkRecyclerAdapter
import com.certified.notes.adapters.BookMarkRecyclerAdapter.OnBookMarkClickedListener
import com.certified.notes.model.BookMark
import com.certified.notes.model.Note
import com.certified.notes.util.PreferenceKeys
import com.certified.notes.ui.EditNoteFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

class BookMarksFragment : Fragment(), PopupMenu.OnMenuItemClickListener {

    private lateinit var recyclerBookMarks: RecyclerView
    private lateinit var viewModel: BookMarksViewModel
    private lateinit var ivBookMarkPopupMenu: ImageView
    private lateinit var svSearchBookMark: SearchView
    private lateinit var courseCode: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_book_marks, container, false)

        recyclerBookMarks = view.findViewById(R.id.recycler_view_notes)
        ivBookMarkPopupMenu = view.findViewById(R.id.iv_bookmark_popup_menu)

        svSearchBookMark = view.findViewById(R.id.sv_search_database)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModelFactory = BookMarksViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(BookMarksViewModel::class.java)
        ivBookMarkPopupMenu.setOnClickListener(this::showPopupMenu)

        init()
    }

    private fun init() {
        val bookMarkLayoutManager = GridLayoutManager(requireContext(), 2)
        val bookMarkRecyclerAdapter by lazy { BookMarkRecyclerAdapter() }

        viewModel.allBookMarks.observe(viewLifecycleOwner, bookMarkRecyclerAdapter::submitList)
        recyclerBookMarks.adapter = bookMarkRecyclerAdapter
        recyclerBookMarks.layoutManager = bookMarkLayoutManager

        val courseList = ArrayList<String>()
        val adapterCourses = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            courseList
        )

        courseList.add(0, getString(R.string.select_a_course))
        courseList.add(1, getString(R.string.no_course))

        viewModel.allCourses.observe(viewLifecycleOwner) {
            for (course in it) {
                courseList.add(course.courseTitle)
            }
            adapterCourses.notifyDataSetChanged()
        }
        bookMarkRecyclerAdapter.setOnBookMarkClickedListener(object : OnBookMarkClickedListener {
            override fun onBookMarkClick(bookmark: BookMark) {
                launchNoteDialog(bookmark, Firebase.auth.currentUser)

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
                val tvNoteDialogTitle: TextView = view.findViewById(R.id.tv_note_dialog_title)

                adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerCourses.adapter = adapterCourses

                tvNoteDialogTitle.text = getString(R.string.edit_note)
                etNoteTitle.setText(bookmark.noteTitle)
                etNoteContent.setText(bookmark.noteContent)

                viewModel.getCourseTitle(bookmark.courseCode)
                    ?.observe(viewLifecycleOwner) { courseTitle ->
                        val coursePosition = if (bookmark.courseCode != getString(R.string.nil))
                            adapterCourses.getPosition(courseTitle)
                        else 1

                        coursePosition.let { spinnerCourses.setSelection(it) }
                    }

                btnCancel.setOnClickListener { bottomSheetDialog.dismiss() }
                btnSave.text = getString(R.string.update)
                btnSave.setOnClickListener {
                    val noteId = bookmark.noteId
                    val courseTitle = spinnerCourses.selectedItem.toString()
                    if (spinnerCourses.selectedItemPosition == 1)
                        courseCode = getString(R.string.nil)
                    else
                        viewModel.getCourseCode(courseTitle)?.observe(viewLifecycleOwner) {
                            courseCode = it
                        }
                    val noteTitle = etNoteTitle.text.toString()
                    val noteContent = etNoteContent.text.toString()
                    if (noteTitle.isNotEmpty() && noteContent.isNotEmpty()) {
                        if (courseTitle != getString(R.string.select_a_course)) {
                            if (courseCode != bookmark.courseCode || noteTitle != bookmark.noteTitle || noteContent != bookmark.noteContent) {
                                if (courseTitle != getString(R.string.no_course)) {
                                    val note = Note(courseCode, noteTitle, noteContent)
                                    note.id = bookmark.noteId
                                    viewModel.updateNote(note)
                                    viewModel.getBookMarkWith(bookmark.noteId)
                                        ?.observe(viewLifecycleOwner) {
                                            viewModel.updateBookMark(
                                                it.copy(
                                                    noteId = noteId,
                                                    courseCode = courseCode,
                                                    noteTitle = noteTitle,
                                                    noteContent = noteContent
                                                )
                                            )
                                        }
                                } else {
                                    val note = Note("NIL", noteTitle, noteContent)
                                    note.id = bookmark.noteId
                                    viewModel.updateNote(note)
                                    viewModel.getBookMarkWith(bookmark.noteId)
                                        ?.observe(viewLifecycleOwner) {
                                            viewModel.updateBookMark(
                                                it.copy(
                                                    noteId = noteId,
                                                    courseCode = "NIL",
                                                    noteTitle = noteTitle,
                                                    noteContent = noteContent
                                                )
                                            )
                                        }
                                }
                                bottomSheetDialog.dismissWithAnimation
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Note not changed",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } else {
                            Toast.makeText(requireContext(), "Select a course", Toast.LENGTH_LONG)
                                .show()
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "All field are required",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                bottomSheetDialog.setContentView(view)
//                bottomSheetDialog.show()
            }
        })

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val bookMark =
                    bookMarkRecyclerAdapter.getBookMarkAt(viewHolder.absoluteAdapterPosition)

                val preferences =
                    PreferenceManager.getDefaultSharedPreferences(requireActivity().applicationContext)
                val editor = preferences.edit()

                val defValues = mutableSetOf<String>()
                defValues.add("-1")
                val noteIds = mutableSetOf<String>()
                preferences.getStringSet(PreferenceKeys.NOTE_IDS, defValues)?.let {
                    noteIds.addAll(it)
                }
                val builder = MaterialAlertDialogBuilder(requireContext())
                builder.setTitle(getString(R.string.delete))
                builder.setMessage(getString(R.string.bookmark_delete_dialog_message))
                builder.setPositiveButton(getString(R.string.delete)) { dialog1, _ ->
                    viewModel.deleteBookMark(bookMark)
                    noteIds.remove(bookMark.noteId.toString())

                    editor.putStringSet(PreferenceKeys.NOTE_IDS, noteIds)
                    editor.apply()

                    dialog1.dismiss()
                }
                builder.setNegativeButton(getString(R.string.cancel)) { dialog1, _ ->
                    bookMarkRecyclerAdapter.notifyDataSetChanged()
                    dialog1.dismiss()
                }
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

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                RecyclerViewSwipeDecorator.Builder(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_50)
                    .addSwipeLeftBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.red
                        )
                    )
                    .create()
                    .decorate()
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }

        }).attachToRecyclerView(recyclerBookMarks)

        svSearchBookMark.isSubmitButtonEnabled
        svSearchBookMark.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    searchBookmarks(query, bookMarkRecyclerAdapter)
                }
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                if (query != null) {
                    searchBookmarks(query, bookMarkRecyclerAdapter)
                }
                return true
            }

        })
    }

    private fun launchNoteDialog(bookMark: BookMark, currentUser: FirebaseUser?) {
        val fragmentManager = requireActivity().supportFragmentManager
        val completeOrderFragment =
            EditNoteFragment(bookMark = bookMark, which = "bookMark", user = currentUser)
        val transaction = fragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .add(android.R.id.content, completeOrderFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun searchBookmarks(query: String, bookMarkRecyclerAdapter: BookMarkRecyclerAdapter) {
        val searchQuery = "%$query%"
        viewModel.searchBookmarks(searchQuery)?.observe(
            viewLifecycleOwner,
            bookMarkRecyclerAdapter::submitList
        )
    }

    private fun showPopupMenu(view: View) {
        val menu = PopupMenu(requireContext(), view)
        menu.setOnMenuItemClickListener(this)
        menu.inflate(R.menu.bookmark_menu)
        menu.show()
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.delete_all_bookmarks)
            launchDeleteDialog()
        return true
    }

    private fun launchDeleteDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setTitle(getString(R.string.delete))
        builder.setMessage(getString(R.string.all_bookmark_delete_dialog_message))
        builder.setIcon(R.drawable.ic_baseline_delete_24)
        builder.setPositiveButton(getString(R.string.yes)) { dialog: DialogInterface, _: Int ->
            viewModel.deleteAllBookMarks()

            val preferences =
                PreferenceManager.getDefaultSharedPreferences(requireActivity().applicationContext)
            val editor = preferences.edit()
            val defValues = mutableSetOf<String>()
            defValues.add("-1")

            val noteIds = mutableSetOf<String>()
            preferences.getStringSet(PreferenceKeys.NOTE_IDS, defValues)?.let { noteIds.addAll(it) }
            preferences.getStringSet(PreferenceKeys.NOTE_IDS, defValues)
                ?.let { noteIds.removeAll(it) }

            editor.putStringSet(PreferenceKeys.NOTE_IDS, noteIds)
            editor.apply()

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