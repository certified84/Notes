package com.certified.notes.view.Home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.certified.notes.R
import com.certified.notes.adapters.HomeCourseRecyclerAdapter
import com.certified.notes.adapters.HomeNoteRecyclerAdapter
import com.certified.notes.adapters.TodoRecyclerAdapter
import com.certified.notes.model.Note
import com.certified.notes.model.Todo
import com.certified.notes.util.PreferenceKeys
import com.github.captain_miao.optroundcardview.OptRoundCardView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textview.MaterialTextView

class HomeFragment : Fragment(), View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    companion object {
        private const val ID_NOT_SET = 0
    }

    private lateinit var recyclerCourses: RecyclerView
    private lateinit var recyclerNotes: RecyclerView
    private lateinit var recyclerTodos: RecyclerView
    private lateinit var btnShowAllNotes: MaterialButton
    private lateinit var btnShowAllCourses: MaterialButton
    private lateinit var ivTodoPopupMenu: ImageView
    private lateinit var navController: NavController
    private lateinit var viewModel: HomeViewModel
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

        val viewModelFactory = HomeViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)

        activity?.findViewById<OptRoundCardView>(R.id.optRoundCardView2)?.visibility = View.VISIBLE
        activity?.findViewById<FloatingActionButton>(R.id.fab)?.visibility = View.VISIBLE

        isFirstOpen()
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
        todoRecyclerAdapter.setOnTodoClickedListener(object :
            TodoRecyclerAdapter.OnTodoClickedListener {
            override fun onTodoClicked(todo: Todo) {
                val view = layoutInflater.inflate(R.layout.dialog_new_todo, null)

                val bottomSheetDialog = BottomSheetDialog(requireContext())

                val tvTodoDialogTitle: MaterialTextView =
                    view.findViewById(R.id.tv_todo_dialog_title)
                val etTodo: EditText = view.findViewById(R.id.et_todo)
                val btnSave: MaterialButton = view.findViewById(R.id.btn_save)
                val btnCancel: MaterialButton = view.findViewById(R.id.btn_cancel)

                etTodo.setText(todo.todo)
                tvTodoDialogTitle.text = getString(R.string.edit_todo)
                btnCancel.setOnClickListener { bottomSheetDialog.dismiss() }
                btnSave.text = getString(R.string.update)
                btnSave.setOnClickListener {
                    val todoContent = etTodo.text.toString()
                    val done = todo.isDone
                    if (todoContent.isNotEmpty()) {
                        if (todoContent != todo.todo) {
                            val todo1 = Todo(todoContent, done)
                            todo1.id = todo.id
                            viewModel.updateTodo(todo1)
                            bottomSheetDialog.dismiss()
                        } else
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.todo_not_changed),
                                Toast.LENGTH_LONG
                            ).show()
                    } else
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.add_a_todo),
                            Toast.LENGTH_LONG
                        ).show()
                }
                bottomSheetDialog.setContentView(view)
                bottomSheetDialog.show()
            }

        })
    }

    private fun isFirstOpen() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val isFirstOpen = preferences.getBoolean(PreferenceKeys.FIRST_TIME_OPEN, true)
        if (isFirstOpen) {
            val alertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
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
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_show_all_notes -> navController.navigate(R.id.notesFragment)
            R.id.btn_show_all_courses -> navController.navigate(R.id.coursesFragment)
            R.id.iv_todo_popup_menu -> {
                val menu = PopupMenu(requireContext(), v)
                menu.setOnMenuItemClickListener(this)
                menu.inflate(R.menu.todo_menu)
                menu.show()
            }
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (val id = item?.itemId) {
            R.id.delete_all_todos -> launchDeleteDialog(id)
            R.id.delete_completed_todos -> launchDeleteDialog(id)
        }
        return true
    }

    private fun launchDeleteDialog(id: Int) {
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setTitle(getString(R.string.delete))
        builder.setIcon(R.drawable.ic_baseline_delete_24)
        when (id) {
            R.id.delete_completed_todos -> {
                builder.setMessage(getString(R.string.completed_todo_delete_dialog_message))
                builder.setPositiveButton(getString(R.string.yes)) { dialog1, _ ->
                    viewModel.deleteCompletedTodos()
                    dialog1.dismiss()
                }
            }
            R.id.delete_all_todos -> {
                builder.setMessage(getString(R.string.all_todo_delete_dialog_message))
                builder.setPositiveButton(getString(R.string.yes)) { dialog1, _ ->
                    viewModel.deleteAllTodos()
                    dialog1.dismiss()
                }
            }
        }
        builder.setNegativeButton(getString(R.string.no)) { dialog1, _ -> dialog1.dismiss() }
        val alertDialog = builder.create()
        alertDialog.setOnShowListener {
            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                alertDialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.black
                        )
                    )
                alertDialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.black
                        )
                    )
            } else {
                alertDialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.red
                        )
                    )
                alertDialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(
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