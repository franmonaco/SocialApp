package com.example.blogapp.domain.auth

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import com.google.firebase.auth.FirebaseUser

interface AuthRepo {
    suspend fun signIn(email: String, password: String) : FirebaseUser?
    suspend fun signUp(email: String, password: String, userName: String): FirebaseUser?
    suspend fun updateUserProfile(imageBitmap: Bitmap, username: String)
}