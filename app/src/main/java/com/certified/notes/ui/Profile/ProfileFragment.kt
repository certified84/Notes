package com.certified.notes.ui.Profile

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils.isEmpty
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.certified.notes.R
import com.certified.notes.model.User
import com.certified.notes.util.PreferenceKeys
import com.github.captain_miao.optroundcardview.OptRoundCardView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import de.hdodenhof.circleimageview.CircleImageView
import java.io.FileNotFoundException

class ProfileFragment : Fragment(), View.OnClickListener {

    private lateinit var viewModel: ProfileViewModel
    private lateinit var mNavController: NavController

    private lateinit var groupName: Group
    private lateinit var groupSchool: Group
    private lateinit var groupDepartment: Group
    private lateinit var groupLevel: Group

    private lateinit var tvName: TextView
    private lateinit var tvSchool: TextView
    private lateinit var tvDepartment: TextView
    private lateinit var tvLevel: TextView

    private lateinit var profileImage: CircleImageView
    private lateinit var switchDarkMode: SwitchMaterial

    private lateinit var fabChangeProfilePicture: FloatingActionButton
    private lateinit var fabSettings: FloatingActionButton

    private lateinit var userName: String
    private lateinit var userSchool: String
    private lateinit var userDepartment: String
    private lateinit var userLevel: String
    private var profileImageBitmap: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        groupName = view.findViewById(R.id.group_edit_name)
        groupSchool = view.findViewById(R.id.group_edit_school)
        groupDepartment = view.findViewById(R.id.group_edit_department)
        groupLevel = view.findViewById(R.id.group_edit_level)

        tvName = view.findViewById(R.id.tv_name)
        tvSchool = view.findViewById(R.id.tv_school)
        tvDepartment = view.findViewById(R.id.tv_department)
        tvLevel = view.findViewById(R.id.tv_level)

        profileImage = view.findViewById(R.id.profile_image)

        fabChangeProfilePicture = view.findViewById(R.id.fab_change_profile_picture)
        fabSettings = view.findViewById(R.id.fab_settings)

