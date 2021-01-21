package com.certified.notes.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.certified.notes.room.NotesViewModel;
import com.certified.notes.R;
import com.certified.notes.model.Course;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

import static android.text.TextUtils.isEmpty;

/**
 * Created by Samson.
 */

public class ResultRecyclerAdapter extends ListAdapter<Course, ResultRecyclerAdapter.ViewHolder> {

    private static final DiffUtil.ItemCallback<Course> DIFF_CALLBACK = new DiffUtil.ItemCallback<Course>() {
        @Override
        public boolean areItemsTheSame(@NonNull Course oldItem, @NonNull Course newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Course oldItem, @NonNull Course newItem) {
            return oldItem.getCourseUnit().equals(newItem.getCourseUnit()) &&
                    oldItem.getCourseCode().equals(newItem.getCourseCode());
        }
    };

    private Context mContext;
    private NotesViewModel mViewModel;

    public ResultRecyclerAdapter(NotesViewModel viewModel) {
        super(DIFF_CALLBACK);
        this.mViewModel = viewModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.list_item_result, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Course course = getItem(position);
        holder.mCourseCode.setText(course.getCourseCode());
        holder.mCourseUnit.setText(String.valueOf(course.getCourseUnit()));
        holder.mCourseMark.setText(String.valueOf(course.getCourseMark()));
        holder.mCourseGrade.setText(course.getCourseGrade());

        holder.itemView.setOnClickListener(v -> {
            View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_edit_result, null);

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(mContext);
            builder.setBackground(mContext.getDrawable(R.drawable.alert_dialog_bg));
            builder.setTitle("Enter mark for " + course.getCourseCode());

            AlertDialog alertDialog = builder.create();
            alertDialog.setView(view);

            EditText etCourseMark = view.findViewById(R.id.et_course_mark);
            MaterialButton btnCancel = view.findViewById(R.id.btn_cancel);
            MaterialButton btnSave = view.findViewById(R.id.btn_save);

            List<Integer> validMark = new ArrayList<>();
            for (int i = 0; i <= 100; i++) {
                validMark.add(i);
            }

            etCourseMark.setText(holder.mCourseMark.getText());
            btnCancel.setOnClickListener(v1 -> alertDialog.dismiss());
            btnSave.setText(R.string.update);
            btnSave.setOnClickListener(v2 -> {
                String courseCode = course.getCourseCode();
                String courseTitle = course.getCourseTitle();
                Integer courseUnit = course.getCourseUnit();
                int courseMark = Integer.parseInt(etCourseMark.getText().toString().trim());
                String courseGrade;
                int courseGradePoint;

                if (courseMark >= 70 && courseMark <= 100) {
                    courseGrade = "A";
                    courseGradePoint = 5;
                } else if (courseMark >= 60 && courseMark <= 69) {
                    courseGrade = "B";
                    courseGradePoint = 4;
                } else if (courseMark >= 50 && courseMark <= 59) {
                    courseGrade = "C";
                    courseGradePoint = 3;
                } else if (courseMark >= 45 && courseMark <= 49) {
                    courseGrade = "D";
                    courseGradePoint = 2;
                } else {
                    courseGrade = "F";
                    courseGradePoint = 0;
                }

                if (!isEmpty(etCourseMark.getText().toString().trim())) {
                    if (validMark.contains(courseMark)) {
                        if (courseMark != course.getCourseMark()) {
                            Course course1 = new Course(courseCode, courseTitle, courseUnit, courseMark, courseGrade, courseGradePoint);
                            course1.setId(course.getId());
                            mViewModel.updateCourse(course1);

                            holder.mCourseGrade.setText(courseGrade);
                            holder.mCourseMark.setText(String.valueOf(courseMark));

                            alertDialog.dismiss();
                        } else
                            Toast.makeText(mContext, "Mark not changed", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(mContext, "Please input a valid score", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(mContext, "Please enter a score", Toast.LENGTH_SHORT).show();
            });

            alertDialog.show();
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView mCourseCode, mCourseUnit, mCourseMark, mCourseGrade;

        public ViewHolder(View itemView) {
            super(itemView);
            mCourseCode = itemView.findViewById(R.id.tv_course_code);
            mCourseUnit = itemView.findViewById(R.id.tv_course_unit);
            mCourseMark = itemView.findViewById(R.id.tv_course_mark);
            mCourseGrade = itemView.findViewById(R.id.tv_course_grade);
        }
    }
}