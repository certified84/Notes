package com.certified.notes.view.Profile

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils.isEmpty
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.certified.notes.R
import com.certified.notes.databinding.FragmentProfileBinding
import com.certified.notes.model.User
import com.certified.notes.util.PreferenceKeys
import com.github.captain_miao.optroundcardview.OptRoundCardView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

class ProfileFragment : Fragment(), View.OnClickListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: ProfileViewModel
    private lateinit var mNavController: NavController
    private lateinit var binding: FragmentProfileBinding

    private lateinit var userName: String
    private lateinit var userSchool: String
    private lateinit var userDepartment: String
    private lateinit var userLevel: String
    private var profileImageBitmap: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)

        auth = Firebase.auth

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModelFactory = ProfileViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ProfileViewModel::class.java)
        mNavController = Navigation.findNavController(view)

        binding.apply {
            val currentUser = auth.currentUser
            if (currentUser == null)
                loadFromRoom()
            else
                loadFromFirestore(currentUser)

            groupEditName.setOnClickListener(this@ProfileFragment)
            groupEditSchool.setOnClickListener(this@ProfileFragment)
            groupEditDepartment.setOnClickListener(this@ProfileFragment)
            groupEditLevel.setOnClickListener(this@ProfileFragment)

            profileImage.setOnClickListener(this@ProfileFragment)
            fabChangeProfilePicture.setOnClickListener(this@ProfileFragment)
            fabSettings.setOnClickListener(this@ProfileFragment)

            val preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
            val nightMode = preferences.getInt(PreferenceKeys.DARK_MODE, 0)
            val editor = preferences.edit()

            switchDarkMode.isChecked = nightMode == 1
            switchDarkMode.setOnClickListener {
                if (switchDarkMode.isChecked) {
                    editor.putInt(PreferenceKeys.DARK_MODE, 1)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    editor.putInt(PreferenceKeys.DARK_MODE, 2)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
                editor.apply()
            }
        }
    }

    private fun loadFromFirestore(currentUser: FirebaseUser) {
        binding.apply {
            val db = Firebase.firestore
            val userRef =
                db.collection("users").document(currentUser.uid)
            userRef.get().addOnSuccessListener {
                userName = currentUser.displayName!!
                profileImageBitmap = currentUser.photoUrl
                userSchool = it.getString("school")!!
                userDepartment = it.getString("department")!!
                userLevel = it.getString("level")!!

                tvName.text = currentUser.displayName!!
                tvSchool.text = it.getString("school")!!
                tvDepartment.text = it.getString("department")!!
                tvLevel.text = it.getString("level")!!
            }

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

    private fun loadFromRoom() {
        binding.apply {
            viewModel.user.observe(viewLifecycleOwner) { user ->
                if (user != null) {
                    userName = user.name
                    userSchool = user.school
                    userDepartment = user.department
                    userLevel = user.level
                    profileImageBitmap = user.profileImage

                    tvName.text = user.name
                    tvSchool.text = user.school
                    tvDepartment.text = user.department
                    tvLevel.text = user.level

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
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().findViewById<OptRoundCardView>(R.id.optRoundCardView2)?.visibility = View.VISIBLE
        requireActivity().findViewById<FloatingActionButton>(R.id.fab)?.visibility = View.VISIBLE
    }

    override fun onClick(v: View) {
        binding.apply {
            when (v) {
                groupEditName -> launchNameDialog()
                groupEditSchool -> launchSchoolDialog()
                groupEditDepartment -> launchDepartmentDialog()
                groupEditLevel -> launchLevelDialog()
                profileImage -> launchProfileImageDialog()
                fabChangeProfilePicture -> launchProfileImageDialog()
//                fabSettings -> mNavController.navigate(R.id.settings_fragment)
            }
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
        val user = User(userName, userSchool, userDepartment, userLevel, null)
        user.id = USER_ID
        if (auth.currentUser == null)
            viewModel.updateUser(user)
        else
            updateUser(user)
    }

    private fun updateUser(user: User) {
        val db = Firebase.firestore
        val userRef =
            db.collection("users").document(auth.currentUser!!.uid)
        userRef.set(user).addOnCompleteListener {

        }
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
            try {
                requireContext().openFileOutput("profile_image", Context.MODE_PRIVATE).use {
                    profileImageBitmap?.compress(Bitmap.CompressFormat.PNG, 100, it)
                }
                val file = File(requireContext().filesDir, "profile_image")
                val uri = Uri.fromFile(file)
//                requireContext().openFileInput("profile_image").use {
//                    uri = it
//                }

                val user = User(userName, userSchool, userDepartment, userLevel, uri)
                user.id = USER_ID
                if (auth.currentUser == null)
                    viewModel.updateUser(user)
                else {
                    updateUser(user)
                    val profileChangeRequest =
                        UserProfileChangeRequest.Builder()
                            .setPhotoUri(uri)
                            .build()
                    auth.currentUser!!.updateProfile(profileChangeRequest)
                }
                Glide.with(requireContext())
                    .load(uri)
                    .into(binding.profileImage)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else if (requestCode == PICK_IMAGE_CODE && resultCode == Activity.RESULT_OK) {
            assert(data != null)
            val uri = data?.data
            try {
                val user = User(userName, userSchool, userDepartment, userLevel, uri)
                user.id = USER_ID
                if (auth.currentUser == null)
                    viewModel.updateUser(user)
                else {
                    updateUser(user)
                    val profileChangeRequest =
                        UserProfileChangeRequest.Builder()
                            .setPhotoUri(uri)
                            .build()
                    auth.currentUser!!.updateProfile(profileChangeRequest)
                }
                Glide.with(requireContext())
                    .load(uri)
                    .into(binding.profileImage)
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
            binding.apply {
                val name = inputEditText.text.toString().trim()
                val school = tvSchool.text.toString().trim()
                val department = tvDepartment.text.toString().trim()
                val level = tvLevel.text.toString().trim()
                if (!isEmpty(name)) {
                    if (name != userName) {
                        val user = User(name, school, department, level, profileImageBitmap)
                        user.id = USER_ID
                        viewModel.updateUser(user)
                        tvName.text = name
                    } else Toast.makeText(context, "Name not changed", Toast.LENGTH_SHORT).show()
                    bottomSheetDialog.dismiss()
                } else Toast.makeText(context, "Please Enter a name", Toast.LENGTH_SHORT).show()
            }
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
            binding.apply {
                val name = tvName.text.toString().trim()
                val school = inputEditText.text.toString().trim()
                val department = tvDepartment.text.toString().trim()
                val level = tvLevel.text.toString().trim()
                if (!isEmpty(school)) {
                    if (school != userSchool) {
                        val user = User(name, school, department, level, profileImageBitmap)
                        user.id = USER_ID
                        viewModel.updateUser(user)
                        tvSchool.text = school
                    } else Toast.makeText(context, "School not changed", Toast.LENGTH_SHORT).show()
                    bottomSheetDialog.dismiss()
                } else Toast.makeText(context, "Please Enter a school", Toast.LENGTH_SHORT).show()
            }
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
            binding.apply {
                val name = tvName.text.toString().trim()
                val school = tvSchool.text.toString().trim()
                val department = inputEditText.text.toString().trim()
                val level = tvLevel.text.toString().trim()
                if (!isEmpty(department)) {
                    if (department != userDepartment) {
                        val user = User(name, school, department, level, profileImageBitmap)
                        user.id = USER_ID
                        viewModel.updateUser(user)
                        tvDepartment.text = department
                    } else Toast.makeText(context, "Department not changed", Toast.LENGTH_SHORT)
                        .show()
                    bottomSheetDialog.dismiss()
                } else Toast.makeText(context, "Please Enter a department", Toast.LENGTH_SHORT)
                    .show()
            }
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
            binding.apply {
                val name = tvName.text.toString().trim()
                val school = tvSchool.text.toString().trim()
                val department = tvDepartment.text.toString().trim()
                val level = spinnerLevel.selectedItem.toString()
                if (level != getString(R.string.select_level)) {
                    if (level != userLevel) {
                        val user = User(name, school, department, level, profileImageBitmap)
                        user.id = USER_ID
                        viewModel.updateUser(user)
                        tvLevel.text = level
                    } else Toast.makeText(context, "Level not changed", Toast.LENGTH_SHORT).show()
                    alertDialog.dismiss()
                } else Toast.makeText(context, "Please select a level", Toast.LENGTH_SHORT).show()
            }
        }
        alertDialog.show()
    }

    companion object {
        private const val USER_ID = 0
        private const val REQUEST_IMAGE_CAPTURE = 101
        private const val PICK_IMAGE_CODE = 102
    }
}