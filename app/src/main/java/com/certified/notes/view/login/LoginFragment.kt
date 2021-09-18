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
import com.github.captain_miao.optroundcardview.OptRoundCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
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

                val email = etEmail.text.toString().trim()
                val password = etPassword.text.toString().trim()

                if (currentUser == null) {
                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        progressBar.visibility = View.VISIBLE
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(requireActivity()) { task ->
                                if (task.isSuccessful) {

                                    progressBar.visibility = View.GONE
                                    uploadFilesToFireStore()

                                    val user = auth.currentUser
//                                    if (user?.isEmailVerified!!) {
//                                        val navOptions = NavOptions.Builder()
//                                            .setPopUpTo(R.id.splashFragment, true).build()
//                                        navController.navigate(R.id.homeFragment, null, navOptions)
//                                    } else
//                                        FancyToast.makeText(
//                                            requireContext(),
//                                            "Check your email for verification link",
//                                            FancyToast.LENGTH_LONG
//                                        ).show()
                                } else {
                                    FancyToast.makeText(
                                        requireContext(),
                                        "Authentication failed. ${task.exception}",
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

    override fun onResume() {
        super.onResume()

        activity?.findViewById<OptRoundCardView>(R.id.optRoundCardView2)?.visibility = View.VISIBLE
        activity?.findViewById<FloatingActionButton>(R.id.fab)?.visibility = View.VISIBLE
    }

    private fun uploadFilesToFireStore() {
//        TODO("Not yet implemented")
    }
}