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
import com.certified.notes.model.Course;
import com.google.android.material.snackbar.Snackbar;


/**
 * Created by Samson.
 */

public class CourseRecyclerAdapter extends ListAdapter<Course, CourseRecyclerAdapter.ViewHolder> {

    private onCourseClickedListener listener;

    private static final DiffUtil.ItemCallback<Course> DIFF_CALLBACK = new DiffUtil.ItemCallback<Course>() {
        @Override
        public boolean areItemsTheSame(@NonNull Course oldItem, @NonNull Course newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Course oldItem, @NonNull Course newItem) {
            return oldItem.getCourseTitle().equals(newItem.getCourseTitle()) &&
                    oldItem.getCourseCode().equals(newItem.getCourseCode());
        }
    };

    public CourseRecyclerAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_courses, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Course course = getItem(position);
        holder.mCourseTitle.setText(course.getCourseTitle());
        holder.mCourseCode.setText(course.getCourseCode());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView mCourseCode, mCourseTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            mCourseCode = itemView.findViewById(R.id.tv_course_code);
            mCourseTitle = itemView.findViewById(R.id.tv_course_title);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onCourseClicked(getItem(position));
                }
            });
        }
    }

    public interface onCourseClickedListener {
        void onCourseClicked(Course course);
    }

    public void setOnCourseClickedListener(onCourseClickedListener listener) {
        this.listener = listener;
    }
}