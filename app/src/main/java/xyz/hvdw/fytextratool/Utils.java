package xyz.hvdw.fytextratool;

import static androidx.core.content.ContextCompat.getSystemService;
import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.UiModeManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.hardware.usb.UsbManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Utils {
    static String[] REQUIRED_PERMISSIONS = {
            android.Manifest.permission.BLUETOOTH,
            android.Manifest.permission.BLUETOOTH_ADMIN,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_SETTINGS,
            android.Manifest.permission.WRITE_SECURE_SETTINGS};

    private static final int REQUEST_CODE = 123;
    public static void checkPermissions(Context context) {
        if (Utils.arePermissionsGranted(context, REQUIRED_PERMISSIONS)) {
            Logger.logToFile("All permissions are granted");
            // Perform your logic here
        } else {
            Logger.logToFile("Request permissions");
            ActivityCompat.requestPermissions((Activity) context, REQUIRED_PERMISSIONS, REQUEST_CODE);
        }
    }

    public static void startBluetoothSettings(Context context) {
        Logger.logToFile("Starting the Bluetooth settings");
        Intent intent = new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
        context.startActivity(intent);
    }

    // we use below one for certain Settings apk activities */
    public static void startExternalAppByActivity(Context context, String appName, String appActivity) {
        Intent intent = new Intent();
        intent.setClassName(appName, appActivity);
        //context.startActivity(intent);
        // Check if we use the correct call and if the app is installed to prevent app crashes
        PackageManager packageManager = context.getPackageManager();
        ResolveInfo resolveInfo = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);

        if (resolveInfo != null) {
            // External app is installed, start the specified activity
            context.startActivity(intent);
        } else {
            // External app is not installed, handle accordingly
            Toast.makeText(context, "The app " + appName + " or the appActivity " + appActivity + " is not installed or not correct" , Toast.LENGTH_SHORT).show();
        }
    }

    // Method to check if multiple permissions are granted
    public static boolean arePermissionsGranted(Context context, String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    // Return false if any of the permissions is not granted
                    return false;
                }
            }
        }
        // Return true if all permissions are granted or if the device is running pre-Marshmallow
        return true;
    }

    public static void showInfoDialog(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Positive button click action (if needed)
                        dialog.dismiss();
                    }
                });

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void showDialogAndCloseApp(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Positive button click action (if needed)
                        dialog.dismiss();
                        ((Activity)context).finish();
                    }
                });

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static Map multiPropReader(Context context, String[] propNames) {
        Map<String, String> propsHashMap = new HashMap<>();
        String returnValue = "";

        for (String propName : propNames){
            returnValue = propReader(context, propName);
            propsHashMap.put(propName, returnValue);
        }
        return propsHashMap;
    }

    public static String getPropFromHashMap(String propName) {
        String propValue = "";
        Map<String, String> props = MyGettersSetters.getPropsHashMap();
        propValue = props.get(propName);

        return propValue;
    }
    public static String propReader(Context context, String propName) {
        Process process = null;
        BufferedReader bufferedReader = null;

        try {
            process = new ProcessBuilder().command("/system/bin/getprop", propName).redirectErrorStream(true).start();
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = bufferedReader.readLine();
            if (line == null){
                line = ""; //prop not set
            }
            //Toast.makeText(context, line, Toast.LENGTH_SHORT).show();
            Logger.logToFile("System Property: " + propName + "=" + line);
            return line;
        } catch (Exception e) {
            Logger.logToFile("Failed to read System Property " + propName);
            return "";
        } finally{
            if (bufferedReader != null){
                try {
                    bufferedReader.close();
                } catch (IOException e) {}
            }
            if (process != null){
                process.destroy();
            }
        }
    }

    public static String getDateTime() {

        Date currentDate = new Date();
        String dateFormat = "yyyyMMdd_HHmmss";
        // Create a SimpleDateFormat object with the specified format and locale
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.getDefault());
        // Format the current date and time as a string
        String formattedDateTime = simpleDateFormat.format(currentDate);
        return formattedDateTime;
    }

    public static String getDateTimeForLog() {

        Date currentDate = new Date();
        String dateFormat = "yyyy-MM-dd HH:mm:ss";
        // Create a SimpleDateFormat object with the specified format and locale
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.getDefault());
        // Format the current date and time as a string
        String formattedDateTime = simpleDateFormat.format(currentDate);
        return formattedDateTime;
    }

    public static void showAboutDialog(Context context, String whichInfo) {
        Map<String, String> propsHashMap = new HashMap<>();
        String dialogTitle = "";
        if (whichInfo.equals("properties")) {
            propsHashMap = MyGettersSetters.getPropsHashMap();
        }

        PackageManager manager = context.getPackageManager();

        // Create a layout for the dialog
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        //layout.getLayoutParams().width = 800;
        //layout.requestLayout();
        layout.setPadding(16, 16, 16, 16);

        // Create a ScrollView
        ScrollView scrollView = new ScrollView(context);
        scrollView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        // Create a LinearLayout for the content inside the ScrollView
        LinearLayout contentLayout = new LinearLayout(context);
        contentLayout.setOrientation(LinearLayout.VERTICAL);

        TextView textView = new TextView(context);
        TableLayout propsTableLayout = new TableLayout(context);


        switch (whichInfo) {
            case "about":
                dialogTitle = context.getString(R.string.btn_about_text);
                // First add some app info
                textView.setText(context.getString(R.string.displayed_name) + ": " + context.getString(R.string.app_name) + "\n");
                textView.append(context.getString(R.string.author) + ": " + context.getString(R.string.programmer) + "\n");
                textView.append(context.getString(R.string.website) + ": https:/github.com/hvdwolf/FET \n\n");

                try {
                    PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
                    textView.append(context.getString(R.string.version) + ": " + info.versionName + "\n");
                    textView.append(context.getString(R.string.pkg_name) + ": " + info.packageName + "\n\n");
                    textView.append(context.getString(R.string.adroid_root) + "\n\n");

                } catch (PackageManager.NameNotFoundException e) {
                    throw new RuntimeException(e);
                }
                contentLayout.addView(textView);
                break;

            case "properties":
                dialogTitle = context.getString(R.string.btn_important_props);
                textView.setText(context.getString(R.string.btn_important_props) + ":\n\n");

                for (Map.Entry<String, String> entry : propsHashMap.entrySet()) {
                    TableRow row = new TableRow(context);
                    TextView textViewKey = new TextView(context);
                    String key = entry.getKey();
                    textViewKey.setText(key);
                    row.addView(textViewKey); // First Key column in row
                    String value = entry.getValue();
                    if (value.equals("")) {
                        value = context.getString(R.string.not_applicable_abbreviation);
                    }
                    TextView textviewValue = new TextView(context);
                    textviewValue.setText(value);
                    row.addView(textviewValue); // Add 2nd column value ro row
                    propsTableLayout.addView(row); // Add the row to the table
                    //textView.append(context.getString(R.string.the_property) + ": " + key + ", " + context.getString(R.string.hashmap_value) + value + "\n");
                    //textView.append(key + "\t:\t" + value + "\n");
                    //Logger.logToFile("properties: Key: " + key + ", Value: " + value);
                }
                contentLayout.addView(propsTableLayout);
                break;

            case "/oem/app/config.txt":
                String longText;
                dialogTitle = FileUtils.extractFileName(whichInfo);
                File textFile = new File(whichInfo);
                if (textFile.exists()) {
                    longText = FileUtils.readFileToString(textFile);
                } else {
                    longText = context.getString(R.string.config_txt_not_found);
                }
                textView.setText(longText);
                contentLayout.addView(textView);
                break;
        }

        // Add contentLayout to the ScrollView
        scrollView.addView(contentLayout);
        // Add ScrollView to the main layout
        layout.addView(scrollView);
        // Build the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(layout)
                .setTitle(dialogTitle)
                .setPositiveButton(context.getString(R.string.btn_ok), (dialog, which) -> {
                    // Handle OK button click if needed
                });

        // Show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getWindow().setLayout(
                (int) (context.getResources().getDisplayMetrics().widthPixels * 0.9),
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
    }


    public static void setNightMode(Context context, boolean isNightModeEnabled) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Use UiModeManager for devices running Android 10 (API level 29) and above
            UiModeManager uiModeManager = context.getSystemService(UiModeManager.class);
            if (uiModeManager != null) {
                uiModeManager.setNightMode(isNightModeEnabled ? UiModeManager.MODE_NIGHT_YES : UiModeManager.MODE_NIGHT_NO);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Use Configuration for devices running Android 5.0 (API level 21) and above
            Configuration configuration = context.getResources().getConfiguration();
            configuration.uiMode &= ~Configuration.UI_MODE_NIGHT_MASK; // Clear the existing night mode
            configuration.uiMode |= isNightModeEnabled ? Configuration.UI_MODE_NIGHT_YES : Configuration.UI_MODE_NIGHT_NO;
            context.getResources().updateConfiguration(configuration, null);
        }

    }


}
