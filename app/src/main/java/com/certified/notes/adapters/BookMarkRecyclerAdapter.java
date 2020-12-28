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
import com.certified.notes.model.BookMark;
import com.certified.notes.model.Note;
import com.like.LikeButton;

/**
 * Created by Samson.
 */

public class BookMarkRecyclerAdapter extends ListAdapter<BookMark, BookMarkRecyclerAdapter.ViewHolder> {

    private static final DiffUtil.ItemCallback<BookMark> DIFF_CALLBACK = new DiffUtil.ItemCallback<BookMark>() {
        @Override
        public boolean areItemsTheSame(@NonNull BookMark oldItem, @NonNull BookMark newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull BookMark oldItem, @NonNull BookMark newItem) {
            return oldItem.getNoteId() == newItem.getNoteId() &&
                    oldItem.getCourseCode().equals(newItem.getCourseCode()) &&
                    oldItem.getNoteTitle().equals(newItem.getNoteTitle()) &&
                    oldItem.getNoteContent().equals(newItem.getNoteContent());
        }
    };
    private OnBookMarkClickedListener listener;

    public BookMarkRecyclerAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_notes, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BookMark currentBookMark = getItem(position);
        holder.mNoteContent.setText(currentBookMark.getNoteContent());
        holder.mNoteTitle.setText(currentBookMark.getNoteTitle());
        holder.mLikeButton.setLiked(true);
    }

    public BookMark getBookMarkAt(int position) {
        return getItem(position);
    }

    public void setOnBookMarkClickedListener(OnBookMarkClickedListener listener) {
        this.listener = listener;
    }

    public interface OnBookMarkClickedListener {
        void onBookMarkClick(BookMark bookMark);
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
                    listener.onBookMarkClick(getItem(position));
                }
            });
        }
    }
}








