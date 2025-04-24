package com.example.blogapp.data.remote.auth

import android.graphics.Bitmap
import android.net.Uri
import com.example.blogapp.data.model.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import androidx.core.net.toUri

class AuthDataSource {
    suspend fun signIn(email: String, password: String): FirebaseUser? {
        val authResult = FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).await()
        return authResult.user
    }

    suspend fun signUp(email: String, password: String, userName: String): FirebaseUser? {
        val authResult = FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).await()
        authResult.user?.uid?.let { uid ->
        FirebaseFirestore.getInstance().collection("users").document(uid).set(User(email, userName, "FOTO_URL.png")).await()
        }
        return authResult.user
    }

    suspend fun updateUserProfile(imageBitmap: Bitmap, userName: String) {

        val user = FirebaseAuth.getInstance().currentUser
        val imageRef = FirebaseStorage.getInstance().reference.child("profile_images/${user?.uid}/profile_picture")
        val baos = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val dowloadUrl = imageRef.putBytes(baos.toByteArray()).await().storage.downloadUrl.await().toString()
        val profileUpdate = UserProfileChangeRequest.Builder()
                .setDisplayName(userName)
                .setPhotoUri(dowloadUrl.toUri())
                .build()
        user?.updateProfile(profileUpdate)?.await()

    }
}