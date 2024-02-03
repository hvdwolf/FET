package xyz.hvdw.fytextratool;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class FileUtils {

    private static final String TAG = "Fyt Extra Tool";
    public static void copyAssetFileToCache(Context context, String assetFileName) {
        InputStream in = null;
        OutputStream out = null;
        try {
            // Open the asset file
            in = context.getAssets().open(assetFileName);

            // Create an output file in the cache directory
            File outFile = new File(context.getCacheDir(), assetFileName);
            out = new FileOutputStream(outFile);

            // Copy the content from the input stream to the output stream
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }

            // Close the streams
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            Logger.logToFile(e.toString());
        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
            } catch (IOException e) {
                e.printStackTrace();
                Logger.logToFile(e.toString());
            }
        }
    }

    public static void changeFilePermissions(File file, String permissions) {
        try {
            // Convert the permission string to an integer
            int permission = Integer.parseInt(permissions, 8);

            // Use the ProcessBuilder to execute the chmod command
            Process process = new ProcessBuilder()
                    .command("chmod", Integer.toOctalString(permission), file.getAbsolutePath())
                    .start();

            // Wait for the process to complete
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                Logger.logToFile("File permissions changed successfully");
            } else {
                Logger.logToFile("Failed to change file permissions");
            }

        } catch (IOException | InterruptedException | NumberFormatException e) {
            e.printStackTrace();
            Logger.logToFile(e.toString());
        }
    }

    /* Below 2 methods copy a file from assets to external storage */
    public static String copyAssetsFileToExternalStorageFolder(Context context, String sourceFileName, String destinationFolder, String destinationFileName) {
        String returnString = "";

        try {
            // Get the internal storage directory
            File internalFile = new File(context.getFilesDir(), sourceFileName);
            //AssetManager assetManager;
            //String[] assetFile = assetManager.list("lsec6315update");
            InputStream inFile = context.getAssets().open(sourceFileName);

            // Get the external storage directory
            File externalStorage = Environment.getExternalStorageDirectory();

            File destinationDirectory = new File(externalStorage, destinationFolder);
            if (!destinationDirectory.exists()) {
                destinationDirectory.mkdirs();
            }

            // Create the destination file on external storage
            File externalFile = new File(destinationDirectory, destinationFileName);
            OutputStream outFile = new FileOutputStream(externalFile);

            // Copy the file
            //copyFile(internalFile, externalFile); //Uses File handlers
            copyFileAsStream(inFile, outFile); // uses Streams

        } catch (IOException e) {
            e.printStackTrace();
            Logger.logToFile(e.toString());
            returnString = e.toString();
        }
        return returnString;
    }

    public static String copyAssetsFileToExternalStorage(Context context, String sourceFileName, String destinationFileName) {
        String returnString = "";
        try {
            // Get the internal storage directory
            File internalFile = new File(context.getFilesDir(), sourceFileName);
            //AssetManager assetManager;
            //String[] assetFile = assetManager.list("lsec6315update");
            InputStream inFile = context.getAssets().open(sourceFileName);

            // Get the external storage directory
            File externalStorage = Environment.getExternalStorageDirectory();
            File externalFile = new File(externalStorage, destinationFileName);

            OutputStream outFile = new FileOutputStream(externalFile);

            // Copy the file
            //copyFile(internalFile, externalFile); //Uses File handlers
            copyFileAsStream(inFile, outFile); // uses Streams

        } catch (IOException e) {
            e.printStackTrace();
            Logger.logToFile(e.toString());
            returnString = e.toString();
        }
        return returnString;
    }




    /* Below method copyFileasStream uses two streams  as input*/
    private static void copyFileAsStream(InputStream inputStream, OutputStream destinationStream) throws IOException {
        try {
            byte[] buffer = new byte[1024];
            int length;

            while ((length = inputStream.read(buffer)) > 0) {
                destinationStream.write(buffer, 0, length);
            }

        } catch (IOException e) {
            e.printStackTrace();
            Logger.logToFile(e.toString());
        }
    }

    /* Below method copyFile is identical to above method copyFileAsStream but uses two File objects as input and converts these to streams */
    public static void copyFile(File sourceFile, File destinationFile) throws IOException {
        try (InputStream in = new FileInputStream(sourceFile);
             OutputStream out = new FileOutputStream(destinationFile)) {

            byte[] buffer = new byte[1024];
            int length;

            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }

        }
    }

    /* Below two methods remove and then recreate the passed folder */
    public static void removeAndRecreateFolder(String folderName) {
        // Get the external storage directory
        File externalStorage = Environment.getExternalStorageDirectory();

        // Create a File object representing the folder to be removed
        File folderToRemove = new File(externalStorage, folderName);

        // Remove the folder and its contents
        deleteRecursive(folderToRemove);

        // Create the folder again
        if (folderToRemove.mkdirs()) {
            // Folder created successfully
            Logger.logToFile("Folder " + folderName + " sucessfully created");
            Log.i(TAG, "Folder " + folderName + " sucessfully created");
        } else {
            Logger.logToFile("Failed to create the folder " + folderName);
            Log.e(TAG, "Failed to create the folder " + folderName);
            // Handle the error accordingly
        }
    }

    public static void optionallyCreateFolder(String folderName) {
        // Get the external storage directory
        File externalStorage = Environment.getExternalStorageDirectory();

        // Create a File object representing the folder to be created if not existing yet
        File folderToCreate = new File(externalStorage, folderName);
        boolean success = true;
        if (!folderToCreate.exists()) {
            success = folderToCreate.mkdirs();
        }
    }


    private static void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }
        // Delete the file or directory
        fileOrDirectory.delete();
    }

    public static void removeFile(String filePath) {
        // Create a File object representing the file to be removed
        File fileToRemove = new File(filePath);

        // Check if the file exists
        if (fileToRemove.exists()) {
            // Attempt to delete the file
            if (fileToRemove.delete()) {
                // File deleted successfully
                Log.i(TAG, "File " + filePath + " successfully deleted.");
            } else {
                // Failed to delete the file
                Log.e(TAG, "Error removing file " + filePath);
            }
        } else {
            // The file does not exist
            // Handle accordingly or log a message
            Log.e(TAG, "Requested file " + filePath + " to be deleted, can not be found");
        }
    }

    public static String readFileToString(File file) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }

            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return e.toString();
        }

        return stringBuilder.toString();
    }

    public static String extractFileName(String filePath) {
        // Find the last occurrence of "/"
        int lastSlashIndex = filePath.lastIndexOf("/");

        // Extract the file name using substring
        if (lastSlashIndex != -1) {
            return filePath.substring(lastSlashIndex + 1);
        } else {
            // No "/" found, return the original path
            return filePath;
        }
    }

    public static boolean areFilesIdentical(File file1, File file2) {
        if (file1.length() != file2.length()) {
            return false; // Files have different sizes, they can't be identical
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            FileInputStream fis1 = new FileInputStream(file1);
            FileInputStream fis2 = new FileInputStream(file2);

            byte[] buffer1 = new byte[8192];
            byte[] buffer2 = new byte[8192];

            int bytesRead1;
            int bytesRead2;

            while ((bytesRead1 = fis1.read(buffer1)) > 0) {
                bytesRead2 = fis2.read(buffer2);
                if (bytesRead1 != bytesRead2) {
                    return false; // Files have different sizes
                }

                if (!MessageDigest.isEqual(buffer1, buffer2)) {
                    return false; // Files have different content
                }

                digest.update(buffer1, 0, bytesRead1);
            }

            fis1.close();
            fis2.close();

            byte[] hash1 = digest.digest();

            // If you want to compare checksums, you can compare hash1 and hash2
            // Otherwise, you can remove this part

            return true; // Files are identical
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false; // Error occurred during comparison
        }
    }
}
