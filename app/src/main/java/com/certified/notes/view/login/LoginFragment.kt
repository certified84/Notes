package com.certified.notes.view.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.certified.notes.R
import com.certified.notes.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.shashank.sony.fancytoastlib.FancyToast

class LoginFragment : Fragment() {

    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        auth = Firebase.auth
        val currentUser = auth.currentUser

        binding.apply {
            btnLogin.setOnClickListener {

                progressBar.visibility = View.VISIBLE
                val email = etEmail.text.toString().trim()
                val password = etPassword.text.toString().trim()

                if (currentUser == null) {
                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(requireActivity()) { task ->
                                if (task.isSuccessful) {

                                    progressBar.visibility = View.GONE
                                    uploadFilesToFireStore()

                                    val navOptions = NavOptions.Builder()
                                        .setPopUpTo(R.id.splashFragment, true).build()
                                    navController.navigate(R.id.homeFragment, null, navOptions)
                                } else {
                                    FancyToast.makeText(
                                        requireContext(), "Authentication failed. ${task.exception}",
                                        FancyToast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    } else {
                        FancyToast.makeText(
                            requireContext(),
                            "All fields are required",
                            FancyToast.LENGTH_LONG
                        ).show()
                    }
                }
            }

            tvCreateAnAccount.setOnClickListener {
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.loginFragment, true).build()
                navController.navigate(R.id.signupFragment, null, navOptions)
            }

            btnNotNow.setOnClickListener {
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.splashFragment, true).build()
                navController.navigate(R.id.homeFragment, null, navOptions)
            }
        }
    }

    private fun uploadFilesToFireStore() {
        TODO("Not yet implemented")
    }
}