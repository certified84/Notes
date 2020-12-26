package com.certified.notes.ui;

import android.os.Bundle;

import com.certified.notes.NotesViewModel;
import com.certified.notes.R;
import com.certified.notes.model.Course;
import com.certified.notes.model.Todo;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import me.ibrahimsn.lib.SmoothBottomBar;

import static android.text.TextUtils.isEmpty;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

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

    public void launchBlurDialog() {
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.blur_dialog, null);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        AlertDialog alertDialog = builder.create();
        alertDialog.setView(view);

        View blurView = view.findViewById(R.id.view_blur);

        if (viewBlur.getVisibility() == View.VISIBLE) {
            alertDialog.show();
        } else {
            alertDialog.dismiss();
        }
    }
}