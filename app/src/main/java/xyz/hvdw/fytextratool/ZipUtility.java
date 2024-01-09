package xyz.hvdw.fytextratool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;

import java.io.File;

import xyz.hvdw.fytextratool.MyGettersSetters;

public class ZipUtility {

    public static void zipFiles(String sourcePath, String outputPath) {
        try {
            // Specify the zip command
            String command = "zip-arm -r -v -y -0 --password 048a02243bb74474b25233bda3cd02f8 " + outputPath + " " + sourcePath;

            // Execute the command
            Process process = Runtime.getRuntime().exec(command);

            // Get the output (if needed)
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // Wait for the process to complete
            int exitCode = process.waitFor();
            System.out.println("Exit Code: " + exitCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void zipFoldersWithoutCompressionWithPassword(String[] sourceFolderPaths, String destinationZipPath, String password) {
        // Create a ZipFile object with the destination path
        ZipFile zipFile = new ZipFile(destinationZipPath, password.toCharArray());
        for (String folderPath : sourceFolderPaths) {
            try {
                //ZipFile zipFile = new ZipFile("/storage/emulated/0/BACKUP/AllAppUpdate.bin", password.toCharArray());

                // Add each folder to the zip file without compression
                //File sourceFolder = new File(folderPath);
                //File[] files = sourceFolder.listFiles();

            /*if (files != null) {
                for (File file : files) {
                    zipFile.addFile(file, createZipParameters(file, password));
                }
            }*/
                zipFile.addFolder(new File(folderPath));
            } catch(ZipException e){
                Logger.logToFile("zipFoldersWithoutCompressionWithPassword for folder " + folderPath + " gives error " + e.toString());
                e.printStackTrace();
            }
        }
    }
    private static ZipParameters createZipParameters(File file, String password) {
        // Create ZipParameters with no compression and set a password
        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(CompressionMethod.STORE);
        parameters.setEncryptFiles(true);
        //parameters.setEncryptFiles(false);
        parameters.setEncryptionMethod(EncryptionMethod.ZIP_STANDARD);
        //parameters.setPassword(password);
        return parameters;
    }
}
