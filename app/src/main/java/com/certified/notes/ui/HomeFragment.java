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
import com.certified.notes.model.Course;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static android.text.TextUtils.isEmpty;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private FloatingActionButton fab, fabAddNote, fabAddCourse, fabAddTodo;
    private RecyclerView recyclerCourses, recyclerNotes;
    private MaterialButton tvShowAllNotes, tvShowAllCourses, tvShowAllTodos;
    private View viewBlur;
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

        fab = view.findViewById(R.id.fab);
        fabAddCourse = view.findViewById(R.id.fab_add_course);
        fabAddNote = view.findViewById(R.id.fab_add_note);
        fabAddTodo = view.findViewById(R.id.fab_add_todo);

        recyclerCourses = view.findViewById(R.id.recycler_view_courses);
        recyclerNotes = view.findViewById(R.id.recycler_view_notes);

        tvShowAllNotes = view.findViewById(R.id.btn_show_all_notes);
        tvShowAllCourses = view.findViewById(R.id.btn_show_all_courses);
        tvShowAllTodos = view.findViewById(R.id.btn_show_all_todos);

        viewBlur = view.findViewById(R.id.view);

        fab.setOnClickListener(this);
        fabAddCourse.setOnClickListener(this);
        fabAddNote.setOnClickListener(this);
        fabAddTodo.setOnClickListener(this);

        tvShowAllNotes.setOnClickListener(this);
        tvShowAllCourses.setOnClickListener(this);
        tvShowAllTodos.setOnClickListener(this);

        viewBlur.setOnClickListener(this);
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
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        if (id == R.id.fab) {
            if (viewBlur.getVisibility() == View.VISIBLE) {
                hideViews();
            } else if (viewBlur.getVisibility() == View.GONE) {
                viewBlur.setVisibility(View.VISIBLE);
                fabAddCourse.setVisibility(View.VISIBLE);
                fabAddNote.setVisibility(View.VISIBLE);
                fabAddTodo.setVisibility(View.VISIBLE);
            }
        } else if (id == R.id.fab_add_course) {
            hideViews();
            launchCourseDialog();
        } else if (id == R.id.view) {
            hideViews();
        } else if (id == R.id.btn_show_all_notes) {
            mNavController.navigate(R.id.notesFragment);
        } else if (id == R.id.btn_show_all_courses) {
            mNavController.navigate(R.id.coursesFragment);
        }
    }

    private void hideViews() {
//        getActivity().get
        viewBlur.setVisibility(View.GONE);
        fabAddCourse.setVisibility(View.GONE);
        fabAddNote.setVisibility(View.GONE);
        fabAddTodo.setVisibility(View.GONE);
    }

    private void launchCourseDialog() {
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_new_course, null);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
        AlertDialog alertDialog = builder.create();
        alertDialog.setView(view);

        EditText etCourseCode = view.findViewById(R.id.et_course_code);
        EditText etCourseTitle = view.findViewById(R.id.et_course_title);
        MaterialButton btnSave = view.findViewById(R.id.btn_save);
        MaterialButton btnCancel = view.findViewById(R.id.btn_cancel);
        TextView tvCourseDialogTitle = view.findViewById(R.id.tv_course_dialog_title);

        tvCourseDialogTitle.setText(getString(R.string.add_course));
        btnCancel.setOnClickListener(v -> alertDialog.dismiss());
        btnSave.setOnClickListener(v -> {
            String courseCode = etCourseCode.getText().toString().trim();
            String courseTitle = etCourseTitle.getText().toString().trim();
            if (!isEmpty(courseCode) && !isEmpty(courseTitle)) {
                Course course = new Course(courseCode, courseTitle);
                mViewModel.insertCourse(course);
                alertDialog.dismiss();
            } else
                Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_SHORT).show();
        });

        alertDialog.show();
    }
}