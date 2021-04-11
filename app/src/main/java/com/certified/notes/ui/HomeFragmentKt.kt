package com.certified.notes.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.certified.notes.R
import com.certified.notes.adapters.HomeCourseRecyclerAdapter
import com.certified.notes.adapters.HomeNoteRecyclerAdapter
import com.certified.notes.adapters.TodoRecyclerAdapter
import com.certified.notes.model.Note
import com.certified.notes.model.Todo
import com.certified.notes.room.NotesViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView

class HomeFragmentKt : Fragment(), View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    companion object {
        private const val ID_NOT_SET = 0
    }

    private lateinit var recyclerCourses: RecyclerView
    private lateinit var recyclerNotes: RecyclerView
    private lateinit var recyclerTodos: RecyclerView
    private lateinit var btnShowAllNotes: MaterialButton
    private lateinit var btnShowAllCourses: MaterialButton

    //    private lateinit var btnShowAllNotes: MaterialButton
//    private lateinit var btnShowAllNotes: MaterialButton
    private lateinit var ivTodoPopupMenu: ImageView
    private lateinit var navController: NavController
    private lateinit var viewModel: NotesViewModel
    private lateinit var cardView: MaterialCardView
    private lateinit var todoRecyclerAdapter: TodoRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerCourses = view.findViewById(R.id.recycler_view_courses)
        recyclerNotes = view.findViewById(R.id.recycler_view_notes)
        recyclerTodos = view.findViewById(R.id.recycler_view_todos)

        btnShowAllCourses = view.findViewById(R.id.btn_show_all_courses)
        btnShowAllNotes = view.findViewById(R.id.btn_show_all_notes)

        ivTodoPopupMenu = view.findViewById(R.id.iv_todo_popup_menu)
        cardView = view.findViewById(R.id.card_view)

        btnShowAllNotes.setOnClickListener(this)
        btnShowAllCourses.setOnClickListener(this)
        ivTodoPopupMenu.setOnClickListener(this)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        viewModel = NotesViewModel(requireActivity().application)

        init()
    }

    private fun init() {
        val noteLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val courseLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val todoLayoutManager = LinearLayoutManager(requireContext())

        val noteRecyclerAdapter = HomeNoteRecyclerAdapter(ID_NOT_SET)
        viewModel.randomNotes.observe(viewLifecycleOwner) { notes ->
            if (notes != null) {
                noteRecyclerAdapter.submitList(notes)
                btnShowAllNotes.isClickable = true
            } else {
                btnShowAllNotes.isClickable = false
            }
        }

        recyclerNotes.adapter = noteRecyclerAdapter
        recyclerNotes.layoutManager = noteLayoutManager
        recyclerNotes.clipToPadding = false
        recyclerNotes.clipChildren = false
        noteRecyclerAdapter.setOnNoteClickedListener(object :
            HomeNoteRecyclerAdapter.OnNoteClickedListener {
            override fun onNoteClicked(note: Note) {
                navController.navigate(R.id.notesFragment)
            }
        })

        val courseRecyclerAdapter = HomeCourseRecyclerAdapter()
        viewModel.randomCourses.observe(viewLifecycleOwner) { courses ->
            if (courses != null) {
                courseRecyclerAdapter.submitList(courses)
                btnShowAllCourses.isClickable = true
            } else {
                btnShowAllCourses.isClickable = false
            }
        }

        recyclerCourses.layoutManager = courseLayoutManager
        recyclerCourses.adapter = courseRecyclerAdapter
        recyclerCourses.clipToPadding = false
        recyclerCourses.clipChildren = false
        courseRecyclerAdapter.setOnCourseClickedListener(object :
            HomeCourseRecyclerAdapter.OnCourseClickedListener {
            override fun onCourseClicked() {
                navController.navigate(R.id.coursesFragment)
            }
        })

        todoRecyclerAdapter = TodoRecyclerAdapter(requireContext(), viewModel)
        viewModel.allTodos.observe(viewLifecycleOwner) { todos ->
            if (todos.isNotEmpty())
                todoRecyclerAdapter.submitList(todos)
            else
                cardView.visibility = View.GONE
        }

        recyclerTodos.layoutManager = todoLayoutManager
        recyclerTodos.adapter = todoRecyclerAdapter
        todoRecyclerAdapter.setOnTodoClickedListener(object : TodoRecyclerAdapter.OnTodoClickedListener {
            override fun onTodoClicked(todo: Todo) {
                val view = layoutInflater.inflate(R.layout.dialog_new_todo, null)

                val bottomSheetDialog = BottomSheetDialog(requireContext())

                val tvTodoDialogTitle: MaterialTextView = view.findViewById(R.id.tv_todo_dialog_title)
                val etTodo: EditText = view.findViewById(R.id.et_todo)
                val btnSave: MaterialButton = view.findViewById(R.id.btn_save)
                val btnCancel: MaterialButton = view.findViewById(R.id.btn_cancel)
            }

        })
    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        TODO("Not yet implemented")
    }
}