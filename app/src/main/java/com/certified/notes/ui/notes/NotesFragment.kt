package com.certified.notes.ui.notes

import android.content.SharedPreferences
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.certified.notes.R
import com.certified.notes.adapters.NoteRecyclerAdapter
import com.certified.notes.model.BookMark
import com.certified.notes.model.Note
import com.certified.notes.util.PreferenceKeys
import com.certified.notes.ui.EditNoteFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

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
//        val noteLayoutManager = LinearLayoutManager(requireContext())
        val noteRecyclerAdapter by lazy { NoteRecyclerAdapter(requireContext()) }

        viewModel.allNotes.observe(viewLifecycleOwner, noteRecyclerAdapter::submitList)
        recyclerNotes.adapter = noteRecyclerAdapter
        recyclerNotes.layoutManager = GridLayoutManager(requireContext(), 2)

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
            override fun onNoteClick(note: Note) {
                launchNoteDialog(note, Firebase.auth.currentUser)
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
                            val note =
                                noteRecyclerAdapter.getNoteAt(viewHolder.absoluteAdapterPosition)

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

                    ItemTouchHelper.RIGHT -> {
                        val note = noteRecyclerAdapter.getNoteAt(viewHolder.absoluteAdapterPosition)
                        if (note != null) {
                            val noteId = note.id
                            val courseCode = note.courseCode
                            val noteTitle = note.title
                            val noteContent = note.content
                            val bookMark = BookMark(noteId, courseCode, noteTitle, noteContent!!)

                            viewModel.getBookMarkWith(noteId)
                                ?.observe(viewLifecycleOwner) { bookMarks ->
                                    if (bookMarks == null) {
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
                    .addSwipeRightActionIcon(R.drawable.ic_baseline_bookmark_50)
                    .addSwipeRightBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.teal_700
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
        }).attachToRecyclerView(recyclerNotes)

        svSearchNotes.isSubmitButtonEnabled
        svSearchNotes.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    searchNotes(query, noteRecyclerAdapter)
                }
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                if (query != null) {
                    searchNotes(query, noteRecyclerAdapter)
                }
                return true
            }
        })
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

    private fun searchNotes(query: String, noteRecyclerAdapter: NoteRecyclerAdapter) {
        val searchQuery = "%$query%"
        viewModel.searchNotes(searchQuery)
            ?.observe(viewLifecycleOwner, noteRecyclerAdapter::submitList)
    }

    private fun showPopupMenu(view: View) {
        val menu = PopupMenu(requireContext(), view)
        menu.inflate(R.menu.note_menu)
        menu.setOnMenuItemClickListener(this)
        menu.show()
    }

    private fun launchDeleteDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setTitle(getString(R.string.delete))
        builder.setMessage(getString(R.string.all_note_delete_dialog_message))
        builder.setIcon(R.drawable.ic_baseline_delete_24)
        builder.setPositiveButton(getString(R.string.yes)) { dialog, _ ->
            viewModel.deleteAllNotes()
            viewModel.deleteAllBookMarks()
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

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.delete_all_notes -> launchDeleteDialog()
        }
        return true
    }
}