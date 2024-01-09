package xyz.hvdw.fytextratool;

import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatDelegate;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import p32929.easypasscodelock.Utils.EasyLock;

//import p32929.easypasscodelock.Utils.LockscreenHandler;

public class MainActivity extends AppCompatActivity {
    String[] REQUIRED_PERMISSIONS = {
            android.Manifest.permission.BLUETOOTH,
            android.Manifest.permission.BLUETOOTH_ADMIN,
            android.Manifest.permission.BLUETOOTH_CONNECT,
            android.Manifest.permission.BLUETOOTH_SCAN,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_SETTINGS,
            android.Manifest.permission.WRITE_SECURE_SETTINGS};
    String[] propNames = {"ro.board.platform", "ro.build.version.sdk", "ro.build.version.release", "ro.fyt.uiid", "sys.fyt.bluetooth_type", "sys.fyt.front_video_ic", "ro.build.fytid",
            "persist.sys.fetkernel", "ro.fota.platform", "sys.fyt.cvbs.height", "sys.fyt.cvbs.width", "persist.sys.syu.audio", "ro.system.build.date", "ro.lsec.app.version",
            "persist.sys.syu.audio", "persist.syu.camera360", "persist.fyt.fm.name", "persist.fyt.zh_frontview_enable" };
    Map<String, String> propsHashMap = new HashMap<>();
    // Define a constant for the permission request
    private static final int REQUEST_CODE = 123;
    private static final String TAG = "Fyt Extra Tool";
    private static final String BASE_LOG_FILE_NAME = "fyt_extra_tool.txt";
    private static final String PREF_MODE_KEY = "app_mode";
    private static final String LIGHT_MODE = "light";
    private static final String DARK_MODE = "dark";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context context;

        String logFileName = createLogFile();
        MyGettersSetters.setLogFileName(logFileName);
        Log.i(TAG, "Start of program at " + Utils.getDateTime());
        Logger.logToFile("Start of program at " + Utils.getDateTime());
        // Is this a FYT and as second test: on Android 10 SDK 29
        if (checkIsFYT()) {
            //If it is a FYT we can continue and do some further checks
            Utils.checkPermissions(this);
            //String fetValueForKey2 = propsHashMap.get("persist.sys.fetkernel");
            //EasyLock.checkPassword(this);
            //Utils.showAboutDialog(this);
        }


        EasyLock.forgotPassword(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Clicked on forgot password", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void dispAboutInfo(View view) {
        //Intent intent = new Intent(this, AboutProperties.class);
        //startActivity(intent);
        Utils.showAboutDialog(this, "about");
    }
    public void dispImportantProperties(View view) {
        //Intent intent = new Intent(this, AboutProperties.class);
        //startActivity(intent);
        Utils.showAboutDialog(this, "properties");
    }


