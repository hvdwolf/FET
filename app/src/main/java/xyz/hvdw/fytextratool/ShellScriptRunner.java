package xyz.hvdw.fytextratool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ShellScriptRunner {

    public static void runScript(String bashScript) {
        // First copy from our assets
        try {
            // Specify the command to execute the Bash script
            String command = "sh " + bashScript;

            // Execute the command
            Process process = Runtime.getRuntime().exec(command);

            // Read the output (if needed)
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                Logger.logToFile(line);
            }

            // Wait for the process to complete
            int exitCode = process.waitFor();
            System.out.println("Exit Code: " + exitCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            Logger.logToFile(e.toString());
        }
        //return
    }

    public static void runShellScriptFromCache(String scriptName) {
        try {
            // Specify the path to your shell script in the app's internal cache directory
            String scriptPath = "/data/data/xyz.hvdw.gytextratool/cache/";

            // Specify the command to execute the shell script
            String command = "sh " + scriptPath + scriptName;

            // Execute the command
            Process process = Runtime.getRuntime().exec(command);

            // Read the output (if needed)
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                Logger.logToFile(line);
            }

            // Wait for the process to complete
            int exitCode = process.waitFor();
            System.out.println("Exit Code: " + exitCode);
            Logger.logToFile("Exit Code: " + exitCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            Logger.logToFile(e.toString());
        }
    }
}
