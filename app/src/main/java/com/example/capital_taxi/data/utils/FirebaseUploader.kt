package com.example.capital_taxi.data.utils


import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
class FirebaseUploader(private val context: Context) {
    private val storage = FirebaseStorage.getInstance().apply {
        // زيادة مهلات الانتظار
        maxUploadRetryTimeMillis = 600000 // 10 دقائق
        maxOperationRetryTimeMillis = 600000
    }

    private val storageRef = storage.reference

    fun uploadAudioFile(
        file: File,
        onProgress: (Double) -> Unit,
        callback: (Result<Uri>) -> Unit
    ) {
        if (!file.exists()) {
            callback(Result.failure(FileNotFoundException("الملف غير موجود")))
            return
        }

        try {
            // 1. تحضير الـ Metadata
            val metadata = StorageMetadata.Builder()
                .setContentType("audio/3gpp")
                .setCustomMetadata("originalName", file.name)
                .build()

            // 2. إنشاء مرجع فريد للملف
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.getDefault()).format(Date())
            val audioRef = storageRef.child("recordings/audio_$timestamp.3gp")

            // 3. رفع الملف باستخدام putFile
            val fileUri = Uri.fromFile(file)
            val uploadTask = audioRef.putFile(fileUri, metadata)

            uploadTask.addOnProgressListener { taskSnapshot ->
                val progress = (100.0 * taskSnapshot.bytesTransferred) / file.length().toDouble()
                onProgress(progress.coerceIn(0.0, 100.0))
                Log.d("Upload", "التقدم: ${"%.2f".format(progress)}%")
            }.addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                    callback(Result.success(uri))
                }
            }.addOnFailureListener { e ->
                callback(Result.failure(Exception("فشل الرفع: ${e.message}")))
            }

        } catch (e: Exception) {
            callback(Result.failure(Exception("خطأ في التحضير للرفع: ${e.message}")))
        }
    }



    /**
     * Validates the audio file before upload
     */
    private fun validateFile(file: File): Boolean {
        return when {
            !file.exists() -> {
                Log.e(TAG, "File does not exist: ${file.absolutePath}")
                false
            }
            file.length() == 0L -> {
                Log.e(TAG, "File is empty")
                false
            }
            !file.canRead() -> {
                Log.e(TAG, "Cannot read file")
                false
            }
            else -> true
        }
    }

    /**
     * Deletes a file from Firebase Storage
     */
    fun deleteAudioFile(downloadUrl: String, callback: (Result<Boolean>) -> Unit) {
        val storageRef = storage.getReferenceFromUrl(downloadUrl)
        storageRef.delete()
            .addOnSuccessListener {
                Log.d(TAG, "File deleted successfully")
                callback(Result.success(true))
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to delete file", e)
                callback(Result.failure(e))
            }
    }
}