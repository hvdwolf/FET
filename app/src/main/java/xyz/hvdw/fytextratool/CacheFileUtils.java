package xyz.hvdw.fytextratool;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
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

}
