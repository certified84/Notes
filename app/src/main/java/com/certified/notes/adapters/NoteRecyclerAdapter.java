package com.certified.notes.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.certified.notes.room.NotesViewModel;
import com.certified.notes.R;
import com.certified.notes.model.BookMark;
import com.certified.notes.model.Note;
import com.certified.notes.util.PreferenceKeys;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.HashSet;
import java.util.Set;

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

    private static final String TAG = "NoteRecyclerAdapter";
    private Context mContext;
    private LifecycleOwner mOwner;
    private NotesViewModel mViewModel;
    private OnNoteClickedListener listener;
    private SharedPreferences mPreferences;
    private Set<String> mNoteIds;
    private Set<String> mDefValues;

    public NoteRecyclerAdapter(Context context, LifecycleOwner owner, NotesViewModel viewModel) {
        super(DIFF_CALLBACK);
        this.mContext = context;
        this.mOwner = owner;
        this.mViewModel = viewModel;

        mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());

        mDefValues = new HashSet<>();
        mDefValues.add("-1");

        mNoteIds = new HashSet<>(mPreferences.getStringSet(PreferenceKeys.NOTE_IDS, mDefValues));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_notes, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SharedPreferences.Editor editor = mPreferences.edit();

        Note currentNote = getItem(position);
        holder.mNoteContent.setText(currentNote.getContent());
        holder.mNoteTitle.setText(currentNote.getTitle());
//        holder.mNoteTitle.setText(currentNote.getTitle());
        holder.mLikeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                int noteId = currentNote.getId();
                String courseCode = currentNote.getCourseCode();
                String noteTitle = currentNote.getTitle();
                String noteContent = currentNote.getContent();

                BookMark bookMark = new BookMark(noteId, courseCode, noteTitle, noteContent);
                mViewModel.insertBookMark(bookMark);

                mNoteIds.add(String.valueOf(noteId));
                editor.putStringSet(PreferenceKeys.NOTE_IDS, mNoteIds);
                editor.apply();
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                mViewModel.getBookMarkAt(currentNote.getId()).observe(mOwner, bookMarks -> {
                    for (BookMark bookMark : bookMarks) {
                        mViewModel.deleteBookMark(bookMark);
                    }

                    mNoteIds.remove(String.valueOf(currentNote.getId()));
                    editor.putStringSet(PreferenceKeys.NOTE_IDS, mNoteIds);
                    editor.apply();
                });
            }
        });

        holder.checkIfBookMarked(currentNote.getId(), holder.mLikeButton);
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

        private void checkIfBookMarked(int noteId, LikeButton likeButton) {
            Set<String> defValues = new HashSet<>();
            defValues.add("-1");
            Set<String> noteIds = new HashSet<>(mPreferences.getStringSet(PreferenceKeys.NOTE_IDS, defValues));
            if (noteIds.contains(String.valueOf(noteId))) {
                likeButton.setLiked(true);
            }
            Log.d(TAG, "checkIfBookMarked: " + noteIds);
        }
    }
}