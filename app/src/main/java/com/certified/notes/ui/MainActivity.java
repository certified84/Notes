package com.certified.notes.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.certified.notes.NotesViewModel;
import com.certified.notes.R;
import com.certified.notes.model.Course;
import com.certified.notes.model.Note;
import com.certified.notes.model.Todo;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

import me.ibrahimsn.lib.SmoothBottomBar;

import static android.text.TextUtils.isEmpty;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private FloatingActionButton fab, fabAddNote, fabAddCourse, fabAddTodo;
    private View viewBlur;
    private NavController mNavController;
    private SmoothBottomBar mSmoothBottomBar;

    private NotesViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        mViewModel = new NotesViewModel(getApplication());

        mNavController = Navigation.findNavController(this, R.id.fragment);
        mSmoothBottomBar = findViewById(R.id.smoothBottomBar);

        fab = findViewById(R.id.fab);
        fabAddCourse = findViewById(R.id.fab_add_course);
        fabAddNote = findViewById(R.id.fab_add_note);
        fabAddTodo = findViewById(R.id.fab_add_todo);

        viewBlur = findViewById(R.id.view);

        fab.setOnClickListener(this);
        fabAddCourse.setOnClickListener(this);
        fabAddNote.setOnClickListener(this);
        fabAddTodo.setOnClickListener(this);

        viewBlur.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mSmoothBottomBar.setupWithNavController(menu, mNavController);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
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
        } else if (id == R.id.fab_add_todo) {
            hideViews();
            launchTodoDialog();
        } else if (id == R.id.fab_add_note) {
          hideViews();
          launchNoteDialog();
        } else if (id == R.id.view) {
            hideViews();
        }
    }

    private void hideViews() {
        viewBlur.setVisibility(View.GONE);
        fabAddCourse.setVisibility(View.GONE);
        fabAddNote.setVisibility(View.GONE);
        fabAddTodo.setVisibility(View.GONE);
    }

    private void launchCourseDialog() {
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_new_course, null);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setBackground(getDrawable(R.drawable.alert_dialog_bg));
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
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
        });

        alertDialog.show();
    }

    private void launchTodoDialog() {
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_new_todo, null);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setBackground(getDrawable(R.drawable.alert_dialog_bg));
        AlertDialog alertDialog = builder.create();
        alertDialog.setView(view);

        EditText etTodo = view.findViewById(R.id.et_todo);
        MaterialButton btnSave = view.findViewById(R.id.btn_save);
        MaterialButton btnCancel = view.findViewById(R.id.btn_cancel);

        btnCancel.setOnClickListener(v -> alertDialog.dismiss());
        btnSave.setOnClickListener(v -> {
            String todoContent = etTodo.getText().toString().trim();
            if (!isEmpty(todoContent)) {
                Todo todo = new Todo(todoContent, false);
                mViewModel.insertTodo(todo);
                alertDialog.dismiss();
            } else
                Toast.makeText(this, "Add a todo", Toast.LENGTH_SHORT).show();
        });
        alertDialog.show();
    }

    private void launchNoteDialog() {
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_new_note, null);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setBackground(getDrawable(R.drawable.alert_dialog_bg));
        AlertDialog alertDialog = builder.create();
        alertDialog.setView(view);

        Spinner spinnerCourses = view.findViewById(R.id.spinner_courses);
        EditText etNoteTitle = view.findViewById(R.id.et_note_title);
        EditText etNoteContent = view.findViewById(R.id.et_note_content);
        MaterialButton btnSave = view.findViewById(R.id.btn_save);
        MaterialButton btnCancel = view.findViewById(R.id.btn_cancel);
        TextView tvNoteDialogTitle = view.findViewById(R.id.tv_note_dialog_title);

        ArrayList<String> courseList = new ArrayList<>();
        ArrayAdapter<String> adapterCourses = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courseList);

        mViewModel.getAllCourses().observe(this, courses -> {
            courseList.add("Select a course");
            for (Course course : courses) {
                courseList.add(course.getCourseTitle());
            }
            adapterCourses.notifyDataSetChanged();
        });

        adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourses.setAdapter(adapterCourses);

        tvNoteDialogTitle.setText(getString(R.string.add_note));
        btnCancel.setOnClickListener(v -> alertDialog.dismiss());
        btnSave.setOnClickListener(v -> {
            String courseTitle = spinnerCourses.getSelectedItem().toString();
            String courseCode = mViewModel.getCourseCode(courseTitle);
            String noteTitle = etNoteTitle.getText().toString().trim();
            String noteContent = etNoteContent.getText().toString().trim();
            if (!isEmpty(noteTitle) && !isEmpty(noteContent)) {
                if (!courseTitle.equals("Select a course")) {
                    Note note = new Note(courseCode, noteTitle, noteContent);
                    mViewModel.insertNote(note);
                    alertDialog.dismiss();
                } else
                    Toast.makeText(this, "Select a course", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
        });

        alertDialog.show();
    }
}