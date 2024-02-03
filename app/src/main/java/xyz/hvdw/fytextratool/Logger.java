package xyz.hvdw.fytextratool;

import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import xyz.hvdw.fytextratool.MyGettersSetters;

public class Logger {

    private static final String TAG = "Fyt Extra Tool";

    public static void logToFile(String message) {
        Log.d(TAG, message);

        // Append the log message to a file
        try (FileWriter writer = new FileWriter(new File(MyGettersSetters.getLogFileName()), true)) {
            writer.append(Utils.getDateTimeForLog() + " : " + message).append("\n");
        } catch (IOException e) {
            e.printStackTrace();
            //logToFile(e.toString());
        }
    }


}
