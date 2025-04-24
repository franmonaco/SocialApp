package com.example.blogapp.ui.camera

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.blogapp.R
import com.example.blogapp.data.remote.camera.CameraDataSource
import com.example.blogapp.databinding.FragmentCameraBinding
import com.example.blogapp.domain.camera.CameraRepoImpl
import com.example.blogapp.presentation.camera.CameraViewModel
import com.example.blogapp.presentation.camera.CameraViewModelFactory
import kotlin.getValue
import com.example.blogapp.core.Result


class CameraFragment : Fragment(R.layout.fragment_camera) {

    private lateinit var binding: FragmentCameraBinding
    private var bitmap : Bitmap? = null
    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as? Bitmap
            binding.imagePhoto.setImageBitmap(imageBitmap)
            bitmap = imageBitmap
            Toast.makeText(requireContext(), "Foto tomada con éxito", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "No se pudo tomar la foto", Toast.LENGTH_SHORT).show()
        }
    }
    private val viewModel by viewModels<CameraViewModel>{CameraViewModelFactory(CameraRepoImpl
        (CameraDataSource()))}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCameraBinding.bind(view)

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
                takePictureLauncher.launch(takePictureIntent)
                Toast.makeText(requireContext(), "se abrio la camarar", Toast.LENGTH_SHORT).show()

        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), "No se pudo abrir la cámara", Toast.LENGTH_SHORT).show()
        }

        binding.uploadBtn.setOnClickListener {
            bitmap?.let{
                viewModel.uploadPhoto(it, binding.etxDescription.text.toString().trim()).observe(viewLifecycleOwner) { result ->
                    when (result) {
                        is Result.Success -> {
                            findNavController().navigate(R.id.action_cameraFragment_to_homeScreenFragment)
                        }
                        is Result.Failure -> {
                            Toast.makeText(requireContext(), "Error ${result.exception}", Toast.LENGTH_SHORT).show()
                        }
                        is Result.Loading -> {
                            Toast.makeText(requireContext(), "Loading...", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

        }
    }
}


