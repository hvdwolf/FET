package xyz.hvdw.fytextratool;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CacheFileUtils {
    public static void copyAssetFileToCache(Context context, String logFileName, String assetFileName) {
        // Get the cache directory
        File cacheDir = context.getCacheDir();

        // Create a File object representing the destination file in the cache directory
        File destinationFile = new File(cacheDir, assetFileName);

        try (
                InputStream inputStream = context.getAssets().open(assetFileName);
                OutputStream outputStream = new FileOutputStream(destinationFile)
        ) {
            // Copy the file from assets to the cache directory
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Logger.logToFile(e.toString());
        }
    }

    public static void writeToCache(Context context, String fileName, String data) {
        File cacheDir = context.getCacheDir();
        File file = new File(cacheDir, fileName);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(data.getBytes());
            // Do additional operations if needed
        } catch (IOException e) {
            e.printStackTrace();
            Logger.logToFile(e.toString());
        }
    }

    public static String saveStringToCacheFile(File filename, String data) {

        String result = "";

        FileWriter writer = null;
        try {
            writer = new FileWriter(filename);
            writer.write(data);
            result = "writing config.txt to cache";
        } catch (IOException e) {
            e.printStackTrace();
            Logger.logToFile("error writing to cache: " + e.toString());
            result = e.toString();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                    result += "\nwritten to cache";
                    Logger.logToFile("config.txt written to cache");
                }
            } catch (IOException e) {
                e.printStackTrace();
                Logger.logToFile("error writing to cache: " + e.toString());
                result = e.toString();
            }
        }
        return result;
    }


}
