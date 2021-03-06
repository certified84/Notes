package com.certified.notes.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.certified.notes.R;
import com.certified.notes.model.User;
import com.certified.notes.room.NotesViewModel;
import com.certified.notes.util.PreferenceKeys;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static android.text.TextUtils.isEmpty;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private static final int USER_ID = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private static final int PICK_IMAGE_CODE = 102;
    String userName, userSchool, userDepartment, userLevel;
    Bitmap profileImageBitmap;
    private NotesViewModel mViewModel;
    private NavController mNavController;
    private Group groupName, groupSchool, groupDepartment, groupLevel;
    private TextView tvName, tvSchool, tvDepartment, tvLevel;
    private CircleImageView profileImage;
    private FloatingActionButton fabChangeProfilePicture, fabSettings;
    private SwitchMaterial switchDarkMode;
//    private static final String currentPhotoPath;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        groupName = view.findViewById(R.id.group_edit_name);
        groupSchool = view.findViewById(R.id.group_edit_school);
        groupDepartment = view.findViewById(R.id.group_edit_department);
        groupLevel = view.findViewById(R.id.group_edit_level);

        tvName = view.findViewById(R.id.tv_name);
        tvSchool = view.findViewById(R.id.tv_school);
        tvDepartment = view.findViewById(R.id.tv_department);
        tvLevel = view.findViewById(R.id.tv_level);

        profileImage = view.findViewById(R.id.profile_image);
        fabChangeProfilePicture = view.findViewById(R.id.fab_change_profile_picture);
        fabSettings = view.findViewById(R.id.fab_settings);

        switchDarkMode = view.findViewById(R.id.switch_dark_mode);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new NotesViewModel(requireActivity().getApplication());
        mNavController = Navigation.findNavController(view);

        groupName.setOnClickListener(this);
        groupSchool.setOnClickListener(this);
        groupDepartment.setOnClickListener(this);
        groupLevel.setOnClickListener(this);

        profileImage.setOnClickListener(this);
        fabChangeProfilePicture.setOnClickListener(this);
        fabSettings.setOnClickListener(this);

        mViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                userName = user.getName();
                userSchool = user.getSchool();
                userDepartment = user.getDepartment();
                userLevel = user.getLevel();
                profileImageBitmap = user.getProfileImage();

                tvName.setText(userName);
                tvSchool.setText(userSchool);
                tvDepartment.setText(userDepartment);
                tvLevel.setText(userLevel);
                if (profileImageBitmap != null) {
                    Glide.with(requireContext())
                            .load(profileImageBitmap)
                            .into(profileImage);
                } else {
                    Glide.with(requireContext())
                            .load(R.drawable.ic_logo)
                            .into(profileImage);
                }
            }
        });

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        boolean nightMode = preferences.getBoolean(PreferenceKeys.DARK_MODE, false);
        switchDarkMode.setChecked(nightMode);
        switchDarkMode.setOnClickListener(v -> {
            if (switchDarkMode.isChecked()) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(PreferenceKeys.DARK_MODE, true);
                editor.apply();

                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(PreferenceKeys.DARK_MODE, false);
                editor.apply();

                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });
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
        } else if (id == R.id.fab_settings) {
            mNavController.navigate(R.id.settingsFragment);
        } else if (id == R.id.profile_image || id == R.id.fab_change_profile_picture) {
            launchProfileImageDialog();
        }
    }

    private void launchProfileImageDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        CharSequence[] selection = new CharSequence[]{
//                "View profile picture",
                "Take picture",
                "Choose from gallery",
                "Delete profile picture"
        };
        builder.setTitle("Options");
        builder.setSingleChoiceItems(selection, -1, (dialog, which) -> {
            switch (which) {
                case 0:
                    launchCamera();
                    dialog.dismiss();
                    break;
                case 1:
                    chooseFromGallery();
                    dialog.dismiss();
                    break;
                case 2:
                    deleteProfileImage();
                    dialog.dismiss();
                    break;
            }
        });
        builder.show();
    }

    private void deleteProfileImage() {
        User user = new User(userName, userSchool, userDepartment, userLevel, null);
        user.setId(USER_ID);
        mViewModel.updateUser(user);
    }

    private void launchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), "An error occurred", Toast.LENGTH_LONG).show();
        }
    }

    public void chooseFromGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        try {
            startActivityForResult(Intent.createChooser(intent, "Select image"), PICK_IMAGE_CODE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), "An error occurred", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            assert data != null;
            Bundle extras = data.getExtras();

            Bitmap profileImageBitmap = (Bitmap) extras.get("data");
            String name = tvName.getText().toString();
            String school = tvSchool.getText().toString();
            String department = tvDepartment.getText().toString();
            String level = tvLevel.getText().toString();

            User user = new User(name, school, department, level, profileImageBitmap);
            user.setId(USER_ID);
            mViewModel.updateUser(user);

            Glide.with(requireContext())
                    .load(profileImageBitmap)
                    .into(profileImage);
        } else if (requestCode == PICK_IMAGE_CODE && resultCode == RESULT_OK) {
            assert data != null;
            Uri uri = data.getData();
            try {
                InputStream stream = getContext().getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(stream);
                User user = new User(userName, userSchool, userDepartment, userLevel, bitmap);
                user.setId(USER_ID);
                mViewModel.updateUser(user);

                Glide.with(requireContext())
                        .load(bitmap)
                        .into(profileImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void launchNameDialog() {
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_profile, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme);

        MaterialTextView tvEditProfileDialogTitle = view.findViewById(R.id.tv_edit_profile_dialog_title);
        TextInputLayout inputLayout = view.findViewById(R.id.et_edit_profile_layout);
        TextInputEditText inputEditText = view.findViewById(R.id.et_edit_profile);
        MaterialButton btnCancel = view.findViewById(R.id.btn_cancel);
        MaterialButton btnSave = view.findViewById(R.id.btn_save);

        tvEditProfileDialogTitle.setText(R.string.edit_name);
        inputLayout.setHint(getString(R.string.name));
        inputEditText.setText(userName);

        btnCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());
        btnSave.setOnClickListener(v -> {
            String name = Objects.requireNonNull(inputEditText.getText()).toString().trim();
            String school = tvSchool.getText().toString().trim();
            String department = tvDepartment.getText().toString().trim();
            String level = tvLevel.getText().toString().trim();

            if (!isEmpty(name)) {
                if (!name.equals(userName)) {

                    User user = new User(name, school, department, level, profileImageBitmap);
                    user.setId(USER_ID);
                    mViewModel.updateUser(user);
                    tvName.setText(name);

                    bottomSheetDialog.dismiss();
                } else
                    Toast.makeText(getContext(), "Name not changed", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(getContext(), "Please Enter a name", Toast.LENGTH_SHORT).show();
        });
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }

    private void launchSchoolDialog() {
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_profile, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme);

        MaterialTextView tvEditProfileDialogTitle = view.findViewById(R.id.tv_edit_profile_dialog_title);
        TextInputLayout inputLayout = view.findViewById(R.id.et_edit_profile_layout);
        TextInputEditText inputEditText = view.findViewById(R.id.et_edit_profile);
        MaterialButton btnCancel = view.findViewById(R.id.btn_cancel);
        MaterialButton btnSave = view.findViewById(R.id.btn_save);

        tvEditProfileDialogTitle.setText(R.string.edit_school);
        inputLayout.setHint(getString(R.string.school));
        inputEditText.setText(userSchool);

        btnCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());
        btnSave.setOnClickListener(v -> {
            String name = tvName.getText().toString().trim();
            String school = inputEditText.getText().toString().trim();
            String department = tvDepartment.getText().toString().trim();
            String level = tvLevel.getText().toString().trim();

            if (!isEmpty(school)) {
                if (!school.equals(userSchool)) {
                    User user = new User(name, school, department, level, profileImageBitmap);
                    user.setId(USER_ID);

                    mViewModel.updateUser(user);
                    tvSchool.setText(school);

                    bottomSheetDialog.dismiss();
                } else
                    Toast.makeText(getContext(), "School not changed", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(getContext(), "Please Enter a school", Toast.LENGTH_SHORT).show();
        });
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }

    private void launchDepartmentDialog() {
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_profile, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme);

        MaterialTextView tvEditProfileDialogTitle = view.findViewById(R.id.tv_edit_profile_dialog_title);
        TextInputLayout inputLayout = view.findViewById(R.id.et_edit_profile_layout);
        TextInputEditText inputEditText = view.findViewById(R.id.et_edit_profile);
        MaterialButton btnCancel = view.findViewById(R.id.btn_cancel);
        MaterialButton btnSave = view.findViewById(R.id.btn_save);

        tvEditProfileDialogTitle.setText(R.string.edit_department);
        inputLayout.setHint(getString(R.string.department));
        inputEditText.setText(userDepartment);

        btnCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());
        btnSave.setOnClickListener(v -> {
            String name = tvName.getText().toString().trim();
            String school = tvSchool.getText().toString().trim();
            String department = inputEditText.getText().toString().trim();
            String level = tvLevel.getText().toString().trim();

            if (!isEmpty(department)) {
                if (!department.equals(userDepartment)) {
                    User user = new User(name, school, department, level, profileImageBitmap);
                    user.setId(USER_ID);

                    mViewModel.updateUser(user);
                    tvDepartment.setText(department);
                    bottomSheetDialog.dismiss();
                } else
                    Toast.makeText(getContext(), "Department not changed", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(getContext(), "Please Enter a department", Toast.LENGTH_SHORT).show();
        });
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }

    private void launchLevelDialog() {
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_level, null);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setBackground(getContext().getDrawable(R.drawable.alert_dialog_bg));
        }
        builder.setTitle(getString(R.string.select_level));
        AlertDialog alertDialog = builder.create();
        alertDialog.setView(view);

        Spinner spinnerLevel = view.findViewById(R.id.spinner_level);
        MaterialButton btnCancel = view.findViewById(R.id.btn_cancel);
        MaterialButton btnSave = view.findViewById(R.id.btn_save);

        String[] levels = {getString(R.string.select_level), "100L", "200L", "300L", "400L", "500L"};
        ArrayAdapter<String> adapterLevels = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, levels);

        adapterLevels.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLevel.setAdapter(adapterLevels);

        int selection = adapterLevels.getPosition(userLevel);
        spinnerLevel.setSelection(selection);

        btnCancel.setOnClickListener(v -> alertDialog.dismiss());
        btnSave.setOnClickListener(v -> {
            String name = tvName.getText().toString().trim();
            String school = tvSchool.getText().toString().trim();
            String department = tvDepartment.getText().toString().trim();
            String level = spinnerLevel.getSelectedItem().toString();

            if (!level.equals(getString(R.string.select_level))) {
                if (!level.equals(userLevel)) {
                    User user = new User(name, school, department, level, profileImageBitmap);
                    user.setId(USER_ID);

                    mViewModel.updateUser(user);
                    tvLevel.setText(level);

                    alertDialog.dismiss();
                } else
                    Toast.makeText(getContext(), "Level not changed", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(getContext(), "Please select a level", Toast.LENGTH_SHORT).show();
        });
        alertDialog.show();
    }
}