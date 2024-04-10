package xyz.hvdw.fytextratool;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GitHubTextFileReader {

    private Context context;
    int webVersion = 0;
    int localVersion = 0;
    String popupText = "";
    String whichIsNewer = "";

    public GitHubTextFileReader(Context context) {
        this.context = context;
    }

    public void readTextFileFromGitHub(String url) {
        new ReadTextFileTask().execute(url);
    }

    private class ReadTextFileTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            StringBuilder result = new StringBuilder();
            HttpURLConnection urlConnection = null;
            try {
                URL githubUrl = new URL(url);
                urlConnection = (HttpURLConnection) githubUrl.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line).append("\n");
                }
                return result.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    PackageManager manager = context.getPackageManager();
                    PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
                    localVersion = info.versionCode; // Versioncode gives 129, versionName gives 1.2.9
                    String cleanResult = result.replaceAll("\\s+", "");
                    try {
                        webVersion = Integer.parseInt(cleanResult);
                    } catch (NumberFormatException e) {
                        Logger.logToFile("webVersion can't be converted to int: " + e.toString());
                        throw new RuntimeException(e);
                    }
                    if (webVersion > localVersion) {
                        Logger.logToFile("The webversion is newer:" + cleanResult);
                        popupText = "There is a new version available:  " + getFormattedString(cleanResult);
                        popupText += "\nOpen the download post in the XDA forum?";
                        whichIsNewer = "web";
                    } else {
                        popupText = "The local version is already the latest version: " + getFormattedString(String.valueOf(localVersion));
                        whichIsNewer = "local";
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    Logger.logToFile("packagemenager error " + e.toString());
                    throw new RuntimeException(e);
                }

                //displayPopup(String.valueOf(localVersion));
                displayPopup(popupText, whichIsNewer);
            } else {
                // Handle error case
            }
        }
    }

    private static String getFormattedString(String versionTxt) {
        StringBuilder formattedString = new StringBuilder();

        // Iterate through the characters of the input string
        for (int i = 0; i < versionTxt.length(); i++) {
            char c = versionTxt.charAt(i);

            // Append the character to the formatted string
            formattedString.append(c);

            // If it's not the last character, append a dot
            if (i < versionTxt.length() - 1) {
                formattedString.append(".");
            }
        }
        String formattedVersion = formattedString.toString();
        return formattedVersion;
    }

    private void displayPopup(String text, String whichIsNewer) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (whichIsNewer.contains("web")) {
            builder.setMessage(text)
                    .setTitle("Github Text File Content")
                    .setPositiveButton("OK", null)
                    .setNegativeButton("Cancel", null);
        } else {
            builder.setMessage(text)
                    .setTitle("Github Text File Content")
                    .setPositiveButton("OK", null);
        }
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
