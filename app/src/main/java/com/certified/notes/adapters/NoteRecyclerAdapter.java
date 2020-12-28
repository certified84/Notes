package com.certified.notes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.certified.notes.NotesViewModel;
import com.certified.notes.R;
import com.certified.notes.model.BookMark;
import com.certified.notes.model.Note;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Samson.
 */

public class NoteRecyclerAdapter extends ListAdapter<Note, NoteRecyclerAdapter.ViewHolder> {

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

    private LifecycleOwner mOwner;
    private NotesViewModel mViewModel;
    private OnNoteClickedListener listener;

    public NoteRecyclerAdapter(LifecycleOwner owner, NotesViewModel viewModel) {
        super(DIFF_CALLBACK);
        this.mOwner = owner;
        this.mViewModel = viewModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_notes, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Note currentNote = getItem(position);
        holder.mNoteContent.setText(currentNote.getContent());
        holder.mNoteTitle.setText(currentNote.getTitle());
        holder.mLikeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                int noteId = currentNote.getId();
                String courseCode = currentNote.getCourseCode();
                String noteTitle = currentNote.getTitle();
                String noteContent = currentNote.getContent();

                BookMark bookMark = new BookMark(noteId, courseCode, noteTitle, noteContent);
                mViewModel.insertBookMark(bookMark);
            }

            @Override
            public void unLiked(LikeButton likeButton) {

            }
        });
        holder.checkIfBookMarked(currentNote.getId());
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
        public final LikeButton mLikeButton;

        public ViewHolder(View itemView) {
            super(itemView);
            mNoteContent = itemView.findViewById(R.id.tv_note_content);
            mNoteTitle = itemView.findViewById(R.id.tv_note_title);
            mLikeButton = itemView.findViewById(R.id.likeButton);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onNoteClick(getItem(position));
                }
            });
        }

        private void checkIfBookMarked(int noteId) {
            List<Integer> noteIds = new ArrayList<>();
            mViewModel.getAllNoteIds().observe(mOwner, ids -> {
                noteIds.addAll(ids);
            });
            if (noteIds.contains(noteId)) {
                mLikeButton.setLiked(true);
            }
        }
    }
}








