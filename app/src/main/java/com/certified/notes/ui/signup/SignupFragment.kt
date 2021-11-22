package com.certified.notes.ui.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.certified.notes.R
import com.certified.notes.databinding.FragmentSignupBinding
import com.certified.notes.model.User
import com.certified.notes.util.Extensions.showToast
import com.github.captain_miao.optroundcardview.OptRoundCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class SignupFragment : Fragment() {

    private lateinit var navController: NavController
    private lateinit var binding: FragmentSignupBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSignupBinding.inflate(layoutInflater)
        auth = Firebase.auth

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.findViewById<OptRoundCardView>(R.id.optRoundCardView2)?.visibility = View.GONE
        activity?.findViewById<FloatingActionButton>(R.id.fab)?.visibility = View.GONE

        navController = Navigation.findNavController(view)
        val currentUser = auth.currentUser

        binding.apply {
            btnSignUp.setOnClickListener {
                val name = etFullName.text.toString()
                val email = etEmail.text.toString().trim()
                val password = etPassword.text.toString().trim()
                val confirmPassword = etConfirmPassword.text.toString().trim()

                if (currentUser == null) {
                    if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                        if (password == confirmPassword) {
                            progressBar.visibility = View.VISIBLE
                            createAccount(name, email, password, progressBar)
                        } else {
                            showToast("Passwords do not match")
                            etConfirmPassword.requestFocus()
                        }
                    } else
                        showToast("All fields are required")
                }
            }

            tvLoginHere.setOnClickListener { navigateTo(R.id.loginFragment) }
            btnNotNow.setOnClickListener { navigateTo(R.id.homeFragment) }
        }
    }

    private fun createAccount(
        name: String,
        email: String,
        password: String,
        progressBar: ProgressBar
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    progressBar.visibility = View.GONE
                    val user = auth.currentUser
                    user?.sendEmailVerification()
                    saveInFirestore(name, email, user!!)
                    showToast("Account created successfully. Check email for confirmation link")
                } else {
                    showToast("Unable to create account: ${task.exception}")
                    progressBar.visibility = View.GONE
                }
            }
    }

    private fun saveInFirestore(name: String, email: String, user: FirebaseUser) {
        val newUser =
            User(name, "Enter school", "Enter department", "Enter level", null)
        newUser.uid = user.uid
        newUser.email = email

        val db = Firebase.firestore
        val userRef =
            db.collection("users").document(user.uid)
        userRef.set(newUser).addOnCompleteListener {
            if (it.isSuccessful) {
                updateProfile(user, newUser)
                Firebase.auth.signOut()
                navigateTo(R.id.loginFragment)
            } else
                showToast("An error occurred: ${it.exception}")
        }
    }

    private fun navigateTo(id: Int) {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.splashFragment, true).build()
        when (id) {
            R.id.loginFragment -> navController.navigate(R.id.loginFragment, null, navOptions)
            R.id.homeFragment -> navController.navigate(R.id.homeFragment, null, navOptions)
        }
    }

    private fun updateProfile(fUser: FirebaseUser, nUser: User) {
        val profileChangeRequest =
            UserProfileChangeRequest.Builder()
                .setDisplayName(nUser.name)
                .setPhotoUri(nUser.profileImage)
                .build()
        fUser.updateProfile(profileChangeRequest)
    }
}