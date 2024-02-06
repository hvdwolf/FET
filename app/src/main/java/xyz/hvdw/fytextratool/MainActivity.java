package xyz.hvdw.fytextratool;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatDelegate;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import p32929.easypasscodelock.Utils.EasyLock;


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
            "sys.fyt.platform", "ro.fota.platform", "sys.fyt.cvbs.height", "sys.fyt.cvbs.width", "persist.sys.syu.audio", "ro.system.build.date", "ro.lsec.app.version",
            "persist.sys.syu.audio", "persist.syu.camera360", "persist.fyt.fm.name", "persist.fyt.withrdsfn", "persist.fyt.zh_frontview_enable", "ro.build.fytmanufacturer" };
    Map<String, String> propsHashMap = new HashMap<>();

    ProgressBar mprogressBar;
    File cacheDir;
    // Define a constant for the permission request
    private static final int REQUEST_CODE = 123;
    private static final String TAG = "Fyt Extra Tool";
    private static final String BASE_LOG_FILE_NAME = "fyt_extra_tool.txt";
    private static final String PREF_MODE_KEY = "app_mode";
    private static final String LIGHT_MODE = "light";
    private static final String DARK_MODE = "dark";
    private static final String PREF_SYSTEM_MODE_KEY = "system_mode";
    private static final String DAY_MODE = "day";
    private static final String NIGHT_MODE = "night";
    private Boolean logFileCreated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context context;

        mprogressBar = findViewById(R.id.progressBar);
        // Make it invisible
        mprogressBar.setVisibility(View.INVISIBLE);

        // Make sure it is only executed once, for example after a reconfigure. Like the app light/dark restarts the onCreate
        if (!logFileCreated) {
            String logFileName = createLogFile();
            MyGettersSetters.setLogFileName(logFileName);
            logFileCreated = true;
        }
        Log.i(TAG, "Start of program at " + Utils.getDateTime());
        Logger.logToFile("Start of program at " + Utils.getDateTime());
        // Is this a FYT and as second test: on Android 10 SDK 29
        if (checkIsFYT()) {
            //If it is a FYT we can continue and do some further checks
            Utils.checkPermissions(this);
            // Check app and system modi and set button texts
            textButtonsAppSystem();
            // Check if rooted. If not disable some buttons
            boolean isRooted = CheckIfRooted.isUnitRooted();
            Logger.logToFile("Boolean isRooted is " + Boolean.toString(isRooted));
            boolean isMagiskRooted = CheckIfRooted.isMagiskRooted();
            Logger.logToFile("Boolean isMagiskRooted is " + Boolean.toString(isMagiskRooted));
            if ( !isRooted && !isMagiskRooted) {
                disableRootedButtons();
            }

        }


        EasyLock.forgotPassword(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Clicked on forgot password", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void textButtonsAppSystem() {
        Button appMode = findViewById(R.id.toggleButton);
        Button systemMode = findViewById(R.id.DeviceDayNight);
        Button suSystemMode = findViewById(R.id.suDeviceDayNight);
        int currentMode = getSavedMode("App");
        if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) {
            appMode.setText(R.string.app_theme_light);
        } else {
            appMode.setText(R.string.app_theme_dark);
        }
        currentMode = getSavedMode("System");
        if (currentMode == 1) { //Day
            systemMode.setText(R.string.system_theme_night);
            suSystemMode.setText(R.string.system_theme_night);
        } else {
            systemMode.setText(R.string.system_theme_day);
            suSystemMode.setText(R.string.system_theme_day);
        }
    }

    private void disableRootedButtons() {
        //If not our testVersion
        if (!MyGettersSetters.getTestVersion()) {
            Button androidsystemmode = findViewById(R.id.suDeviceDayNight);
            Button editconfig = findViewById(R.id.btneditconfig);
            Button addBTtoFyt = findViewById(R.id.addbttosettings);
            androidsystemmode.setEnabled(false);
            editconfig.setEnabled(false);
            addBTtoFyt.setEnabled(false);
        }

    }

    public void dispAboutInfo(View view) {
        Utils.showAboutDialog(this, "about");
    }
    public void dispImportantProperties(View view) {
        //Intent intent = new Intent(this, AboutProperties.class);
        //startActivity(intent);
        Utils.showAboutDialog(this, "properties");
    }


    /* Below 4 methods are for the light/dark app toggle mode */
    public void toggleAppMode(View view) {
        int currentMode = getSavedMode("App");
        if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) {
            Logger.logToFile("App theme currently set to night mode. Set to day mode.");
            saveMode("App", AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            Logger.logToFile("App theme currently set to day mode. Set to night mode.");
            saveMode("App", AppCompatDelegate.MODE_NIGHT_YES);
        }
        // Apply the new app mode
        applyAppMode(getSavedMode("App"));
    }
    private int getSavedMode(String whichItem) {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        if (whichItem.equals("App")) {
            String mode = preferences.getString(PREF_MODE_KEY, LIGHT_MODE);
            Logger.logToFile("saved App mode is " + mode);
            return mode.equals(LIGHT_MODE) ? AppCompatDelegate.MODE_NIGHT_NO : AppCompatDelegate.MODE_NIGHT_YES;
        } else {
            String mode = preferences.getString(PREF_SYSTEM_MODE_KEY, DAY_MODE);
            Logger.logToFile("saved System mode is " + mode);
            return mode.equals(DAY_MODE) ? 1 : 2; //DAY = 1; NIGHT = 2
        }
    }

    private void saveMode(String item, int mode) {
        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        if (item.equals("App")) {
            editor.putString(PREF_MODE_KEY, mode == AppCompatDelegate.MODE_NIGHT_NO ? LIGHT_MODE : DARK_MODE);
        } else {
            editor.putString(PREF_SYSTEM_MODE_KEY, String.valueOf(mode));
        }
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
    public void switchDeviceToDayNightMode(View view) {
        Boolean switchToNight = true;

        int currentMode = getSavedMode("System");
        if (currentMode == 2) {
            Utils.setNightMode(this, false);
            saveMode("System", 1);
        } else {
            Utils.setNightMode(this, true);
            saveMode("System", 2);
        }

        Utils.showInfoDialog(this,getString(R.string.reboot_title), getString(R.string.reboot_text));
    }

    public void suSwitchDeviceToDayNightMode(View view) {

        ContentResolver contentResolver = this.getContentResolver();
        int currentMode = getSavedMode("System");
        if (currentMode == 1) {
            //RootCommands.executeRootCommand("settings put secure ui_night_mode 2");
            //ShellRootCommands.rootExec("settings put secure ui_night_mode 2"); //set device mode to night
            Settings.Secure.putInt(contentResolver, "ui_night_mode", 2);
            saveMode("System", 2);
        } else {
            //ShellRootCommands.rootExec("settings put secure ui_night_mode 1"); //set devicem mode to day
            Settings.Secure.putInt(contentResolver, "ui_night_mode", 1);
            saveMode("System", 1);
        }

        Utils.showInfoDialog(this,getString(R.string.reboot_title), getString(R.string.reboot_text));
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

    public void addBTSettingsToFYTSettings(View view) {
        Intent intent = new Intent(MainActivity.this, CustomDialog.class);
        intent.putExtra("FILENAME", "/oem/app/config.txt");
        intent.putExtra("TITLE", getString(R.string.add_btsettings_to_fyt_settings_title));
        intent.putExtra("TEXT", getString(R.string.add_btsettings_to_fyt_settings_text));
        intent.putExtra("IMAGE", "btsettingstofyt");
        startActivity(intent);

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

    public void defaultandroidsettings(View view) {
        Logger.logToFile("Starting the default Android Settings");
        startActivity(new Intent("android.settings.SETTINGS"));
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

    /**
     * This method shows an info dialog (with scrollview) for the mention file/text
     * @param view
     */
    public void disp_config_txt(View view) {
        Utils.showAboutDialog(this, "/oem/app/config.txt");
    }

    public void enableAppsOnBoot(View view) {
        Intent intent = new Intent(MainActivity.this, EnableAppsOnBoot.class);
        intent.putExtra("TITLE", "Start Apps On BOOT_COMPLETED");
        startActivity(intent);
        //startActivity(new Intent(MainActivity.this, EnableAppsOnBoot.class));

    }

    /**
     * This method simply starts the editor activity with the provided config.txt file
     * From this editor activity the Save/Cancel actions are arragned
     * @param view
     */
    public void editConfigTxt(View view) {
        Intent intent = new Intent(MainActivity.this, EditorActivity.class);
        intent.putExtra("FILENAME", "/oem/app/config.txt");
        intent.putExtra("TITLE", getString(R.string.btn_edit_config_txt));
        startActivity(intent);
    }

    /**
     * This method makes a backup of the app layer as provided by FYT in the AllAppUpdate.bin
     * It will zip with password withour compression and add the lsex631Xupdate, config.txt and updatecfg.txt to folder
     * BACKUP on External Storage (Internal memory)
     * @param view
     */
    public void zipAllAppUpdateBin(View view) {

        // Check if we are on a T'eyes
        String fytProp = propsHashMap.get("ro.build.fytmanufacturer");
        if (!fytProp.equals("95")) { // If not a T'eyes unit we can start our backup

            //showToastAndWait(getString(R.string.toast_start_zip), 100);
            // Start with displaying our progressBar
            mprogressBar.setVisibility(View.VISIBLE);

            String message = getBackupMessage();
            cacheDir = this.getCacheDir();
            String cacheDirString = cacheDir.toString();
            Logger.logToFile("Copying zip-arm to " + cacheDirString + " and set perimissions to 755");
            FileUtils.copyAssetFileToCache(MainActivity.this, "zip-arm");
            FileUtils.changeFilePermissions(new File(cacheDirString + "/zip-arm"), "755");

            // First (re)create folder BACKUP
            // Then copy lsec631xupdate, config.txt and updatecfg into BACKUP
            FileUtils.removeAndRecreateFolder("BACKUP");
            File BackupFolder = new File(Environment.getExternalStorageDirectory(), "BACKUP/AllAppUpdate.bin");
            //String BackupFolderPath = FileUtils.readFileToString(BackupFolder);
            Logger.logToFile("Created the backup folder " + BackupFolder.toString());

            // Copy the lsec631Xupdate binary using the earlier created message
            String result = "";
            String binary = "";
            if (message.contains("lsec6315update")) {
                binary = "lsec6315update";
                result = FileUtils.copyAssetsFileToExternalStorageFolder(this, binary, "BACKUP", binary);
            } else {
                binary = "lsec6316update";
                result = FileUtils.copyAssetsFileToExternalStorageFolder(this, binary, "BACKUP", binary);
            }
            if (result.equals("")) {
                Logger.logToFile("Copied " + binary + " to the BACKUP folder");
            } else {
                Logger.logToFile("Failed to copy " + binary + " to the BACKUP folder");
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
            }

            ///////////////////////
            // option 1
            // Below 2 sentences are working but block the UI
            String zipCommand = cacheDirString + "/zip-arm  -r -v -y -0 --password 048a02243bb74474b25233bda3cd02f8 /storage/emulated/0/BACKUP/AllAppUpdate.bin .";
            //Now start the command to do the backup using ShellExec or Rootexec
            ShellRootCommands.shellExec("echo twipe_all > /storage/emulated/0/BACKUP/updatecfg.txt", "cd /oem", zipCommand);

            //////////////////////////////
            // option 2
            /*AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    String zipCommand = cacheDirString + "/zip-arm  -r -v -y -0 --password 048a02243bb74474b25233bda3cd02f8 /storage/emulated/0/BACKUP/AllAppUpdate.bin .";
                    ShellRootCommands.shellExec("cd /oem", zipCommand);
                    //new ZipTask().onPreExecute();
                }
            });*/

            /////////////////////////////////////
            // Option 3
            //new ZipTask().doInBackground();

            // And now hide our progressBar again
            mprogressBar.setVisibility(View.INVISIBLE);

            Utils.showInfoDialog(this, getString(R.string.backup_finished), message);
        } else { // So we are on a T'eyes unit. This backup will not functiom.
            Utils.showInfoDialog(this, getString(R.string.teyes_unit_title), getString(R.string.teyes_unit_txt));
        }

    }

    private class ZipTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mprogressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected String doInBackground(Void... params) {
            String cacheDirString = cacheDir.toString();
            String zipCommand = cacheDirString + "/zip-arm  -r -v -y -0 --password 048a02243bb74474b25233bda3cd02f8 /storage/emulated/0/BACKUP/AllAppUpdate.bin .";
            ShellRootCommands.shellExec("echo twipe_all > /storage/emulated/0/BACKUP/updatecfg.txt", "cd /oem", zipCommand);

            return "ZipTask ready";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // This is executed on the main thread after the background task is completed
            mprogressBar.setVisibility(View.GONE);
        }
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
        MyGettersSetters.setTestVersion(TEST);
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
    private void showToastAndWait(String message, int delayMillis) {
        // Show a Toast
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        // Close the app after a delay
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do nothing, just wait
            }
        }, delayMillis);
    }


    // We create the logfile here in the MainActivity to be able to use it from here in all classes
    public static String createLogFile() {
        // Get the external storage directory
        File externalStorage = android.os.Environment.getExternalStorageDirectory();
        FileUtils.optionallyCreateFolder("FET_Logs");
        String curDateTime = Utils.getDateTime();
        // Create a File object representing the log file
        return new File(externalStorage, "FET_Logs" + File.separator + curDateTime + "_" + BASE_LOG_FILE_NAME).getAbsolutePath();
    }

    public void collectSystemInfo(View view){
        //showToastAndWait(getString(R.string.start_collect_system_info), 100);
        cacheDir = this.getCacheDir();
        String cacheDirString = cacheDir.toString();
        Logger.logToFile("Copying zip-arm to " + cacheDirString + " and set perimissions to 755");
        FileUtils.copyAssetFileToCache(MainActivity.this, "zip-arm");
        FileUtils.changeFilePermissions(new File(cacheDirString + "/zip-arm"), "755");

        String infoFolder = " > /storage/emulated/0/HWGetInfo/";
        FileUtils.removeAndRecreateFolder("HWGetInfo");
        String[] Commands = {
                "cat /proc/cpuinfo  > /storage/emulated/0/HWgetInfo/cpuinfo.txt",
                "cat /proc/meminfo  > /storage/emulated/0/HWgetInfo/meminfo.txt",
                "uname -a  > /storage/emulated/0/HWgetInfo/uname.txt",
                "getprop  > /storage/emulated/0/HWgetInfo/properties.txt",
                "ls -l /dev/block/platform/soc/soc:ap-ahb/c0c00000.sdio/by-name  > /storage/emulated/0/HWgetInfo/mapping_blocks2partitions.txt",
                "mount  > /storage/emulated/0/HWgetInfo/mounts.txt",
                "cat /proc/partitions  > /storage/emulated/0/HWGetInfo/partitions.txt",
                "ls -lR /dev/  > /storage/emulated/0/HWGetInfo/dev_listing.txt",
                "ls -lR /system/  > /storage/emulated/0/HWGetInfo/system_listing.txt",
                "ls -lR /sys/  > /storage/emulated/0/HWGetInfo/sys_listing.txt",
                "ls -lR /oem/  > /storage/emulated/0/HWGetInfo/oem_listing.txt",
                "ls -lR /vendor/  > /storage/emulated/0/HWGetInfo/vendor_listing.txt",
                "ls -lR /product/  > /storage/emulated/0/HWGetInfo/product_listing.txt",
                "cp /oem/app/config.txt /storage/emulated/0/HWGetInfo/",
                "cp /oem/app/fyt.prop /storage/emulated/0/HWGetInfo/",
                "cp /system/build.prop /storage/emulated/0/HWGetInfo/",
                "cp /oem/Ver /storage/emulated/0/HWGetInfo/",
                "rm -rf /storage/emulated/0/HWGetInfo.zip",
                "cd /storage/emulated/0/HWGetInfo/",
                cacheDirString + "/zip-arm  -r -v -y * /storage/emulated/0/HWGetInfo.zip"};

        ShellRootCommands.shellExec(Commands);
        Utils.showInfoDialog(this, getString(R.string.collected_system_info_title), getString(R.string.collected_system_info_txt));
    }

}
