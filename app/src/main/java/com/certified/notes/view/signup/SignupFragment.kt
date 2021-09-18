package com.certified.notes.view.signup

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.certified.notes.R
import com.certified.notes.databinding.FragmentSignupBinding
import com.github.captain_miao.optroundcardview.OptRoundCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.shashank.sony.fancytoastlib.FancyToast

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

        navController = Navigation.findNavController(view)
        val currentUser = auth.currentUser

        binding.apply {
            btnSignUp.setOnClickListener {

                val email = etEmail.text.toString().trim()
                val password = etPassword.text.toString().trim()
                val confirmPassword = etConfirmPassword.text.toString().trim()

                if (currentUser == null) {
                    if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                        if (password == confirmPassword) {
                            progressBar.visibility = View.VISIBLE
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(requireActivity()) { task ->
                                    if (task.isSuccessful) {

                                        progressBar.visibility = View.GONE
                                        val user = auth.currentUser
                                        user?.sendEmailVerification()
                                        uploadUserDetails(user!!)

                                        FancyToast.makeText(
                                            requireContext(),
                                            "Success. Check email for confirmation link",
                                            FancyToast.LENGTH_SHORT
                                        ).show()

                                        Firebase.auth.signOut()

                                        val navOptions = NavOptions.Builder()
                                            .setPopUpTo(R.id.splashFragment, true).build()
                                        navController.navigate(R.id.loginFragment, null, navOptions)
                                    } else {
                                        FancyToast.makeText(
                                            requireContext(),
                                            "Authentication failed ${task.exception}",
                                            FancyToast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                        } else {
                            FancyToast.makeText(
                                requireContext(),
                                "Passwords do not match",
                                FancyToast.LENGTH_LONG
                            ).show()
                            etConfirmPassword.requestFocus()
                        }
                    }  else {
                        FancyToast.makeText(
                            requireContext(),
                            "All fields are required",
                            FancyToast.LENGTH_LONG
                        ).show()
                    }
                }
            }

            tvLoginHere.setOnClickListener {
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.signupFragment, true).build()
                navController.navigate(R.id.loginFragment, null, navOptions)
            }

            btnNotNow.setOnClickListener {
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.splashFragment, true).build()
                navController.navigate(R.id.homeFragment, null, navOptions)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        activity?.findViewById<OptRoundCardView>(R.id.optRoundCardView2)?.visibility = View.VISIBLE
        activity?.findViewById<FloatingActionButton>(R.id.fab)?.visibility = View.VISIBLE
    }

    private fun uploadUserDetails(user: FirebaseUser) {

    }
}