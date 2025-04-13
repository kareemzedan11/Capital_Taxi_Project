package com.example.capital_taxi.data.utils

import android.content.Context
import android.media.MediaRecorder
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AudioRecorder(private val context: Context) {
    private var recorder: MediaRecorder? = null
    private lateinit var outputFile: File

    fun startRecording() {
        // تحديد الطابع الزمني (timestamp) للمساعدة في تمييز الملفات
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())

        // تحديد المسار لتخزين التسجيل، سواء كان جهاز موبايل أو كمبيوتر
        val storageDir = if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            // إذا كان التطبيق يعمل على جهاز موبايل، يتم استخدام التخزين الخارجي
            context.getExternalFilesDir(Environment.DIRECTORY_MUSIC) ?:
            context.filesDir.resolve("recordings").also { it.mkdirs() }
        } else {
            // في حالة الكمبيوتر، يمكنك تحديد مسار محلي خاص
            File("C:/Users/Kareem Zedan/Documents").apply { mkdirs() }
        }

        outputFile = File(storageDir, "recording_$timestamp.3gp").apply {
            createNewFile() // إنشاء الملف إذا لم يكن موجودًا
        }

        // بدء عملية التسجيل
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(outputFile.absolutePath)

            try {
                prepare()
                start()
                Log.d("AudioRecorder", "Recording started to ${outputFile.absolutePath}")
            } catch (e: Exception) {
                Log.e("AudioRecorder", "Recording failed", e)
                outputFile.delete() // تنظيف الملف في حالة حدوث خطأ
                throw e
            }
        }
    }

    fun stopRecording(): File {
        recorder?.apply {
            try {
                stop()
            } finally {
                release()
            }
        }
        recorder = null

        if (!outputFile.exists() || outputFile.length() == 0L) {
            throw IllegalStateException("Recording file is empty or missing")
        }

        return outputFile
    }
}
