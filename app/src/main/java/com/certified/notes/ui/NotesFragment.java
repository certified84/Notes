package com.certified.notes.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.certified.notes.NotesViewModel;
import com.certified.notes.R;
import com.certified.notes.adapters.NoteRecyclerAdapter;
import com.certified.notes.model.Course;
import com.certified.notes.model.Note;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

import static android.text.TextUtils.isEmpty;

public class NotesFragment extends Fragment implements PopupMenu.OnMenuItemClickListener {

    private RecyclerView recyclerNotes;
    private NavController mNavController;
    private NotesViewModel mViewModel;
    private ImageView ivNotePopupMenu;

    public NotesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notes, container, false);

        recyclerNotes = view.findViewById(R.id.recycler_view_notes);
        ivNotePopupMenu = view.findViewById(R.id.iv_note_popup_menu);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new NotesViewModel(getActivity().getApplication());
        ivNotePopupMenu.setOnClickListener(this::showPopupMenu);

        init();
    }

    private void showPopupMenu(View view) {
        PopupMenu menu = new PopupMenu(getContext(), view);
        menu.setOnMenuItemClickListener(this);
        menu.inflate(R.menu.note_menu);
        menu.show();
    }

    private void init() {
        LinearLayoutManager noteLayoutManager = new LinearLayoutManager(getContext());

        NoteRecyclerAdapter noteRecyclerAdapter = new NoteRecyclerAdapter(getViewLifecycleOwner(), mViewModel);
        mViewModel.getAllNotes().observe(getViewLifecycleOwner(), notes -> {
            noteRecyclerAdapter.submitList(notes);
        });
        recyclerNotes.setAdapter(noteRecyclerAdapter);
        recyclerNotes.setLayoutManager(noteLayoutManager);

        noteRecyclerAdapter.setOnNoteClickedListener(note -> {
            LayoutInflater inflater = this.getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_new_note, null);

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
            builder.setBackground(getContext().getDrawable(R.drawable.alert_dialog_bg));
            AlertDialog alertDialog = builder.create();
            alertDialog.setView(view);

            Spinner spinnerCourses = view.findViewById(R.id.spinner_courses);
            EditText etNoteTitle = view.findViewById(R.id.et_note_title);
            EditText etNoteContent = view.findViewById(R.id.et_note_content);
            MaterialButton btnSave = view.findViewById(R.id.btn_save);
            MaterialButton btnCancel = view.findViewById(R.id.btn_cancel);
            TextView tvNoteDialogTitle = view.findViewById(R.id.tv_note_dialog_title);

            ArrayList<String> courseList = new ArrayList<>();
            ArrayAdapter<String> adapterCourses = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, courseList);

            mViewModel.getAllCourses().observe(getViewLifecycleOwner(), courses -> {
                courseList.add("Select a course");
                for (Course course : courses) {
                    courseList.add(course.getCourseTitle());
                }
                adapterCourses.notifyDataSetChanged();
            });

            adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCourses.setAdapter(adapterCourses);

            tvNoteDialogTitle.setText(getString(R.string.edit_note));
            etNoteTitle.setText(note.getTitle());
            etNoteContent.setText(note.getContent());

            btnCancel.setOnClickListener(v -> alertDialog.dismiss());
            btnSave.setOnClickListener(v -> {
                String courseTitle = spinnerCourses.getSelectedItem().toString();
                String courseCode = mViewModel.getCourseCode(courseTitle);
                String noteTitle = etNoteTitle.getText().toString().trim();
                String noteContent = etNoteContent.getText().toString().trim();
                if (!isEmpty(noteTitle) && !isEmpty(noteContent)) {
                    if (!courseTitle.equals("Select a course")) {
                        if (!courseCode.equals(note.getCourseCode()) || !noteTitle.equals(note.getTitle()) || !noteContent.equals(note.getContent())) {
                            Note note1 = new Note(courseCode, noteTitle, noteContent);
                            note1.setId(note.getId());
                            mViewModel.updateNote(note1);
                            alertDialog.dismiss();
                        } else
                            Toast.makeText(getContext(), "Note not changed", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(getContext(), "Select a course", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_SHORT).show();
            });

            alertDialog.show();
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.delete_all_notes) {
            mViewModel.deleteAllNotes();
        }
        return true;
    }
}