package xyz.hvdw.fytextratool;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GitHubCheckVersion {

    private Context context;
    int webVersion = 0;
    int localVersion = 0;
    String popupText = "";
    String whichIsNewer = "";
    private static long downloadID;
    String thisAction = "";

    public GitHubCheckVersion(Context context) {
        this.context = context;
    }

    public void readTextFileFromGitHub(String url, String whichAction) {
        thisAction = whichAction;
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
                Logger.logToFile("Can't connect to github to check. No internet");
                Utils.showInfoDialog(context, context.getString(R.string.version_check_no_access_github),
                        ( context.getString(R.string.version_check_no_access_github) + " " + context.getString(R.string.version_check_have_internet_text) ));
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
            String formattedVersion = "";
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
                    if (webVersion != 0) {
                        if (webVersion > localVersion) {
                            Logger.logToFile("The webversion is newer:" + cleanResult);
                            popupText = context.getString(R.string.version_check_new_version) + " " + getFormattedString(cleanResult);
                            formattedVersion = getFormattedString(cleanResult);
                            popupText += "\n" + context.getString(R.string.version_check_ask_what_to_do);
                            whichIsNewer = "web";
                        } else {
                                popupText = context.getString(R.string.version_check_no_new_version) + " " + getFormattedString(String.valueOf(localVersion));
                                formattedVersion = getFormattedString(String.valueOf(localVersion));
                                whichIsNewer = "local";
                        }
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    Logger.logToFile("packagemenager error " + e.toString());
                    throw new RuntimeException(e);
                }

                //displayPopup(String.valueOf(localVersion));
                if (whichIsNewer.contains("web")) {
                    // Always display
                    displayPopup(popupText, whichIsNewer, formattedVersion);
                } else { //so local is newer; Only show when called from menu
                    if (thisAction.contains("menuCheck")) {
                        displayPopup(popupText, whichIsNewer, formattedVersion);
                    }
                }
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

    private void displayPopup(String text, String whichIsNewer, String formattedVersion) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(text);
        builder.setTitle(context.getString(R.string.version_check_popup_title));
        if (whichIsNewer.contains("web")) {
            builder.setPositiveButton(context.getString(R.string.btn_download_apk), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //registerReceiver(downloadReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                    downloadApk(context, formattedVersion);
                    dialog.dismiss(); // Close the dialog
                    // Call a method or perform an action
                }
            });
            builder.setNeutralButton(context.getString(R.string.btn_open_xda), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    openXDA(context);
                }
            });
            builder.setNegativeButton(context.getString(R.string.btn_cancel), null);
        } else {
            builder.setPositiveButton(context.getString(R.string.btn_ok), null);
        }
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void openXDA(Context context) {
        // Create an Intent with ACTION_VIEW and the URL
        String url = "https://xdaforums.com/t/fet-fyt-extra-tool.4653315/#post-89303107";
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            Utils.showInfoDialog(context, context.getString(R.string.version_check_no_access_xda),
                    context.getString(R.string.version_check_no_access_xda) + " " + context.getString(R.string.version_check_have_internet_text) );
            throw new RuntimeException(e);
        }
    }

    private static void downloadApk(Context context, String releaseVersion) {
        String baseLink = "https://github.com/hvdwolf/FET/releases/download/";
        String baseApk = "xyz.hvdw.fytextratool.apk";
        String releaseUrl = baseLink + releaseVersion + "/" + baseApk;
        //Utils.showInfoDialog(context, "apk path", "path is: " + releaseUrl);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(releaseUrl));
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, baseApk);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setTitle(baseApk);

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadManager != null) {
            downloadID = downloadManager.enqueue(request);
        }
    }

    /*private final BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (id == downloadID) {
                // Download complete
                Logger.logToFile("Download of xyz.hvdw.fetextratool.apk complete");
                // Show popup
                Toast.makeText(this, "Download of xyz.hvdw.fetextratool.apk complete", Toast.LENGTH_SHORT).show();
            }
        }
    }; */


}
