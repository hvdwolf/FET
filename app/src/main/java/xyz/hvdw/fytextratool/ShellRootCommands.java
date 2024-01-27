package xyz.hvdw.fytextratool;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.Socket;

public class ShellRootCommands {

    //Basic version */
    public static void executeRootCommand(String rootCommand) {
        try {
            // Get the runtime and execute 'su' to obtain root shell
            Process process = Runtime.getRuntime().exec("su");

            // Read the output of the process (optional)
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder output = new StringBuilder();

            // Example: Mounting /system as read-write
            //rootCommand = "mount -o remount,rw /system\n";

            // Write the command to the shell
            process.getOutputStream().write(rootCommand.getBytes());
            process.getOutputStream().write("exit\n".getBytes());
            process.getOutputStream().flush();
            process.getOutputStream().close();

            // Print the output (optional)
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            Logger.logToFile("Output: " + output.toString());

            // Wait for the process to finish
            int exitCode = process.waitFor();
            Logger.logToFile("Exit Code: " + exitCode);

        } catch (IOException | InterruptedException e) {
            Logger.logToFile(e.toString());
            e.printStackTrace();
        }
    }

    /*  Copied from https://stackoverflow.com/questions/20932102/execute-shell-command-from-android/26654728
    from the code of CarloCannas
*/
    public static String shellExec(String... strings) {
        String res = "";
        DataOutputStream outputStream = null;
        InputStream response = null;
        try {
            Process sh = Runtime.getRuntime().exec("sh");
            outputStream = new DataOutputStream(sh.getOutputStream());
            response = sh.getInputStream();

            for (String s : strings) {
                s = s.trim();
                outputStream.writeBytes(s + "\n");
                outputStream.flush();
            }

            outputStream.writeBytes("exit\n");
            outputStream.flush();
            try {
                sh.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            res = readFully(response);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeSilently(outputStream, response);
        }
        return res;
    }


    public static String rootExec(String... strings) {
        String res = "";
        DataOutputStream outputStream = null;
        InputStream response = null;
        try {
            Process su = Runtime.getRuntime().exec("su");
            outputStream = new DataOutputStream(su.getOutputStream());
            response = su.getInputStream();

            for (String s : strings) {
                s = s.trim();
                outputStream.writeBytes(s + "\n");
                outputStream.flush();
            }

            outputStream.writeBytes("exit\n");
            outputStream.flush();
            try {
                su.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            res = readFully(response);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeSilently(outputStream, response);
        }
        return res;
    }

    public static String readFully(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = is.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }
        return baos.toString("UTF-8");
    }


    public static void closeSilently(Object... xs) {
        // Note: on Android API levels prior to 19 Socket does not implement Closeable
        for (Object x : xs) {
            if (x != null) {
                try {
                    //Log.d("closing: "+x);
                    if (x instanceof Closeable) {
                        ((Closeable)x).close();
                    } else if (x instanceof Socket) {
                        ((Socket)x).close();
                    } else if (x instanceof DatagramSocket) {
                        ((DatagramSocket)x).close();
                    } else {
                        //Log.d("cannot close: "+x);
                        throw new RuntimeException("cannot close "+x);
                    }
                } catch (Throwable e) {
                    Logger.logToFile(e.getMessage().toString());
                }
            }
        }
    }
    /* end of shell and su call functions/methods */

}
