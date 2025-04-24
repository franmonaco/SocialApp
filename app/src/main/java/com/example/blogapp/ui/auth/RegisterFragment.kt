package com.example.blogapp.ui.auth


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.blogapp.R
import com.example.blogapp.core.Result
import com.example.blogapp.data.remote.auth.AuthDataSource
import com.example.blogapp.databinding.FragmentRegisterBinding
import com.example.blogapp.domain.auth.AuthRepoImpl
import com.example.blogapp.presentation.auth.AuthViewModel
import com.example.blogapp.presentation.auth.AuthViewModelFactory


class RegisterFragment : Fragment(R.layout.fragment_register) {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModels <AuthViewModel>{AuthViewModelFactory(AuthRepoImpl(AuthDataSource()))}


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRegisterBinding.bind(view)
        signUp()
    }

    private fun signUp() {
        binding.btnSignup.setOnClickListener {

            val userName = binding.textInputUsername.text.toString().trim()
            val email = binding.textInputEmail.text.toString().trim()
            val password = binding.textInputPassword.text.toString().trim()
            val confirmPassword = binding.textInputConfirmPassword.text.toString().trim()

            if (validateUserData(userName, email, password, confirmPassword)) return@setOnClickListener

            createUser(userName, email, password)
        }
    }

    private fun createUser(userName: String, email: String, password: String) {
        viewModel.signUp(email, password, userName).observe(viewLifecycleOwner, Observer { result ->
        when(result){
            is Result.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.btnSignup.isEnabled = false
            }
            is Result.Success -> {
                binding.progressBar.visibility = View.GONE
                findNavController().navigate(R.id.action_registerFragment_to_setupProfileFragment)
            }
            is Result.Failure -> {
                binding.progressBar.visibility = View.GONE
                binding.btnSignup.isEnabled = true
                Toast.makeText(requireContext(), "Error: ${result.exception}", Toast.LENGTH_SHORT).show()
            }

        }

        })
    }


    private fun validateUserData(
            userName: String,
            email: String,
            password: String,
            confirmPassword: String
        ): Boolean {
            if (password != confirmPassword) {
                binding.textInputConfirmPassword.error = "Password does not match"
                binding.textInputPassword.error = "Password does not match"
                return true
            }
            if (userName.isEmpty()) {
                binding.textInputUsername.error = "Please enter your username"
                return true
            }
            if (email.isEmpty()) {
                binding.textInputEmail.error = "Please enter your email"
                return true
            }
            if (password.isEmpty()) {
                binding.textInputPassword.error = "Please enter your password"
                return true
            }
            if (confirmPassword.isEmpty()) {
                binding.textInputConfirmPassword.error = "Please confirm your password"
                return true
            }
            return false
        }
}