        switchDarkMode = view.findViewById(R.id.switch_dark_mode)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModelFactory = ProfileViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ProfileViewModel::class.java)
        mNavController = Navigation.findNavController(view)

        groupName.setOnClickListener(this)
        groupSchool.setOnClickListener(this)
        groupDepartment.setOnClickListener(this)
        groupLevel.setOnClickListener(this)

        profileImage.setOnClickListener(this)

        fabChangeProfilePicture.setOnClickListener(this)
        fabSettings.setOnClickListener(this)

        viewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                userName = user.name
                userSchool = user.school
                userDepartment = user.department
                userLevel = user.level
                profileImageBitmap = user.profileImage

                tvName.text = userName
                tvSchool.text = userSchool
                tvDepartment.text = userDepartment
                tvLevel.text = userLevel

                if (profileImageBitmap != null) {
                    Glide.with(requireContext())
                        .load(profileImageBitmap)
                        .into(profileImage)
                } else {
                    Glide.with(requireContext())
                        .load(R.drawable.ic_logo)
                        .into(profileImage)
                }
            }
        }

        val preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val nightMode = preferences.getBoolean(PreferenceKeys.DARK_MODE, false)
        val editor = preferences.edit()

        switchDarkMode.isChecked = nightMode
        switchDarkMode.setOnClickListener {
            if (switchDarkMode.isChecked) {
                editor.putBoolean(PreferenceKeys.DARK_MODE, true)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                editor.putBoolean(PreferenceKeys.DARK_MODE, false)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            editor.apply()
        }
    }

    override fun onResume() {
        super.onResume()

        activity?.findViewById<OptRoundCardView>(R.id.optRoundCardView2)?.visibility = View.VISIBLE
        activity?.findViewById<FloatingActionButton>(R.id.fab)?.visibility = View.VISIBLE
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.group_edit_name -> launchNameDialog()
            R.id.group_edit_school -> launchSchoolDialog()
            R.id.group_edit_department -> launchDepartmentDialog()
            R.id.group_edit_level -> launchLevelDialog()
//            R.id.fab_settings -> mNavController.navigate(R.id.settingsFragment)
            R.id.profile_image -> launchProfileImageDialog()
            R.id.fab_change_profile_picture -> launchProfileImageDialog()
        }
    }

    private fun launchProfileImageDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val selection = arrayOf(
//            "View profile picture",
            "Take picture",
            "Choose from gallery",
            "Delete profile picture",
        )
        builder.setTitle("Options")
        builder.setSingleChoiceItems(selection, -1) { dialog: DialogInterface, which: Int ->
            when (which) {
                0 -> launchCamera()
                1 -> chooseFromGallery()
                2 -> deleteProfilePicture()
            }
            dialog.dismiss()
        }
        builder.show()
    }

    private fun deleteProfilePicture() {
        val user = User(USER_ID, userName, userSchool, userDepartment, userLevel, null)
        viewModel.updateUser(user)
    }

    private fun launchCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "An error occurred: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun chooseFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        //        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        try {
            startActivityForResult(Intent.createChooser(intent, "Select image"), PICK_IMAGE_CODE)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "An error occurred: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            assert(data != null)
            val extras = data?.extras
            val profileImageBitmap = extras!!["data"] as Bitmap?
            val name = tvName.text.toString()
            val school = tvSchool.text.toString()
            val department = tvDepartment.text.toString()
            val level = tvLevel.text.toString()

            val user = User(USER_ID, name, school, department, level, profileImageBitmap)
            viewModel.updateUser(user)

            Glide.with(requireContext())
                .load(profileImageBitmap)
                .into(profileImage)
        } else if (requestCode == PICK_IMAGE_CODE && resultCode == Activity.RESULT_OK) {
            assert(data != null)
            val uri = data?.data
            try {
                val stream = uri?.let { requireContext().contentResolver.openInputStream(it) }
                val bitmap = BitmapFactory.decodeStream(stream)
                val user = User(USER_ID, userName, userSchool, userDepartment, userLevel, bitmap)
                viewModel.updateUser(user)
                Glide.with(requireContext())
                    .load(bitmap)
                    .into(profileImage)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    private fun launchNameDialog() {
        val inflater = this.layoutInflater
        val view =
            inflater.inflate(R.layout.dialog_edit_profile, ConstraintLayout(requireContext()))

        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        val tvEditProfileDialogTitle: MaterialTextView =
            view.findViewById(R.id.tv_edit_profile_dialog_title)

        val inputLayout: TextInputLayout = view.findViewById(R.id.et_edit_profile_layout)
        val inputEditText: TextInputEditText = view.findViewById(R.id.et_edit_profile)

        val btnCancel: MaterialButton = view.findViewById(R.id.btn_cancel)
        val btnSave: MaterialButton = view.findViewById(R.id.btn_save)

        tvEditProfileDialogTitle.setText(R.string.edit_name)
        inputLayout.hint = getString(R.string.name)
        inputEditText.setText(userName)

        btnCancel.setOnClickListener { bottomSheetDialog.dismiss() }
        btnSave.setOnClickListener {
            val name = tvName.toString().trim()
            val school = tvSchool.text.toString().trim()
            val department = tvDepartment.text.toString().trim()
            val level = tvLevel.text.toString().trim()
            if (!isEmpty(name)) {
                if (name != userName) {
                    val user = User(USER_ID, name, school, department, level, profileImageBitmap)
                    viewModel.updateUser(user)
                    tvName.text = name
                } else Toast.makeText(context, "Name not changed", Toast.LENGTH_SHORT).show()
                bottomSheetDialog.dismiss()
            } else Toast.makeText(context, "Please Enter a name", Toast.LENGTH_SHORT).show()
        }

        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }

    private fun launchSchoolDialog() {
        val inflater = this.layoutInflater
        val view =
            inflater.inflate(R.layout.dialog_edit_profile, ConstraintLayout(requireContext()))

        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        val tvEditProfileDialogTitle: MaterialTextView =
            view.findViewById(R.id.tv_edit_profile_dialog_title)

        val inputLayout: TextInputLayout = view.findViewById(R.id.et_edit_profile_layout)
        val inputEditText: TextInputEditText = view.findViewById(R.id.et_edit_profile)

        val btnCancel: MaterialButton = view.findViewById(R.id.btn_cancel)
        val btnSave: MaterialButton = view.findViewById(R.id.btn_save)

        tvEditProfileDialogTitle.setText(R.string.edit_school)
        inputLayout.hint = getString(R.string.school)
        inputEditText.setText(userSchool)

        btnCancel.setOnClickListener { bottomSheetDialog.dismiss() }
        btnSave.setOnClickListener {
            val name = tvName.text.toString().trim()
            val school = inputEditText.text.toString().trim()
            val department = tvDepartment.text.toString().trim()
            val level = tvLevel.text.toString().trim()
            if (!isEmpty(school)) {
                if (school != userSchool) {
                    val user = User(USER_ID, name, school, department, level, profileImageBitmap)
                    viewModel.updateUser(user)
                    tvSchool.text = school
                } else Toast.makeText(context, "School not changed", Toast.LENGTH_SHORT).show()
                bottomSheetDialog.dismiss()
            } else Toast.makeText(context, "Please Enter a school", Toast.LENGTH_SHORT).show()
        }
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }

    private fun launchDepartmentDialog() {
        val inflater = this.layoutInflater
        val view =
            inflater.inflate(R.layout.dialog_edit_profile, ConstraintLayout(requireContext()))

        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        val tvEditProfileDialogTitle: MaterialTextView =
            view.findViewById(R.id.tv_edit_profile_dialog_title)

        val inputLayout: TextInputLayout = view.findViewById(R.id.et_edit_profile_layout)
        val inputEditText: TextInputEditText = view.findViewById(R.id.et_edit_profile)

        val btnCancel: MaterialButton = view.findViewById(R.id.btn_cancel)
        val btnSave: MaterialButton = view.findViewById(R.id.btn_save)

        tvEditProfileDialogTitle.setText(R.string.edit_department)
        inputLayout.hint = getString(R.string.department)
        inputEditText.setText(userDepartment)

        btnCancel.setOnClickListener { bottomSheetDialog.dismiss() }
        btnSave.setOnClickListener {
            val name = tvName.text.toString().trim()
            val school = tvSchool.text.toString().trim()
            val department = inputEditText.text.toString().trim()
            val level = tvLevel.text.toString().trim()
            if (!isEmpty(department)) {
                if (department != userDepartment) {
                    val user = User(USER_ID, name, school, department, level, profileImageBitmap)
                    viewModel.updateUser(user)
                    tvDepartment.text = department
                } else Toast.makeText(context, "Department not changed", Toast.LENGTH_SHORT).show()
                bottomSheetDialog.dismiss()
            } else Toast.makeText(context, "Please Enter a department", Toast.LENGTH_SHORT).show()
        }
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }

    private fun launchLevelDialog() {
        val inflater = this.layoutInflater
        val view = inflater.inflate(R.layout.dialog_edit_level, null)
        val builder = MaterialAlertDialogBuilder(requireContext())

        builder.background =
            AppCompatResources.getDrawable(requireContext(), R.drawable.alert_dialog_bg)
        builder.setTitle(getString(R.string.select_level))

        val alertDialog = builder.create()
        alertDialog.setView(view)

        val spinnerLevel = view.findViewById<Spinner>(R.id.spinner_level)
        val btnCancel: MaterialButton = view.findViewById(R.id.btn_cancel)
        val btnSave: MaterialButton = view.findViewById(R.id.btn_save)

        val levels =
            arrayOf(getString(R.string.select_level), "100L", "200L", "300L", "400L", "500L")
        val adapterLevels =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, levels)

        adapterLevels.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLevel.adapter = adapterLevels

        val selection = adapterLevels.getPosition(userLevel)
        spinnerLevel.setSelection(selection)
        btnCancel.setOnClickListener { alertDialog.dismiss() }
        btnSave.setOnClickListener {
            val name = tvName.text.toString().trim()
            val school = tvSchool.text.toString().trim()
            val department = tvDepartment.text.toString().trim()
            val level = spinnerLevel.selectedItem.toString()
            if (level != getString(R.string.select_level)) {
                if (level != userLevel) {
                    val user = User(USER_ID, name, school, department, level, profileImageBitmap)
                    viewModel.updateUser(user)
                    tvLevel.text = level
                } else Toast.makeText(context, "Level not changed", Toast.LENGTH_SHORT).show()
                alertDialog.dismiss()
            } else Toast.makeText(context, "Please select a level", Toast.LENGTH_SHORT).show()
        }
        alertDialog.show()
    }

    companion object {
        private const val USER_ID = 0
        private const val REQUEST_IMAGE_CAPTURE = 101
        private const val PICK_IMAGE_CODE = 102
    }
}