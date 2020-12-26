package com.certified.notes.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.certified.notes.NotesViewModel;
import com.certified.notes.R;
import com.certified.notes.adapters.CourseRecyclerAdapter;
import com.certified.notes.adapters.NoteRecyclerAdapter;
import com.certified.notes.adapters.TodoRecyclerAdapter;
import com.certified.notes.model.Course;
import com.certified.notes.model.Todo;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static android.text.TextUtils.isEmpty;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private RecyclerView recyclerCourses, recyclerNotes, recyclerTodos;
    private MaterialButton tvShowAllNotes, tvShowAllCourses, tvShowAllTodos;
    private NavController mNavController;
    private NotesViewModel mViewModel;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerCourses = view.findViewById(R.id.recycler_view_courses);
        recyclerNotes = view.findViewById(R.id.recycler_view_notes);
        recyclerTodos = view.findViewById(R.id.recycler_view_todos);

        tvShowAllNotes = view.findViewById(R.id.btn_show_all_notes);
        tvShowAllCourses = view.findViewById(R.id.btn_show_all_courses);
        tvShowAllTodos = view.findViewById(R.id.btn_show_all_todos);

        tvShowAllNotes.setOnClickListener(this);
        tvShowAllCourses.setOnClickListener(this);
        tvShowAllTodos.setOnClickListener(this);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mNavController = Navigation.findNavController(view);
        mViewModel = new NotesViewModel(getActivity().getApplication());

        init();
    }

    private void init() {
        LinearLayoutManager noteLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager courseLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager todoLayoutManager = new LinearLayoutManager(getContext());

        NoteRecyclerAdapter noteRecyclerAdapter = new NoteRecyclerAdapter();
        mViewModel.getAllNotes().observe(getViewLifecycleOwner(), notes -> noteRecyclerAdapter.submitList(notes));
        recyclerNotes.setAdapter(noteRecyclerAdapter);
        recyclerNotes.setLayoutManager(noteLayoutManager);
        recyclerNotes.setClipToPadding(false);
        recyclerNotes.setClipChildren(false);

        CourseRecyclerAdapter courseRecyclerAdapter = new CourseRecyclerAdapter();
        mViewModel.getAllCourses().observe(getViewLifecycleOwner(), courses -> courseRecyclerAdapter.submitList(courses));
        recyclerCourses.setLayoutManager(courseLayoutManager);
        recyclerCourses.setAdapter(courseRecyclerAdapter);
        recyclerCourses.setClipToPadding(false);
        recyclerCourses.setClipChildren(false);

        TodoRecyclerAdapter todoRecyclerAdapter = new TodoRecyclerAdapter(getContext(), mViewModel);
        mViewModel.getAllTodos().observe(getViewLifecycleOwner(), todos -> todoRecyclerAdapter.submitList(todos));
        recyclerTodos.setLayoutManager(todoLayoutManager);
        recyclerTodos.setAdapter(todoRecyclerAdapter);
        todoRecyclerAdapter.setOnTodoClickedListener(todo -> {
            String todoContent = todo.getTodo();
            boolean done = true;
            if (!todo.isDone()) {
                done = false;
            }
            Todo todo1 = new Todo(todoContent, done);
            todo1.setId(todo.getId());
            mViewModel.updateTodo(todo1);
        });
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        if (id == R.id.btn_show_all_notes) {
            mNavController.navigate(R.id.notesFragment);
        } else if (id == R.id.btn_show_all_courses) {
            mNavController.navigate(R.id.coursesFragment);
        }
    }
}