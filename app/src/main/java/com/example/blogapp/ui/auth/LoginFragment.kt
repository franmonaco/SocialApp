package com.example.blogapp.ui.auth

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.blogapp.R
import com.example.blogapp.core.Result
import com.example.blogapp.data.remote.auth.AuthDataSource
import com.example.blogapp.databinding.FragmentLoginBinding
import com.example.blogapp.domain.auth.AuthRepoImpl
import com.example.blogapp.presentation.auth.AuthViewModel
import com.example.blogapp.presentation.auth.AuthViewModelFactory
import com.google.firebase.auth.FirebaseAuth


class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var  binding: FragmentLoginBinding
    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val viewModel by viewModels<AuthViewModel>{AuthViewModelFactory(AuthRepoImpl(
        AuthDataSource()
    ))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)
        isUserLoggedIn()
        doLogin()
        goToSignUp()

    }

    private fun isUserLoggedIn() {
        firebaseAuth.currentUser?.let { user ->
            if(user.displayName.isNullOrEmpty()){
                findNavController().navigate(R.id.action_login_fragment_to_setupProfileFragment)
            }else{
                findNavController().navigate(R.id.action_login_fragment_to_homeScreenFragment)
            }
        }
    }

    private fun doLogin() {
        binding.btnSignin.setOnClickListener {
            val email = binding.textInputEmail.text.toString().trim()
            val password = binding.textInputPassword.text.toString().trim()
            validateCredentials(email, password)
            signIn(email, password)
        }
    }

    private fun goToSignUp() {
        binding.txtSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_login_fragment_to_registerFragment)
        }
    }


    private fun validateCredentials(email: String, password: String) {
        
        if (email.isEmpty()) {
            binding.textInputEmail.error = "Email is required"
            return
        }

        if (password.isEmpty()) {
            binding.textInputPassword.error = "Password is required"
            return
        }


    }

    /*del login fragment se hace una peticion para ir a firebase con el metodo sign in
    que esta dentro del viewmodel el cual es LoginScreenViewmodel, del viewmodel vamos
    al repo (LoginRepo), luego a su implementacion (LoginRepoImpl) a la implementacion
    del repo vamos al dataSource el cual es (LoginDataSource) y en este ultimo hacemos
    el login con firebase*/

    private fun signIn(email: String, password: String) {
        viewModel.signIn(email, password).observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnSignin.isEnabled = false
                    Log.d("resulttt", "Cargando... $result")

                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    findNavController().navigate(R.id.action_login_fragment_to_homeScreenFragment)
                    Log.d("resulttt", "Hecho... $result")
                    if(result.data?.displayName.isNullOrEmpty()){
                        findNavController().navigate(R.id.action_login_fragment_to_setupProfileFragment)
                    }else{
                        findNavController().navigate(R.id.action_login_fragment_to_homeScreenFragment)
                    }
                }
                is Result.Failure -> {
                    Log.d("resulttt", "Failure... $result")
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        "Error: ${result.exception}",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.btnSignin.isEnabled = true
                }
            }
        })

    }


}