package com.certified.notes.ui;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.certified.notes.NotesViewModel;
import com.certified.notes.R;
import com.certified.notes.adapters.NoteRecyclerAdapter;
import com.certified.notes.model.BookMark;
import com.certified.notes.model.Course;
import com.certified.notes.model.Note;
import com.certified.notes.util.PreferenceKeys;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.text.TextUtils.isEmpty;

public class NotesFragment extends Fragment implements PopupMenu.OnMenuItemClickListener {

    private static final String TAG = "NotesFragment";

    private RecyclerView recyclerNotes;
    private NavController mNavController;
    private NotesViewModel mViewModel;
    private ImageView ivNotePopupMenu;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private Set<String> mDefValues;
    private Set<String> mNoteIds;

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

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());

        mDefValues = new HashSet<>();
        mDefValues.add("-1");

        mNoteIds = new HashSet<>(mPreferences.getStringSet(PreferenceKeys.NOTE_IDS, mDefValues));

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

        NoteRecyclerAdapter noteRecyclerAdapter = new NoteRecyclerAdapter(getContext(), getViewLifecycleOwner(), mViewModel);
        mViewModel.getAllNotes().observe(getViewLifecycleOwner(), notes -> {
            noteRecyclerAdapter.submitList(notes);
        });
        recyclerNotes.setAdapter(noteRecyclerAdapter);
        recyclerNotes.setLayoutManager(noteLayoutManager);

        noteRecyclerAdapter.setOnNoteClickedListener(note -> {
            LayoutInflater inflater = this.getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_new_note, null);

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
            builder.setBackground(getResources().getDrawable(R.drawable.alert_dialog_bg, null));
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
                            mViewModel.getBookMarkAt(note.getId()).observe(getViewLifecycleOwner(), bookMarks -> {
                                for (BookMark bookMark : bookMarks) {
                                    mViewModel.updateBookMark(bookMark);
                                    Log.d(TAG, "init: " + bookMark.toString());
                                }
                            });
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

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.LEFT) {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
                    builder.setTitle("Delete");
                    builder.setMessage(R.string.note_delete_dialog_message);
                    builder.setPositiveButton(getString(R.string.delete), (dialog1, which) -> {
                        mViewModel.deleteNote(noteRecyclerAdapter.getNoteAt(viewHolder.getAdapterPosition()));
                        mViewModel.deleteBookMarkedNote(noteRecyclerAdapter.getNoteAt(viewHolder.getAdapterPosition()).getId());

                        int noteId = noteRecyclerAdapter.getNoteAt(viewHolder.getAdapterPosition()).getId();

                        mNoteIds.remove(String.valueOf(noteId));

                        mEditor = mPreferences.edit();
                        mEditor.putStringSet(PreferenceKeys.NOTE_IDS, mNoteIds);
                        mEditor.apply();

                        dialog1.dismiss();
                    });
                    builder.setNegativeButton(getString(R.string.cancel), (dialog1, which) -> dialog1.dismiss());
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else if (direction == ItemTouchHelper.RIGHT) {
                    Note note = noteRecyclerAdapter.getNoteAt(viewHolder.getAdapterPosition());
                    int noteId = note.getId();
                    String courseCode = note.getCourseCode();
                    String noteTitle = note.getTitle();
                    String noteContent = note.getContent();
                    BookMark bookMark = new BookMark(noteId, courseCode, noteTitle, noteContent);
                    mViewModel.insertBookMark(bookMark);

                    mNoteIds.add(String.valueOf(noteId));

                    mEditor = mPreferences.edit();
                    mEditor.putStringSet(PreferenceKeys.NOTE_IDS, mNoteIds);
                    mEditor.apply();

                    Toast.makeText(getContext(), "Note bookmarked", Toast.LENGTH_SHORT).show();
                }
            }
        }).attachToRecyclerView(recyclerNotes);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.delete_all_notes) {
            launchDeleteDialog();
        }
        return true;
    }

    private void launchDeleteDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
        builder.setTitle("Delete");
        builder.setMessage(R.string.all_note_delete_dialog_message);
        builder.setPositiveButton(getString(R.string.delete), (dialog1, which) -> {
            mViewModel.deleteAllNotes();
            dialog1.dismiss();
        });
        builder.setNegativeButton(getString(R.string.cancel), (dialog1, which) -> dialog1.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}