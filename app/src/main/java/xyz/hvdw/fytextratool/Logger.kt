package xyz.hvdw.fytextratool

import android.util.Log
import java.io.File
import java.io.FileWriter
import java.io.IOException

object Logger {
    private const val TAG = "Fyt Extra Tool"

    @JvmStatic
    fun logToFile(message: String) {
        Log.d(TAG, message)

        // Append the log message to a file
        try {
            FileWriter(File(MyGettersSetters.getLogFileName()), true).use { writer ->
                writer.append(Utils.getDateTimeForLog() + " : " + message).append("\n")
            }
        } catch (e: IOException) {
            e.printStackTrace()
            // logToFile(e.toString())
        }
    }
}
