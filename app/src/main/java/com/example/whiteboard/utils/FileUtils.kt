package com.example.whiteboard.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.whiteboard.data.model.WhiteBoardData
import com.google.gson.Gson
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FileUtils {
    fun saveWhiteboard(context: Context, data: WhiteBoardData) {
        val json = Gson().toJson(data)
        val fileName = "whiteboard_${timestamp()}.json"
        val dir = context.getExternalFilesDir(null) ?: context.filesDir
        val file = File(dir, fileName)

        try {
            file.writeText(json)
            Log.d("TAG", "saveWhiteboard:${file.absolutePath} ")
            Toast.makeText(context, "Saved to: ${file.absolutePath}", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Error saving: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun timestamp(): String {
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        return sdf.format(Date())
    }


}