package com.example.blogapp.ui.auth

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.blogapp.core.Result
import com.example.blogapp.R
import com.example.blogapp.data.remote.auth.AuthDataSource
import com.example.blogapp.databinding.FragmentSetupProfileBinding
import com.example.blogapp.domain.auth.AuthRepoImpl
import com.example.blogapp.presentation.auth.AuthViewModel
import com.example.blogapp.presentation.auth.AuthViewModelFactory


class SetupProfileFragment : Fragment(R.layout.fragment_setup_profile) {

    private lateinit var  binding: FragmentSetupProfileBinding
    private val viewModel by viewModels <AuthViewModel>{
        AuthViewModelFactory(
            AuthRepoImpl(
                AuthDataSource()
            )
        )
    }
    private var bitmap : Bitmap? = null
    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as? Bitmap
            binding.imageProfile.setImageBitmap(imageBitmap)
            bitmap = imageBitmap
            Toast.makeText(requireContext(), "Foto tomada con éxito", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "No se pudo tomar la foto", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSetupProfileBinding.bind(view)

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        try{
            binding.imageProfile.setOnClickListener {
                takePictureLauncher.launch(takePictureIntent)
                Toast.makeText(requireContext(), "se abrio la camarar", Toast.LENGTH_SHORT).show()
            }

        }catch (e: ActivityNotFoundException){
            Toast.makeText(requireContext(), "Camera not found", Toast.LENGTH_SHORT).show()
        }

        binding.btnCreateProfile.setOnClickListener {
            val userName = binding.editTextUsername.text.toString().trim()
            val alerDialog = AlertDialog.Builder(requireContext()).setTitle("Uploading photo...").create()
            bitmap?.let{
                if(userName.isNotEmpty()){

                    viewModel.updateUserProfile(imageBitmap = it, username = userName).observe(viewLifecycleOwner) { result ->
                        when (result) {
                            is Result.Loading -> {
                                alerDialog.show()
                            }
                            is Result.Success -> {
                                alerDialog.dismiss()
                                findNavController().navigate(R.id.action_setupProfileFragment_to_homeScreenFragment)
                            }
                            is Result.Failure -> {
                                alerDialog.dismiss()
                            }
                        }
                    }

                }
            }
        }
    }
}