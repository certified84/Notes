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
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.certified.notes.R;
import com.certified.notes.adapters.HomeCourseRecyclerAdapter;
import com.certified.notes.adapters.HomeNoteRecyclerAdapter;
import com.certified.notes.adapters.TodoRecyclerAdapter;
import com.certified.notes.model.Todo;
import com.certified.notes.room.NotesViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

import static android.graphics.Color.RED;
import static android.text.TextUtils.isEmpty;

public class HomeFragment extends Fragment implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private final int ID_NOT_SET = 0;
    private RecyclerView recyclerCourses, recyclerNotes, recyclerTodos;
    private MaterialButton tvShowAllNotes, tvShowAllCourses;
    private ImageView ivTodoPopupMenu;
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

        ivTodoPopupMenu = view.findViewById(R.id.iv_todo_popup_menu);

        tvShowAllNotes.setOnClickListener(this);
        tvShowAllCourses.setOnClickListener(this);
        ivTodoPopupMenu.setOnClickListener(this);

//        enableStrictMode();

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

        HomeNoteRecyclerAdapter noteRecyclerAdapter = new HomeNoteRecyclerAdapter(ID_NOT_SET);
        mViewModel.getRandomNotes().observe(getViewLifecycleOwner(), noteRecyclerAdapter::submitList);
        recyclerNotes.setAdapter(noteRecyclerAdapter);
        recyclerNotes.setLayoutManager(noteLayoutManager);
        recyclerNotes.setClipToPadding(false);
        recyclerNotes.setClipChildren(false);

        HomeCourseRecyclerAdapter courseRecyclerAdapter = new HomeCourseRecyclerAdapter();
        mViewModel.getRandomCourses().observe(getViewLifecycleOwner(), courseRecyclerAdapter::submitList);
        recyclerCourses.setLayoutManager(courseLayoutManager);
        recyclerCourses.setAdapter(courseRecyclerAdapter);
        recyclerCourses.setClipToPadding(false);
        recyclerCourses.setClipChildren(false);

        TodoRecyclerAdapter todoRecyclerAdapter = new TodoRecyclerAdapter(getContext(), mViewModel);
        mViewModel.getAllTodos().observe(getViewLifecycleOwner(), todoRecyclerAdapter::submitList);
        recyclerTodos.setLayoutManager(todoLayoutManager);
        recyclerTodos.setAdapter(todoRecyclerAdapter);
        todoRecyclerAdapter.setOnTodoClickedListener(todo -> {
            LayoutInflater inflater = this.getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_new_todo, null);

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder.setBackground(getContext().getDrawable(R.drawable.alert_dialog_bg));
            }
            AlertDialog alertDialog = builder.create();
            alertDialog.setView(view);

            MaterialTextView tvTodoDialogTitle = view.findViewById(R.id.tv_todo_dialog_title);
            EditText etTodo = view.findViewById(R.id.et_todo);
            MaterialButton btnSave = view.findViewById(R.id.btn_save);
            MaterialButton btnCancel = view.findViewById(R.id.btn_cancel);

            etTodo.setText(todo.getTodo());

            tvTodoDialogTitle.setText(getString(R.string.edit_todo));
            btnCancel.setOnClickListener(v -> alertDialog.dismiss());
            btnSave.setText(R.string.update);
            btnSave.setOnClickListener(v -> {
                String todoContent = etTodo.getText().toString().trim();
                boolean done = todo.isDone();
                if (!isEmpty(todoContent)) {
                    if (!todoContent.equals(todo.getTodo())) {
                        Todo todo1 = new Todo(todoContent, done);
                        todo1.setId(todo.getId());
                        mViewModel.updateTodo(todo1);
                        alertDialog.dismiss();
                    } else
                        Toast.makeText(getContext(), "Todo not changed", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getContext(), "Add a todo", Toast.LENGTH_SHORT).show();
            });
            alertDialog.show();
        });
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        if (id == R.id.btn_show_all_notes) {
            mNavController.navigate(R.id.notesFragment);
        } else if (id == R.id.btn_show_all_courses) {
            mNavController.navigate(R.id.coursesFragment);
        } else if (id == R.id.iv_todo_popup_menu) {
            PopupMenu menu = new PopupMenu(getContext(), v);
            menu.setOnMenuItemClickListener(this);
            menu.inflate(R.menu.todo_menu);
            menu.show();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.delete_all_todos) {
            launchDeleteDialog(id);
        } else if (id == R.id.delete_completed_todos) {
            launchDeleteDialog(id);
        }
        return true;
    }

    private void launchDeleteDialog(int id) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
        builder.setTitle(R.string.delete);
        builder.setIcon(R.drawable.ic_baseline_delete_24);
        if (id == R.id.delete_completed_todos) {
            builder.setMessage(R.string.completed_todo_delete_dialog_message);
            builder.setPositiveButton(getString(R.string.yes), (dialog1, which) -> {
                mViewModel.deleteCompletedTodos();
                dialog1.dismiss();
            });
        } else if (id == R.id.delete_all_todos) {
            builder.setMessage(getString(R.string.all_todo_delete_dialog_message));
            builder.setPositiveButton(getString(R.string.yes), (dialog1, which) -> {
                mViewModel.deleteAllTodos();
                dialog1.dismiss();
            });
        }
        builder.setNegativeButton(getString(R.string.no), (dialog1, which) -> dialog1.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(dialog1 -> {
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(RED);
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(RED);
        });
        alertDialog.show();
    }
}