    /* Below 4 methods are for the light/dark app toggle mode */
    public void toggleAppMode(View view) {
        int currentMode = getSavedAppMode();
        if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) {
            Logger.logToFile("App theme currently set to night mode. Set to day mode.");
            saveAppMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            Logger.logToFile("App theme currently set to day mode. Set to night mode.");
            saveAppMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        // Apply the new app mode
        applyAppMode(getSavedAppMode());
    }
    private int getSavedAppMode() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        String mode = preferences.getString(PREF_MODE_KEY, LIGHT_MODE);
        return mode.equals(LIGHT_MODE) ? AppCompatDelegate.MODE_NIGHT_NO : AppCompatDelegate.MODE_NIGHT_YES;
    }

    private void saveAppMode(int mode) {
        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        editor.putString(PREF_MODE_KEY, mode == AppCompatDelegate.MODE_NIGHT_NO ? LIGHT_MODE : DARK_MODE);
        editor.apply();
    }

    private void applyAppMode(int mode) {
        Logger.logToFile("App theme switching to " + String.valueOf(mode));
        AppCompatDelegate.setDefaultNightMode(mode);
        Logger.logToFile("Recreate the activity to apply the new theme");
        recreate();
    }
    /* Above 4 methods  are for the light/dark app toggle mode */

    /* Below method switches the unit to night or day mode */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void switchDeviceToDayNightMode(View view) {
        Boolean switchToNight = true;
        UiModeManager uiModeManager = (UiModeManager) getSystemService(Context.UI_MODE_SERVICE);

        if (uiModeManager != null) {
            // Check if the night mode is not already active
            if ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                    != Configuration.UI_MODE_NIGHT_YES) {
                Logger.logToFile("Currently in day mode, so switch to Night mode");
                uiModeManager.setNightMode(UiModeManager.MODE_NIGHT_YES);
                switchToNight = true;
            } else {
                Logger.logToFile("Currently in night mode, so switch to day mode");
                uiModeManager.setNightMode(UiModeManager.MODE_NIGHT_NO);
                switchToNight = false;
            }

            if (switchToNight) {
                // Inform the user that the mode has been changed to night
                Toast.makeText(this, "Switched to Night Mode", Toast.LENGTH_SHORT).show();
            } else {
                // The device is switched to day mode
                Toast.makeText(this, "Switched to Day Mode", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void checkPermissions() {
        if (Utils.arePermissionsGranted(this, REQUIRED_PERMISSIONS)) {
            Logger.logToFile("All permissions are granted");
            // Perform your logic here
        } else {
            Logger.logToFile("Request permissions");
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE);
        }
    }


    @Override
    public void onBackPressed() {
        // Disable the back button
        //Toast.makeText(this, "Back button disabled", Toast.LENGTH_SHORT).show();
        //Log.i("Back button disabled");
        // Next line to completely ignore the back button
        super.onBackPressed();
    }

    /*
    @Override
    protected void onUserLeaveHint() {
        // Disable the home button
        //Toast.makeText(this, "Home button disabled", Toast.LENGTH_SHORT).show();
        //Log.i("Home button disabled");
    } */


    public void setPass(View view) {
        EasyLock.setPassword(this, TestActivity.class);
    }

    public void changePass(View view) {
        EasyLock.changePassword(this, TestActivity.class);
    }

    public void disable(View view) {
        EasyLock.disablePassword(this, TestActivity.class);
    }

    public void checkPass(View view) {
        EasyLock.checkPassword(this);
    }

    public void exitApp(View view) {
        // Call finish() to exit the program
        finish();
    }

    public void openBluetoothSettings(View view) {
        // Start Bluetooth settings
        Utils.startBluetoothSettings(this);
    }
    public void systemInfo(View view) {
        Logger.logToFile("Starting the Equipment Information Activity");
        Utils.startExternalAppByActivity(this, "com.android.settings", "com.android.settings.Settings$EquipmentInformationActivity");
    }
    public void deviceInfo(View view) {
        Logger.logToFile("Starting the Device Information Activity (About the unit)");
        Utils.startExternalAppByActivity(this,"com.android.settings", "com.android.settings.Settings$DeviceInfoActivity");
    }
    public void deepDeviceInfo(View view) {
        Logger.logToFile("Starting the deeper Device Information (System Info))");
        Utils.startExternalAppByActivity(this,"com.android.settings", "com.android.settings.deviceinfo.aboutphone.MyDeviceInfoFragment");
    }
    public void syusettings(View view) {
        Logger.logToFile("Starting the syu settings");
        final Intent launchIntentForPackage = getPackageManager().getLaunchIntentForPackage("com.syu.settings");
        startActivity( launchIntentForPackage );
    }
    public void engineermode(View view) {
        Logger.logToFile("Starting the engineer mode");
        Utils.startExternalAppByActivity(this,"com.sprd.engineermode", "com.sprd.engineermode.EngineerModeActivity");
    }

    /* Below is currently not used due to filepath issues */
    public void zipAllAppUpdateBin(View view) {

        String message = getString(R.string.fytbackup_first_sentence) + "\n" +
                "- AllAppUpdate.bin\n" +
                "- config.txt\n" +
                "- updatecfg.txt\n";
        message += getBackupMessage();

        // Call the zipFiles method from ZipUtility
        //ZipUtility.zipFiles(sourcePath, outputPath);
        // Copy file "example.txt" from assets to cache
        Runnable r = new Runnable() {
            @Override
            public void run() {
                Logger.logToFile("Copying 7zzs to /cache and set perimissions to 755");
                FileUtils.copyAssetFileToCache(MainActivity.this, "7zzs");
                FileUtils.changeFilePermissions(new File("/cache/7zzs"), "755");
                Logger.logToFile("Copying 7862lsec.sh to /cache and set perimissions to 755");
                FileUtils.copyAssetFileToCache(MainActivity.this, "7862lsec.sh");
                FileUtils.changeFilePermissions(new File("/cache/7862lsec.sh"), "755");
                // Now remove and recreate folder BACKUP
                Logger.logToFile("Removing and recreating folder BACKUP");
                FileUtils.removeAndRecreateFolder("BACKUP");
            }
        };
        Handler h = new Handler();
        h.postDelayed(r,1000);

        ShellScriptRunner.runShellScriptFromCache("7862lsec.sh");

        Utils.showInfoDialog(this,getString(R.string.backup_finished), message);
        // Cleanup
        FileUtils.removeFile("/cache/7862lsec.sh");
        FileUtils.removeFile("/cache/7zzs");
    }

    public void scriptedZipAllAppUpdateBin(View view) {
        Logger.logToFile("Using the scripted AllAppUpdateBin method");
        String platformScript = "";
        String message = getString(R.string.fytbackup_first_sentence) + "\n" +
                "- AllAppUpdate.bin\n" +
                "- config.txt\n" +
                "- updatecfg.txt\n";

        Utils.showBeforeFlashDialog(this);
        // As our dialog is asynchronous we just wait for 2500 milliseconds before doing something else
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do nothing, just wait for 2.5 seconds
            }
        }, 2500);


        String binary = Utils.prepareForBackup(this, true);

        Map<String, String> fytPlatform = MyGettersSetters.getPropsHashMap();
        if (fytPlatform.get("ro.board.platform").contains("ums512")) {
            String result = FileUtils.copyAssetsFileToExternalStorage(this, platformScript, "lsec_updatesh", platformScript);
            result = FileUtils.copyAssetsFileToExternalStorage(this, binary, ".", binary);
            if (result.equals("")) {
                Logger.logToFile("Copied " + binary + " to external storage");
            } else {
                Logger.logToFile("Failed to copy " + binary + " to external storage");
            }
        }

        Logger.logToFile("Start the RKUpdate service to activate the flashing process");
        final Intent launchIntentForPackage = getPackageManager().getLaunchIntentForPackage("android.rockchip.update.service");
        startActivity( launchIntentForPackage );


    }

    public void zip4jAllAppUpdateBin(View view) throws IOException {

        Logger.logToFile("Using the zip4jAllAppUpdateBin method");
        String[] oemFolderPath = {"/oem/app", "/oem/priv-app", "/oem/vital-app", "/oem/oem", "/oem/360res"};
        String password = "048a02243bb74474b25233bda3cd02f8";

        String done = Utils.showBeforeFlashDialog(this);

        /*
        FileUtils.removeAndRecreateFolder("BACKUP");
        File BackupFolder = new File(Environment.getExternalStorageDirectory(), "BACKUP/AllAppUpdate.bin");
        String BackupFolderPath = FileUtils.fileToString(BackupFolder);
        Logger.logToFile("Created the backup folder " + BackupFolderPath);

        String result = FileUtils.copyAssetsFileToExternalStorage(this, "lsec6315update", "BACKUP", "lsec6315update");
        if (result.equals("")) {
            Logger.logToFile("Copied lsec6315update to the BACKUP folder");
        } else {
            Logger.logToFile("Failed to copy lsec6315update to the BACKUP folder");
        }
        // Copy config.txt
        File inFile = new File("/oem/app/config.txt");
        File outFile = new File("/storage/emulated/0/BACKUP/config.txt");
        try {
            FileUtils.copyFile(inFile, outFile);
            Logger.logToFile("Copied config.txt to the BACKUP folder");
        } catch (IOException e) {
            Logger.logToFile("Failed to copy config.txt to the BACKUP folder with error " + e.toString());
            //throw new RuntimeException(e);
        } */

        String BackupFolderPath = Utils.prepareForBackup(this, false);
        // Zip the AllAppUpdate.bin
        ZipUtility.zipFoldersWithoutCompressionWithPassword(oemFolderPath, BackupFolderPath, password);

        // Tell the user who well we did this ;)
        //Utils.showInfoDialog(this, getString(R.string.backup_finished), getBackupMessage());
    }

    private String getBackupMessage() {
        String message = getString(R.string.fytbackup_first_sentence) + "\n" +
                "- AllAppUpdate.bin\n" +
                "- config.txt\n" +
                "- updatecfg.txt\n";
        Map<String, String> fytPlatform = MyGettersSetters.getPropsHashMap();
        if (fytPlatform.get("ro.board.platform").contains("ums512")) {
            message += "- lsec6315update\n\n\n" +
                    getString(R.string.fytbackup_last_sentence_7862);
        } else {
            message += "- lsec6316update\n\n\n" +
                    getString(R.string.fytbackup_last_sentence_8581);
        }
        return message;
    }
    public Boolean checkIsFYT() {
        //String fytProp = Utils.propReader("ro.build.fytmanufacturer");
        propsHashMap = Utils.multiPropReader(this, propNames);
        MyGettersSetters.setPropsHashMap(propsHashMap);
        Boolean FYT = false;
        //String fytProp = Utils.propReader(this, "ro.board.platform");
        String fytProp = propsHashMap.get("ro.board.platform");
        //String sdkProp = Utils.propReader(this, "ro.build.version.sdk");
        String sdkProp = propsHashMap.get("ro.build.version.sdk");
        String fotaProp = propsHashMap.get("ro.fota.platform");
        Logger.logToFile("ro.board.platform = " + fytProp);
        Logger.logToFile("ro.build.version.sdk = " + sdkProp);
        Logger.logToFile("ro.fota.platform = " + fotaProp);
        //"ro.fota.platform" gives SC7862 or SC8581

        ///////////// SET TO FALSE BEFORE RELEASE TO FYT /////////////
        Boolean TEST = false;
        if (TEST) { // For testing on my phone
            FYT = true;
        } else { // When using on a unit to really test whether it is a FYT
            if ((fytProp.contains("ums512")) || (fytProp.contains("sp9863a"))) {
                //It is a FYT, but we still want to know whether it is at the right SDK level
                if (!sdkProp.equals("29")) {
                    Utils.showDialogAndCloseApp(this, getString(R.string.correct_fyt_wrong_sdk), getString(R.string.correct_fyt_wrong_sdk_message));
                }
                // In backup we can test for Teyes
                FYT = true;
            } else {
                //showToastAndCloseApp("This is not a FYT unit. The app will be closed.", 3500);
                Utils.showDialogAndCloseApp(this, getString(R.string.not_a_fyt), getString(R.string.not_a_fyt_message));
            }
        }
        return FYT;
    }

    private void showToastAndCloseApp(String message, int delayMillis) {
        // Show a Toast
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        // Close the app after a delay
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                finishAffinity(); // Close all activities in the task
            }
        }, delayMillis);
    }

    // We create the logfile here in the MainActivity to be able to use it from here in all classes
    public static String createLogFile() {
        // Get the external storage directory
        File externalStorage = android.os.Environment.getExternalStorageDirectory();
        String curDateTime = Utils.getDateTime();
        // Create a File object representing the log file
        return new File(externalStorage, curDateTime + "_" + BASE_LOG_FILE_NAME).getAbsolutePath();
    }

}
