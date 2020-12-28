package com.certified.notes.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.certified.notes.NotesViewModel;
import com.certified.notes.R;
import com.certified.notes.adapters.CourseRecyclerAdapter;
import com.certified.notes.adapters.NoteRecyclerAdapter;
import com.certified.notes.model.Course;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import static android.text.TextUtils.isEmpty;

public class CoursesFragment extends Fragment implements PopupMenu.OnMenuItemClickListener {

    private static final int GRID_SPAN_COUNT = 2;
    private RecyclerView recyclerCourses;
    private NavController mNavController;
    private NotesViewModel mViewModel;
    private ImageView ivCoursePopupMenu;

    public CoursesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_courses, container, false);

        recyclerCourses = view.findViewById(R.id.recycler_view_courses);
        ivCoursePopupMenu = view.findViewById(R.id.iv_course_popup_menu);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new NotesViewModel(getActivity().getApplication());
        ivCoursePopupMenu.setOnClickListener(this::showPopupMenu);

        init();
    }

    private void showPopupMenu(View view) {
        PopupMenu menu = new PopupMenu(getContext(), view);
        menu.setOnMenuItemClickListener(this);
        menu.inflate(R.menu.course_menu);
        menu.show();
    }

    private void init() {
        StaggeredGridLayoutManager courseLayoutManager = new StaggeredGridLayoutManager(GRID_SPAN_COUNT, LinearLayoutManager.VERTICAL);

        CourseRecyclerAdapter courseRecyclerAdapter = new CourseRecyclerAdapter();
        mViewModel.getAllCourses().observe(getViewLifecycleOwner(), courses -> courseRecyclerAdapter.submitList(courses));
        recyclerCourses.setAdapter(courseRecyclerAdapter);
        recyclerCourses.setLayoutManager(courseLayoutManager);

        courseRecyclerAdapter.setOnCourseClickedListener(course -> {
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

            tvCourseDialogTitle.setText(getString(R.string.edit_course));
            etCourseCode.setText(course.getCourseCode());
            etCourseTitle.setText(course.getCourseTitle());

            btnCancel.setOnClickListener(v -> alertDialog.dismiss());
            btnSave.setOnClickListener(v -> {
                String courseCode = etCourseCode.getText().toString().trim();
                String courseTitle = etCourseTitle.getText().toString().trim();
                if (!isEmpty(courseCode) && !isEmpty(courseTitle)) {
                    if (!courseCode.equals(course.getCourseCode()) || !courseTitle.equals(course.getCourseTitle())) {
                        Course course1 = new Course(courseCode, courseTitle);
                        course1.setId(course.getId());
                        mViewModel.updateCourse(course1);
                        alertDialog.dismiss();
                    } else
                        Toast.makeText(getContext(), "Course not changed", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_SHORT).show();
            });

            alertDialog.show();
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.delete_all_courses) {
            mViewModel.deleteAllCourses();
        }
        return true;
    }
}