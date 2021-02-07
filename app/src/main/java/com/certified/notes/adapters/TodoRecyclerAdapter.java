package com.certified.notes.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.certified.notes.R;
import com.certified.notes.model.Todo;
import com.certified.notes.room.NotesViewModel;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import static android.graphics.Color.RED;

public class TodoRecyclerAdapter extends ListAdapter<Todo, TodoRecyclerAdapter.ViewHolder> {

    private static final DiffUtil.ItemCallback<Todo> DIFF_CALLBACK = new DiffUtil.ItemCallback<Todo>() {
        @Override
        public boolean areItemsTheSame(@NonNull Todo oldItem, @NonNull Todo newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Todo oldItem, @NonNull Todo newItem) {
            return oldItem.getTodo().equals(newItem.getTodo()) &&
                    oldItem.isDone() == newItem.isDone();
        }
    };
    private final NotesViewModel mViewModel;
    private final Context mContext;
    private onTodoClickedListener listener;

    public TodoRecyclerAdapter(Context context, NotesViewModel viewModel) {
        super(DIFF_CALLBACK);
        this.mContext = context;
        mViewModel = viewModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_todos, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Todo todo = getItem(position);
        holder.mTodo.setText(todo.getTodo());
        holder.mCheckBox.setChecked(todo.isDone());

        holder.mCheckBox.setOnClickListener(v -> {
            String todoContent = holder.mTodo.getText().toString().trim();
            if (holder.mCheckBox.isChecked()) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(mContext);
                builder.setIcon(R.drawable.ic_baseline_delete_24);
                builder.setTitle(R.string.delete_completed_todo);
                builder.setMessage("This todo has been marked as done. Would you like to delete it ?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    mViewModel.deleteTodo(getItem(position));
                    dialog.cancel();
                });
                builder.setNegativeButton("No", (dialog, which) -> {
                    Todo todo1 = new Todo(todoContent, true);
                    todo1.setId(getItem(position).getId());
                    mViewModel.updateTodo(todo1);
                    dialog.cancel();
                });
                builder.setOnDismissListener(dialog -> holder.mCheckBox.setChecked(false));
                AlertDialog dialog = builder.create();
                dialog.setOnShowListener(dialog1 -> {
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(RED);
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(RED);
                });
                dialog.show();
            } else {
                holder.mCheckBox.setChecked(false);
                Todo todo1 = new Todo(todoContent, false);
                todo1.setId(getItem(position).getId());
                mViewModel.updateTodo(todo1);
            }
        });
    }

    public void setOnTodoClickedListener(onTodoClickedListener listener) {
        this.listener = listener;
    }

    public interface onTodoClickedListener {
        void onTodoClicked(Todo todo);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mTodo;
        private final MaterialCheckBox mCheckBox;

        public ViewHolder(View itemView) {
            super(itemView);
            mTodo = itemView.findViewById(R.id.tv_todo);
            mCheckBox = itemView.findViewById(R.id.checkbox_todo);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onTodoClicked(getItem(position));
                }
            });
        }
    }
}