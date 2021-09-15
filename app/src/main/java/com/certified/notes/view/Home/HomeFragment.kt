package com.certified.notes.view.Home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.certified.notes.R
import com.certified.notes.adapters.HomeCourseRecyclerAdapter
import com.certified.notes.adapters.HomeNoteRecyclerAdapter
import com.certified.notes.adapters.TodoRecyclerAdapter
import com.certified.notes.databinding.FragmentHomeBinding
import com.certified.notes.model.Todo
import com.certified.notes.util.PreferenceKeys
import com.github.captain_miao.optroundcardview.OptRoundCardView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment(), View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    companion object {
        private const val ID_NOT_SET = 0
    }

    private lateinit var auth: FirebaseAuth
    private var currentUser: FirebaseUser? = null
    private lateinit var binding: FragmentHomeBinding
    private lateinit var navController: NavController
    private lateinit var viewModel: HomeViewModel
    private lateinit var todoRecyclerAdapter: TodoRecyclerAdapter
    private lateinit var noteRecyclerAdapter: HomeNoteRecyclerAdapter
    private lateinit var courseRecyclerAdapter: HomeCourseRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        navController = Navigation.findNavController(view)

        val viewModelFactory = HomeViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)

        activity?.findViewById<OptRoundCardView>(R.id.optRoundCardView2)?.visibility = View.VISIBLE
        activity?.findViewById<FloatingActionButton>(R.id.fab)?.visibility = View.VISIBLE

        isFirstOpen()
        init()

        currentUser = auth.currentUser
        if (currentUser != null)
            loadFromFireBase()
        else
            loadFromRoom()
    }

    private fun loadFromFireBase() {
        TODO("Not yet implemented")
    }

    private fun loadFromRoom() {
        binding.apply {

            viewModel.randomNotes.observe(viewLifecycleOwner) { notes ->
                if (notes != null)
                    noteRecyclerAdapter.submitList(notes)
                else
                    btnShowAllNotes.visibility = View.GONE
            }

            viewModel.randomCourses.observe(viewLifecycleOwner) { courses ->
                if (courses != null)
                    courseRecyclerAdapter.submitList(courses)
                else
                    btnShowAllCourses.visibility = View.GONE
            }

            viewModel.allTodos.observe(viewLifecycleOwner) { todos ->
                if (todos.isNotEmpty()) {
                    cardView.visibility = View.VISIBLE
                    todoRecyclerAdapter.submitList(todos)
                } else
                    cardView.visibility = View.GONE
            }
        }

        todoRecyclerAdapter.setOnTodoClickedListener(object :
            TodoRecyclerAdapter.OnTodoClickedListener {
            override fun onTodoClicked(todo: Todo) {
                val view = layoutInflater.inflate(
                    R.layout.dialog_new_todo,
                    ConstraintLayout(requireContext())
                )

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

    private fun init() {
        binding.apply {

            ivTodoPopupMenu.setOnClickListener(this@HomeFragment)

            val noteLayoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            val courseLayoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            val todoLayoutManager = LinearLayoutManager(requireContext())

            noteRecyclerAdapter = HomeNoteRecyclerAdapter(ID_NOT_SET)
            recyclerViewNotes.adapter = noteRecyclerAdapter
            recyclerViewNotes.layoutManager = noteLayoutManager
            recyclerViewNotes.clipToPadding = false
            recyclerViewNotes.clipChildren = false
            recyclerViewNotes.setOnClickListener { navController.navigate(R.id.notesFragment) }

            courseRecyclerAdapter = HomeCourseRecyclerAdapter()
            recyclerViewCourses.layoutManager = courseLayoutManager
            recyclerViewCourses.adapter = courseRecyclerAdapter
            recyclerViewCourses.clipToPadding = false
            recyclerViewCourses.clipChildren = false
            recyclerViewCourses.setOnClickListener { navController.navigate(R.id.coursesFragment) }

            todoRecyclerAdapter = TodoRecyclerAdapter(requireContext(), viewModel)
            recyclerViewTodos.layoutManager = todoLayoutManager
            recyclerViewTodos.adapter = todoRecyclerAdapter

            if (currentUser == null) {
                viewModel.user.observe(viewLifecycleOwner) { user ->
                    if (user != null) {
                        val profileImageBitmap = user.profileImage
                        val name = user.name.substringAfter(" ")

                        if (name != "Enter")
                            tvHiName.text = ("Hi, $name")
                        else
                            tvHiName.text = ("Hi, there")

                        if (profileImageBitmap != null) {
                            Glide.with(requireContext())
                                .load(profileImageBitmap)
                                .into(profileImage)
                        } else {
                            Glide.with(requireContext())
                                .load(R.drawable.ic_logo)
                                .into(profileImage)
                        }
                    }
                }
            }
        }
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
                launchSignUpDialog()
            }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        } else
            launchSignUpDialog()
    }

    private fun launchSignUpDialog() {
        TODO("Not yet implemented")
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
                    if (currentUser == null)
                        viewModel.deleteCompletedTodos()
//                    else
////                        Delete completed todos from Firestore
                    dialog1.dismiss()
                }
            }
            R.id.delete_all_todos -> {
                builder.setMessage(getString(R.string.all_todo_delete_dialog_message))
                builder.setPositiveButton(getString(R.string.yes)) { dialog1, _ ->
                    if (currentUser == null)
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