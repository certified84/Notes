package com.certified.notes.ui;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
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
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.certified.notes.R;
import com.certified.notes.adapters.BookMarkRecyclerAdapter;
import com.certified.notes.model.BookMark;
import com.certified.notes.model.Course;
import com.certified.notes.model.Note;
import com.certified.notes.room.NotesViewModel;
import com.certified.notes.util.PreferenceKeys;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

import static android.graphics.Color.RED;
import static android.text.TextUtils.isEmpty;

public class BookMarksFragment extends Fragment implements PopupMenu.OnMenuItemClickListener {

    private RecyclerView recyclerBookMarks;
    private NotesViewModel mViewModel;
    private ImageView ivBookMarkPopupMenu;
    private SearchView svSearchBookmarks;

    public BookMarksFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_book_marks, container, false);

        recyclerBookMarks = view.findViewById(R.id.recycler_view_notes);
        ivBookMarkPopupMenu = view.findViewById(R.id.iv_bookmark_popup_menu);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            svSearchBookmarks = view.findViewById(R.id.sv_search_database);
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new NotesViewModel(getActivity().getApplication());
        ivBookMarkPopupMenu.setOnClickListener(this::showPopupMenu);

        init();
    }

    private void init() {
        LinearLayoutManager noteLayoutManager = new LinearLayoutManager(getContext());

        BookMarkRecyclerAdapter bookMarkRecyclerAdapter = new BookMarkRecyclerAdapter(getContext(), mViewModel);
        mViewModel.getAllBookMarks().observe(getViewLifecycleOwner(), bookMarkRecyclerAdapter::submitList);
        recyclerBookMarks.setAdapter(bookMarkRecyclerAdapter);
        recyclerBookMarks.setLayoutManager(noteLayoutManager);

        ArrayList<String> courseList = new ArrayList<>();
        ArrayAdapter<String> adapterCourses = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, courseList);

        courseList.add(getString(R.string.select_a_course));
        courseList.add(getString(R.string.no_course));

        mViewModel.getAllCourses().observe(getViewLifecycleOwner(), courses -> {
            for (Course course : courses) {
                courseList.add(course.getCourseTitle());
            }
            adapterCourses.notifyDataSetChanged();
        });

        bookMarkRecyclerAdapter.setOnBookMarkClickedListener(bookMark -> {
            LayoutInflater inflater = this.getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_new_note, null);

            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme);

            Spinner spinnerCourses = view.findViewById(R.id.spinner_courses);
            EditText etNoteTitle = view.findViewById(R.id.et_note_title);
            EditText etNoteContent = view.findViewById(R.id.et_note_content);
            MaterialButton btnSave = view.findViewById(R.id.btn_save);
            MaterialButton btnCancel = view.findViewById(R.id.btn_cancel);
            TextView tvNoteDialogTitle = view.findViewById(R.id.tv_note_dialog_title);

            adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCourses.setAdapter(adapterCourses);

            tvNoteDialogTitle.setText(getString(R.string.edit_note));
            etNoteTitle.setText(bookMark.getNoteTitle());
            etNoteContent.setText(bookMark.getNoteContent());
            int coursePosition;
            if (!bookMark.getCourseCode().equals("NIL")) {
                coursePosition = adapterCourses.getPosition(mViewModel.getCourseTitle(bookMark.getCourseCode()));
            } else {
                coursePosition = 1;
            }

            spinnerCourses.setSelection(coursePosition);

            btnCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());
            btnSave.setText(R.string.update);
            btnSave.setOnClickListener(v -> {
                int noteId = bookMark.getNoteId();
                String courseTitle = spinnerCourses.getSelectedItem().toString();
                String courseCode;
                if (courseTitle.equals(getString(R.string.no_course)))
                    courseCode = "NIL";
                else
                    courseCode = mViewModel.getCourseCode(courseTitle);
                String noteTitle = etNoteTitle.getText().toString().trim();
                String noteContent = etNoteContent.getText().toString().trim();
                if (!isEmpty(noteTitle) && !isEmpty(noteContent)) {
                    if (!courseTitle.equals("Select a course")) {
                        if (!courseCode.equals(bookMark.getCourseCode()) || !noteTitle.equals(bookMark.getNoteTitle()) || !noteContent.equals(bookMark.getNoteContent())) {
                            if (!courseTitle.equals(getString(R.string.no_course))) {
                                Note note1 = new Note(courseCode, noteTitle, noteContent);
                                note1.setId(bookMark.getNoteId());
                                mViewModel.updateNote(note1);
                                mViewModel.getBookMarkAt(bookMark.getNoteId()).observe(getViewLifecycleOwner(), bookMark1 -> {
                                    if (bookMark1 != null) {
                                        BookMark bookMark2 = new BookMark(noteId, courseCode, noteTitle, noteContent);
                                        bookMark2.setId(bookMark1.getId());
                                        mViewModel.updateBookMark(bookMark2);
                                    }
                                });
                            } else {
                                Note note1 = new Note("NIL", noteTitle, noteContent);
                                note1.setId(bookMark.getNoteId());
                                mViewModel.updateNote(note1);
                                mViewModel.getBookMarkAt(bookMark.getNoteId()).observe(getViewLifecycleOwner(), bookMark1 -> {
                                    if (bookMark1 != null) {
                                        BookMark bookMark2 = new BookMark(noteId, "NIL", noteTitle, noteContent);
                                        bookMark2.setId(bookMark1.getId());
                                        mViewModel.updateBookMark(bookMark2);
                                    }
                                });
                            }
                            bottomSheetDialog.dismiss();
                        } else
                            Toast.makeText(getContext(), "Note not changed", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(getContext(), "Select a course", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_SHORT).show();
            });
            bottomSheetDialog.setContentView(view);
            bottomSheetDialog.show();
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                BookMark bookMark = bookMarkRecyclerAdapter.getBookMarkAt(viewHolder.getAdapterPosition());
                mViewModel.deleteBookMark(bookMark);

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireActivity().getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();

                Set<String> defValues = new HashSet<>();
                defValues.add("-1");
                Set<String> noteIds = new HashSet<>(preferences.getStringSet(PreferenceKeys.NOTE_IDS, defValues));
                noteIds.remove(String.valueOf(bookMark.getNoteId()));

                editor.putStringSet(PreferenceKeys.NOTE_IDS, noteIds);
                editor.apply();
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_50)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.red))
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(recyclerBookMarks);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            svSearchBookmarks.isSubmitButtonEnabled();
            svSearchBookmarks.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    if (query != null) {
                        searchBookmarks(query, bookMarkRecyclerAdapter);
                    }
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String query) {
                    if (query != null) {
                        searchBookmarks(query, bookMarkRecyclerAdapter);
                    }
                    return true;
                }
            });
        }
    }

    private void searchBookmarks(String query, BookMarkRecyclerAdapter bookMarkRecyclerAdapter) {
        String searchQuery = "%" + query + "%";
        mViewModel.searchBookmarks(searchQuery).observe(getViewLifecycleOwner(), bookMarkRecyclerAdapter::submitList);
    }

    private void showPopupMenu(View view) {
        PopupMenu menu = new PopupMenu(getContext(), view);
        menu.setOnMenuItemClickListener(this);
        menu.inflate(R.menu.bookmark_menu);
        menu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.delete_all_bookmarks) {
            launchDeleteDialog();
        }
        return true;
    }

    private void launchDeleteDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setTitle("Delete");
        builder.setMessage(R.string.all_bookmark_delete_dialog_message);
        builder.setIcon(R.drawable.ic_baseline_delete_24);
        builder.setPositiveButton(getString(R.string.yes), (dialog1, which) -> {
            mViewModel.deleteAllBookMarks();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext().getApplicationContext());
            SharedPreferences.Editor editor = preferences.edit();

            Set<String> defValues = new HashSet<>();
            defValues.add("-1");

            Set<String> noteIds = new HashSet<>(preferences.getStringSet(PreferenceKeys.NOTE_IDS, defValues));
            noteIds.removeAll(preferences.getStringSet(PreferenceKeys.NOTE_IDS, defValues));

            editor.putStringSet(PreferenceKeys.NOTE_IDS, noteIds);
            editor.apply();

            dialog1.dismiss();
        });
        builder.setNegativeButton(getString(R.string.no), (dialog1, which) -> dialog1.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(dialog1 -> {
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(RED);
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(RED);
        });
        alertDialog.show();
    }
}