package com.certified.notes.ui;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.certified.notes.R;
import com.certified.notes.adapters.CourseRecyclerAdapter;
import com.certified.notes.adapters.HomeNoteRecyclerAdapter;
import com.certified.notes.model.BookMark;
import com.certified.notes.model.Course;
import com.certified.notes.model.Note;
import com.certified.notes.room.NotesViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.shawnlin.numberpicker.NumberPicker;

import static android.graphics.Color.RED;
import static android.text.TextUtils.isEmpty;

public class CoursesFragment extends Fragment implements PopupMenu.OnMenuItemClickListener {

    private static final int GRID_SPAN_COUNT = 2;
    private RecyclerView recyclerCourses;
    private NavController mNavController;
    private NotesViewModel mViewModel;
    private ImageView ivCoursePopupMenu;
    private SearchView svSearchNotes;

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            svSearchNotes = view.findViewById(R.id.sv_search_database);
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mNavController = Navigation.findNavController(view);
        mViewModel = new NotesViewModel(requireActivity().getApplication());
        ivCoursePopupMenu.setOnClickListener(this::showPopupMenu);

        init();
    }

    private void init() {
        StaggeredGridLayoutManager courseLayoutManager = new StaggeredGridLayoutManager(GRID_SPAN_COUNT, LinearLayoutManager.VERTICAL);

        CourseRecyclerAdapter courseRecyclerAdapter = new CourseRecyclerAdapter();
        mViewModel.getAllCourses().observe(getViewLifecycleOwner(), courseRecyclerAdapter::submitList);
        recyclerCourses.setAdapter(courseRecyclerAdapter);
        recyclerCourses.setLayoutManager(courseLayoutManager);

        courseRecyclerAdapter.setOnCourseClickedListener(course -> {
            CharSequence[] selection = new CharSequence[]{
                    "Edit Course",
                    "Delete Course",
                    "Show related Notes"
            };

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
            builder.setTitle("Options");
//            builder.setBackground(getContext().getResources().getDrawable(R.drawable.alert_dialog_bg));
            builder.setSingleChoiceItems(selection, -1, (dialog, which) -> {
                switch (which) {
                    case 0:
                        launchEditCourseDialog(course);
                        dialog.dismiss();
                        break;
                    case 1:
                        launchDeleteCourseDialog(course);
                        dialog.dismiss();
                        break;
                    case 2:
                        launchRelatedNotesDialog(course);
                        dialog.dismiss();
                        break;
                }
            });
            builder.show();
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            svSearchNotes.isSubmitButtonEnabled();
            svSearchNotes.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    if (query != null) {
                        searchCourses(query, courseRecyclerAdapter);
                    }
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String query) {
                    if (query != null) {
                        searchCourses(query, courseRecyclerAdapter);
                    }
                    return true;
                }
            });
        }
    }

    private void searchCourses(String query, CourseRecyclerAdapter courseRecyclerAdapter) {
        String searchQuery = "%" + query + "%";
        mViewModel.searchCourses(searchQuery).observe(getViewLifecycleOwner(), courseRecyclerAdapter::submitList);
    }

    private void showPopupMenu(View view) {
        PopupMenu menu = new PopupMenu(getContext(), view);
        menu.setOnMenuItemClickListener(this);
        menu.inflate(R.menu.course_menu);
        menu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.delete_all_courses) {
            launchDeleteDialog();
        }
        return true;
    }

    private void launchEditCourseDialog(@NonNull Course course) {
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_new_course, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme);

        MaterialTextView tvCourseDialogTitle = view.findViewById(R.id.tv_course_dialog_title);
        EditText etCourseCode = view.findViewById(R.id.et_course_code);
        EditText etCourseTitle = view.findViewById(R.id.et_course_title);
        NumberPicker numberPickerCourseUnit = view.findViewById(R.id.number_picker_course_unit);
        MaterialButton btnSave = view.findViewById(R.id.btn_save);
        MaterialButton btnCancel = view.findViewById(R.id.btn_cancel);

        numberPickerCourseUnit.setMinValue(1);
        numberPickerCourseUnit.setMaxValue(4);

        tvCourseDialogTitle.setText(getString(R.string.edit_course));
        etCourseCode.setText(course.getCourseCode());
        etCourseTitle.setText(course.getCourseTitle());
        numberPickerCourseUnit.setValue(course.getCourseUnit());

        btnCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());
        btnSave.setText(R.string.update);
        btnSave.setOnClickListener(v -> {
            String courseCode = etCourseCode.getText().toString().trim();
            String courseTitle = etCourseTitle.getText().toString().trim();
            Integer courseUnit = numberPickerCourseUnit.getValue();
            int courseMark = course.getCourseMark();
            String courseGrade = course.getCourseGrade();
            int courseGradePoint = course.getCourseGradePoint();
            if (!isEmpty(courseCode) && !isEmpty(courseTitle)) {
                if (!courseCode.equals(course.getCourseCode()) || !courseTitle.equals(course.getCourseTitle()) || !courseUnit.equals(course.getCourseUnit())) {
                    Course course1 = new Course(courseCode, courseTitle, courseUnit, courseMark, courseGrade, courseGradePoint);
                    course1.setId(course.getId());
                    mViewModel.getNotesAt(course.getCourseCode()).observe(getViewLifecycleOwner(), notes -> {
                        for (Note note : notes) {
                            String noteTitle = note.getTitle();
                            String noteContent = note.getContent();
                            Note note1 = new Note(courseCode, noteTitle, noteContent);
                            note1.setId(note.getId());
                            mViewModel.updateNote(note1);

                            mViewModel.getBookMarkAt(note.getId()).observe(getViewLifecycleOwner(), bookMarks -> {
                                for (BookMark bookMark : bookMarks) {
                                    BookMark bookMark1 = new BookMark(note.getId(), courseCode, noteTitle, noteContent);
                                    bookMark1.setId(bookMark.getId());
                                    mViewModel.updateBookMark(bookMark1);
                                }
                            });
                        }
                    });
                    mViewModel.updateCourse(course1);
                    bottomSheetDialog.dismiss();
                } else
                    Toast.makeText(getContext(), "Course not changed", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_SHORT).show();
        });
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }

    private void launchRelatedNotesDialog(@NonNull Course course) {
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_related_notes, null);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(dialog1 -> {
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(RED);
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(RED);
        });
        alertDialog.setView(view);

        RecyclerView recyclerViewRelatedNotes = view.findViewById(R.id.recycler_view_related_notes);

        LinearLayoutManager noteLayoutManager = new LinearLayoutManager(getContext());
        HomeNoteRecyclerAdapter noteRecyclerAdapter = new HomeNoteRecyclerAdapter(1);
        mViewModel.getNotesAt(course.getCourseCode()).observe(getViewLifecycleOwner(), noteRecyclerAdapter::submitList);
        recyclerViewRelatedNotes.setAdapter(noteRecyclerAdapter);
        recyclerViewRelatedNotes.setLayoutManager(noteLayoutManager);
        noteRecyclerAdapter.setOnNoteClickedListener(() -> mNavController.navigate(R.id.notesFragment));

        alertDialog.show();
    }

    private void launchDeleteDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setTitle("Delete");
        builder.setMessage(R.string.all_course_delete_dialog_message);
        builder.setIcon(R.drawable.ic_baseline_delete_24);
        builder.setPositiveButton(getString(R.string.yes), (dialog1, which) -> {
            mViewModel.deleteAllCourses();
//            mViewModel.deleteAllNotes();
            mViewModel.getDeletableNotes("NIL").observe(getViewLifecycleOwner(), notes -> {
                for (Note note : notes) {
                    mViewModel.deleteNote(note);
                }
            });
            mViewModel.getDeletableBookmarks("NIL").observe(getViewLifecycleOwner(), bookMarks -> {
                for (BookMark bookMark : bookMarks) {
                    mViewModel.deleteBookMark(bookMark);
                }
            });
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

    private void launchDeleteCourseDialog(Course course) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
        builder.setIcon(R.drawable.ic_baseline_delete_24);
        builder.setTitle(R.string.delete);
        builder.setMessage(R.string.course_delete_dialog_message);
        builder.setPositiveButton(R.string.yes, (dialog, which) -> {
            mViewModel.deleteCourse(course);
            mViewModel.getNotesAt(course.getCourseCode()).observe(getViewLifecycleOwner(), notes -> {
                for (Note note : notes) {
                    mViewModel.deleteNote(note);
                    mViewModel.getBookMarkAt(note.getId()).observe(getViewLifecycleOwner(), bookMarks -> {
                        for (BookMark bookMark : bookMarks) {
                            mViewModel.deleteBookMark(bookMark);
                        }
                    });
                }
            });
            dialog.dismiss();
        });
        builder.setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(dialog1 -> {
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(RED);
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(RED);
        });
        alertDialog.show();
    }
}