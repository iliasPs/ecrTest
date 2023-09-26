package com.example.ecrtool.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.example.ecrtool.appData.AppData
import com.example.ecrtool.listeners.AppMessenger
import com.example.ecrtool.server.MyEcrServerSingleton
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object Logger : KoinComponent {
    private const val TAG = "Logger"
    private const val FILE_NAME = "log.txt"
    private const val MAX_FILE_AGE_DAYS = 30
    private val appListener = AppData.getMyEcrEftposInit()?.appListener

    fun logToFile(message: String) {
        // Log the message using android.util.Log
        appListener?.sendToApp(message)
        Log.d(TAG, message)

        // Create a file object
        val file = File(getLogFileDirectory(get()), FILE_NAME)

        try {
            // Check if the file has expired
            if (isLogFileExpired(file)) {
                deleteLogFile(file)
            }

            // Create a FileWriter to write to the file
            val writer = FileWriter(file, true) // Append mode

            // Get the current timestamp
            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(Date())

            // Write the formatted log message to the file
            writer.append("[$timestamp]\n$message\n\n")

            // Close the writer
            writer.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun isLogFileExpired(file: File): Boolean {
        if (file.exists()) {
            val lastModified = file.lastModified()
            val currentTime = System.currentTimeMillis()
            val fileAgeMillis = currentTime - lastModified
            val fileAgeDays = fileAgeMillis / (1000 * 60 * 60 * 24) // Milliseconds to days
            return fileAgeDays >= MAX_FILE_AGE_DAYS
        }
        return false
    }

    private fun deleteLogFile(file: File) {
        if (file.exists()) {
            file.delete()
        }
    }

    fun shareLogFile(context: Context) {
        val file = File(getLogFileDirectory(context), FILE_NAME)
        if (file.exists()) {
            val uri = Uri.fromFile(file)

            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            val chooserIntent = Intent.createChooser(intent, "Share Log File")
            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            context.startActivity(chooserIntent)
        } else {
            Log.e(TAG, "Log file does not exist.")
        }
    }

    private fun getLogFileDirectory(context: Context): File {
        // Get the app-specific directory within external storage
        val dir = File(context.getExternalFilesDir(null), "Logs")

        // Create the directory if it doesn't exist
        if (!dir.exists()) {
            dir.mkdirs()
        }

        return dir
    }
}
