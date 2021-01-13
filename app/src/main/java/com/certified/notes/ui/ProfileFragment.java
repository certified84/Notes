package com.certified.notes.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;

import com.certified.notes.NotesViewModel;
import com.certified.notes.R;
import com.certified.notes.model.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.text.TextUtils.isEmpty;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private NotesViewModel mViewModel;

    private Group groupName, groupSchool, groupDepartment, groupLevel;
    private TextView tvName, tvSchool, tvDepartment, tvLevel;
    private CircleImageView profileImage;
    private ImageView btnChangeProfilePicture;
    private MaterialAlertDialogBuilder mBuilder;
    private AlertDialog mAlertDialog;

    String userName, userSchool, userDepartment, userLevel;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mViewModel = new NotesViewModel(getActivity().getApplication());

        groupName = view.findViewById(R.id.group_edit_name);
        groupSchool = view.findViewById(R.id.group_edit_school);
        groupDepartment = view.findViewById(R.id.group_edit_department);
        groupLevel = view.findViewById(R.id.group_edit_level);

        tvName = view.findViewById(R.id.tv_name);
        tvSchool = view.findViewById(R.id.tv_school);
        tvDepartment = view.findViewById(R.id.tv_department);
        tvLevel = view.findViewById(R.id.tv_level);

        profileImage = view.findViewById(R.id.profile_image);
        btnChangeProfilePicture = view.findViewById(R.id.iv_change_profile_picture);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        groupName.setOnClickListener(this);
        groupSchool.setOnClickListener(this);
        groupDepartment.setOnClickListener(this);
        groupLevel.setOnClickListener(this);

        mViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                userName = user.getName();
                userSchool = user.getSchool();
                userDepartment = user.getDepartment();
                userLevel = user.getLevel();

                tvName.setText(userName);
                tvSchool.setText(userSchool);
                tvDepartment.setText(userDepartment);
                tvLevel.setText(userLevel);
            }
        });

        mBuilder = new MaterialAlertDialogBuilder(getContext());
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.group_edit_name) {
            launchNameDialog();
        } else if (id == R.id.group_edit_school) {
            launchSchoolDialog();
        } else if (id == R.id.group_edit_department) {
            launchDepartmentDialog();
        } else if (id == R.id.group_edit_level) {
            launchLevelDialog();
        }
    }

    private void launchNameDialog() {
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_profile, null);

        mBuilder.setBackground(getContext().getDrawable(R.drawable.alert_dialog_bg));
        mBuilder.setTitle(getString(R.string.enter_name));
        mAlertDialog = mBuilder.create();
        mAlertDialog.setView(view);

        TextInputLayout inputLayout = view.findViewById(R.id.et_edit_profile_layout);
        TextInputEditText inputEditText = view.findViewById(R.id.et_edit_profile);
        MaterialButton btnCancel = view.findViewById(R.id.btn_cancel);
        MaterialButton btnSave = view.findViewById(R.id.btn_save);

        inputLayout.setHint(getString(R.string.name));
        inputEditText.setText(userName);

        btnCancel.setOnClickListener(v -> mAlertDialog.dismiss());
        btnSave.setOnClickListener(v -> {
            String name = inputEditText.getText().toString().trim();
            String school = tvSchool.getText().toString().trim();
            String department = tvDepartment.getText().toString().trim();
            String level = tvLevel.getText().toString().trim();

            if (!isEmpty(name)) {
                if (!name.equals(userName)) {

                    User user = new User(name, school, department, level);
                    user.setId(0);

                    mViewModel.updateUser(user);
                    tvName.setText(name);

                    mAlertDialog.dismiss();
                } else
                    Toast.makeText(getContext(), "Name not changed", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(getContext(), "Please Enter a name", Toast.LENGTH_SHORT).show();
        });
        mAlertDialog.show();
    }

    private void launchSchoolDialog() {
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_profile, null);

        mBuilder.setBackground(getContext().getDrawable(R.drawable.alert_dialog_bg));
        mBuilder.setTitle(getString(R.string.enter_school));
        mAlertDialog = mBuilder.create();
        mAlertDialog.setView(view);

        TextInputLayout inputLayout = view.findViewById(R.id.et_edit_profile_layout);
        TextInputEditText inputEditText = view.findViewById(R.id.et_edit_profile);
        MaterialButton btnCancel = view.findViewById(R.id.btn_cancel);
        MaterialButton btnSave = view.findViewById(R.id.btn_save);

        inputLayout.setHint(getString(R.string.school));
        inputEditText.setText(userSchool);

        btnCancel.setOnClickListener(v -> mAlertDialog.dismiss());
        btnSave.setOnClickListener(v -> {
            String name = tvName.getText().toString().trim();
            String school = inputEditText.getText().toString().trim();
            String department = tvDepartment.getText().toString().trim();
            String level = tvLevel.getText().toString().trim();

            if (!isEmpty(school)) {
                if (!school.equals(userSchool)) {
                    User user = new User(name, school, department, level);
                    user.setId(0);

                    mViewModel.updateUser(user);
                    tvSchool.setText(school);

                    mAlertDialog.dismiss();
                } else
                    Toast.makeText(getContext(), "School not changed", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(getContext(), "Please Enter a school", Toast.LENGTH_SHORT).show();
        });
        mAlertDialog.show();
    }

    private void launchDepartmentDialog() {
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_profile, null);

        mBuilder.setBackground(getContext().getDrawable(R.drawable.alert_dialog_bg));
        mBuilder.setTitle(getString(R.string.enter_department));
        mAlertDialog = mBuilder.create();
        mAlertDialog.setView(view);

        TextInputLayout inputLayout = view.findViewById(R.id.et_edit_profile_layout);
        TextInputEditText inputEditText = view.findViewById(R.id.et_edit_profile);
        MaterialButton btnCancel = view.findViewById(R.id.btn_cancel);
        MaterialButton btnSave = view.findViewById(R.id.btn_save);

        inputLayout.setHint(getString(R.string.department));
        inputEditText.setText(userDepartment);

        btnCancel.setOnClickListener(v -> mAlertDialog.dismiss());
        btnSave.setOnClickListener(v -> {
            String name = tvName.getText().toString().trim();
            String school = tvSchool.getText().toString().trim();
            String department = inputEditText.getText().toString().trim();
            String level = tvLevel.getText().toString().trim();

            if (!isEmpty(department)) {
                if (!department.equals(userDepartment)) {
                    User user = new User(name, school, department, level);
                    user.setId(0);

                    mViewModel.updateUser(user);
                    tvDepartment.setText(department);
                    mAlertDialog.dismiss();
                } else
                    Toast.makeText(getContext(), "Department not changed", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(getContext(), "Please Enter a department", Toast.LENGTH_SHORT).show();
        });
        mAlertDialog.show();
    }

    private void launchLevelDialog() {
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_level, null);

        mBuilder.setBackground(getContext().getDrawable(R.drawable.alert_dialog_bg));
        mBuilder.setTitle(getString(R.string.select_level));
        mAlertDialog = mBuilder.create();
        mAlertDialog.setView(view);

        Spinner spinnerLevel = view.findViewById(R.id.spinner_level);
        MaterialButton btnCancel = view.findViewById(R.id.btn_cancel);
        MaterialButton btnSave = view.findViewById(R.id.btn_save);

        String[] levels = {getString(R.string.select_level), "100L", "200L", "300L", "400L", "500L"};
        ArrayAdapter<String> adapterLevels = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, levels);

        adapterLevels.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLevel.setAdapter(adapterLevels);

        int selection = adapterLevels.getPosition(userLevel);
        spinnerLevel.setSelection(selection);

        btnCancel.setOnClickListener(v -> mAlertDialog.dismiss());
        btnSave.setOnClickListener(v -> {
            String name = tvName.getText().toString().trim();
            String school = tvSchool.getText().toString().trim();
            String department = tvDepartment.getText().toString().trim();
            String level = spinnerLevel.getSelectedItem().toString();

            if (!level.equals(getString(R.string.select_level))) {
                if (!level.equals(userLevel)) {
                    User user = new User(name, school, department, level);
                    user.setId(0);

                    mViewModel.updateUser(user);
                    tvLevel.setText(level);

                    mAlertDialog.dismiss();
                } else
                    Toast.makeText(getContext(), "Level not changed", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(getContext(), "Please select a level", Toast.LENGTH_SHORT).show();
        });
        mAlertDialog.show();
    }
}