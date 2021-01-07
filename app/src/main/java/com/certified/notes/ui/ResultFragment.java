package com.certified.notes.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.certified.notes.NotesViewModel;
import com.certified.notes.R;
import com.certified.notes.adapters.ResultRecyclerAdapter;
import com.certified.notes.model.Course;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import static android.text.TextUtils.isEmpty;

public class ResultFragment extends Fragment {

    private static final String TAG = "ResultFragment";

    private RecyclerView recyclerResults;
    private MaterialButton btnCheckGpa;
    private TextView tvTotalLoadUnit, tvGradePointAverage;
    private NotesViewModel mViewModel;
    private LinearLayoutManager mResultLayoutManager;
    private ResultRecyclerAdapter mResultRecyclerAdapter;

    public ResultFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_result, container, false);

        recyclerResults = view.findViewById(R.id.recycler_view_result);
        btnCheckGpa = view.findViewById(R.id.btn_check_gpa);
        tvTotalLoadUnit = view.findViewById(R.id.tv_total_load_unit);
        tvGradePointAverage = view.findViewById(R.id.tv_grade_point_average);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();

        btnCheckGpa.setOnClickListener(v -> {
            mViewModel.getAllCourseCreditPoints().observe(getViewLifecycleOwner(), creditPoints -> {
                int totalLoadUnit = Integer.parseInt(tvTotalLoadUnit.getText().toString().trim());
                float totalCreditPoint = 0;
                for (int creditPoint : creditPoints) {
                    totalCreditPoint += creditPoint;
                }
                float gpa = (float) totalCreditPoint / (float) totalLoadUnit;
                tvGradePointAverage.setText(String.valueOf(gpa));
                Log.d(TAG, "init: totalCreditPoint = " + totalCreditPoint + ", totalLoadUnit = " + totalLoadUnit);
            });
        });
    }

    private void init() {
        mViewModel = new NotesViewModel(getActivity().getApplication());
        mResultLayoutManager = new LinearLayoutManager(getContext());
        mResultRecyclerAdapter = new ResultRecyclerAdapter(mViewModel);

        mViewModel.getAllCourses().observe(getViewLifecycleOwner(), courses -> mResultRecyclerAdapter.submitList(courses));

        recyclerResults.setAdapter(mResultRecyclerAdapter);
        recyclerResults.setLayoutManager(mResultLayoutManager);

        mViewModel.getAllCourseUnits().observe(getViewLifecycleOwner(), courseUnits -> {
            int courseUnit = 0;
            for (int courseUnit1 : courseUnits) {
                courseUnit += courseUnit1;
            }
            tvTotalLoadUnit.setText(String.valueOf(courseUnit));
        });
    }
}