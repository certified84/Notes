package com.certified.notes.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.certified.notes.R;
import com.certified.notes.model.Note;

/**
 * Created by Samson.
 */

public class HomeNoteRecyclerAdapter extends ListAdapter<Note, HomeNoteRecyclerAdapter.ViewHolder> {

    private static final DiffUtil.ItemCallback<Note> DIFF_CALLBACK = new DiffUtil.ItemCallback<Note>() {
        @Override
        public boolean areItemsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getContent().equals(newItem.getContent()) &&
                    oldItem.getCourseCode().equals(newItem.getCourseCode());
        }
    };

    private OnNoteClickedListener listener;
    private final int ID_NOT_SET = 0;
    private int id;

    public HomeNoteRecyclerAdapter(int id) {
        super(DIFF_CALLBACK);
        this.id = id;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        if (id == ID_NOT_SET) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_notes_home, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_related_notes, parent, false);
        }
        return new ViewHolder(itemView);
//        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_notes_home, parent, false);
//        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Note currentNote = getItem(position);
        holder.mNoteContent.setText(currentNote.getContent());
        holder.mNoteTitle.setText(currentNote.getTitle());
    }

    public Note getNoteAt(int position) {
        return getItem(position);
    }

    public void setOnNoteClickedListener(OnNoteClickedListener listener) {
        this.listener = listener;
    }

    public interface OnNoteClickedListener {
        void onNoteClick(Note note);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView mNoteContent;
        public final TextView mNoteTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            mNoteContent = itemView.findViewById(R.id.tv_note_content);
            mNoteTitle = itemView.findViewById(R.id.tv_note_title);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onNoteClick(getItem(position));
                }
            });
        }
    }
}








