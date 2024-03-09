package xyz.hvdw.fytextratool;

import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import p32929.easypasscodelock.Utils.EasyLock;


@RequiresApi(api = Build.VERSION_CODES.S)
public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private FrameLayout frameLayout;

    String[] REQUIRED_PERMISSIONS = {
            android.Manifest.permission.BLUETOOTH,
            android.Manifest.permission.BLUETOOTH_ADMIN,
            android.Manifest.permission.BLUETOOTH_CONNECT,
            android.Manifest.permission.BLUETOOTH_SCAN,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_SETTINGS,
            android.Manifest.permission.WRITE_SECURE_SETTINGS,
            android.Manifest.permission.GET_PACKAGE_SIZE,
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.ACCESS_NETWORK_STATE
            };

    String[] propNames = {"ro.board.platform", "ro.build.version.sdk", "ro.build.version.release", "ro.fyt.uiid", "sys.fyt.bluetooth_type", "sys.fyt.front_video_ic", "ro.build.fytid",
            "sys.fyt.platform", "ro.fota.platform", "sys.fyt.cvbs.height", "sys.fyt.cvbs.width", "persist.sys.syu.audio", "ro.system.build.date", "ro.lsec.app.version",
            "persist.sys.syu.audio", "persist.syu.camera360", "persist.fyt.fm.name", "persist.fyt.withrdsfn", "persist.fyt.zh_frontview_enable", "ro.build.fytmanufacturer" };
    Map<String, String> propsHashMap = new HashMap<>();

    private AlertDialog bePatientDialog;
    private Menu menu;

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

        tabLayout = findViewById(R.id.tabLayout);
        frameLayout = findViewById(R.id.frameLayout);
        //appMode = findViewById(R.id.action_apptogglebutton);
        //suSystemMode = findViewById(R.id.suDeviceDayNight);

        // Add tabs
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_general)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_bluetooth)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_settings)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_system)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_lockscreen)));

        // Set listener for tab selection
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        loadFragment(new Fragment_General());
                        break;
                    case 1:
                        loadFragment(new Fragment_Bluetooth());
                        break;
                    case 2:
                        loadFragment(new Fragment_Settings());
                        break;
                    case 3:
                        loadFragment(new Fragment_System());
                        break;
                    case 4:
                        loadFragment(new Fragment_Lockscreen());
                        break;

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        // Set default fragment on launch
        loadFragment(new Fragment_General());


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
            //String remoteVersion = CheckUpdates.readFETVersionString("https://raw.githubusercontent.com/hvdwolf/FET/main/version.txt");
            //String remoteVersion = CheckUpdates.readFETVersionString("https://github.com/hvdwolf/FET/blob/main/app/build.gradle");
            //String remoteVersion = CheckUpdates.readFETVersionString("https://raw.githubusercontent.com/hvdwolf/FET/main/app/build.gradle");
            //Utils.showInfoDialog(this, "remoteVersion", remoteVersion);

            // Check if rooted. If not disable some buttons
            boolean isRooted = CheckIfRooted.isUnitRooted(this);
            MyGettersSetters.setIsRooted(isRooted);
            Logger.logToFile("Boolean isRooted is " + Boolean.toString(isRooted));
            boolean isMagiskRooted = CheckIfRooted.isMagiskRooted(this);
            MyGettersSetters.setIsMagiskRooted(isMagiskRooted);
            Logger.logToFile("Boolean isMagiskRooted is " + Boolean.toString(isMagiskRooted));
            if ( !isRooted && !isMagiskRooted) {
                //disableRootedButtons();
                Logger.logToFile("The unit is not rooted.");
            }
            /*if ( !(CheckIfRooted.isUnitRooted()) && !(CheckIfRooted.isMagiskRooted()) ) {

            }*/
            // Where is my External Storage ?
            Logger.logToFile("ExternalStorage path is " + FileUtils.strExternalStorage());
            //Toast.makeText(MainActivity.this, FileUtils.strExternalStorage(), Toast.LENGTH_SHORT).show();
        }


        EasyLock.forgotPassword(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Clicked on forgot password", Toast.LENGTH_SHORT).show();
            }
        });
    }
    // End of OnCreate

   // Handle mulit-window support in case of splitscreen
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .commit();
    }

    // Fragment for Tab General
    public static class Fragment_General extends Fragment {
        private Button suSystemMode;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_general, container, false);
            suSystemMode = view.findViewById(R.id.suDeviceDayNight);
            return view;
        }

        public void updateButtonText(String newText) {
            suSystemMode.setText(newText);
        }
    }
    // Fragment for tab Bluetooth
    public static class Fragment_Bluetooth extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_bluetooth, container, false);
        }
    }
    // Fragment for Tab Settings
    public static class Fragment_Settings extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_settings, container, false);
        }
    }
    // Fragment for the Canbus monitor
    public static class Fragment_System extends Fragment {
        /*private CheckBox main_interface;
        private CheckBox canbus_interface;
        private CheckBox sound_interface;
        private CheckBox canup_interface;*/
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view =  inflater.inflate(R.layout.fragment_system, container, false);
            /*main_interface = view.findViewById(R.id.main_interface);
            canbus_interface = view.findViewById(R.id.canbus_interface);
            sound_interface = view.findViewById(R.id.sound_interface);
            canup_interface = view.findViewById(R.id.canup_interface);*/
            return view;
        }

        /*public boolean read_main_interface() {
            return main_interface.isChecked();
        }
        public boolean read_canbus_interface() {
            return canup_interface.isChecked();
        }
        public boolean read_sound_interface() {
            return sound_interface.isChecked();
        }
        public boolean read_canup_interface() {
            return canup_interface.isChecked();
        }*/

    }

    // Fragment for Tab Lockscreen
    public static class Fragment_Lockscreen extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_lockscreen, container, false);
        }
    }


    ///////////////////////////////////////////// top-right menu /////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //toggleTextItem = menu.findItem(R.id.action_apptogglebutton);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Unfortunately switch/case does not work, so we need a lot of if statements
        if (id == R.id.action_apptogglebutton) {
            toggleAppMode();
            return true;
        }
        if (id == R.id.action_importantProperties) {
            dispImportantProperties();
            return true;
        }

        if (id == R.id.action_about) {
            Utils.showAboutDialog(this, "about");
            return true;
        }

        if (id == R.id.action_used_os_tools) {
            Utils.showAboutDialog(this, "used_os_tools");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    /////////////////////////////////////////////  top-right menu /////////////////////////

    // Buttons for locscreen
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
    //////// End of buttons for lockscreen

    private void textButtonsAppSystem() {
        //Button appMode = findViewById(R.id.toggleButton);
        //appMode = findViewById(R.id.action_apptogglebutton);

        int currentMode = getSavedMode("App");
        Logger.logToFile("current app mode = " + String.valueOf(currentMode));
        String appMode = "current app mode = " + String.valueOf(currentMode);
        applyAppModeFromStart(currentMode);
        //Toast.makeText(this, "current app mode = " + String.valueOf(currentMode), Toast.LENGTH_LONG).show();
        // I need to fix this some time.
        /*MenuItem menuitemAppMode = menu.findItem(R.id.action_apptogglebutton);
        if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) {
            //appMode.setText(R.string.app_theme_light);
            //appMode.setTitle(R.string.app_theme_light);
            //toggleTextItem.setTitle(R.string.app_theme_light);
            menuitemAppMode.setTitle(R.string.app_theme_light);
            Toast.makeText(this, "current app mode = " + String.valueOf(currentMode), Toast.LENGTH_LONG).show();
        } else {
            //appMode.setText(R.string.app_theme_dark);
            //appMode.setTitle(R.string.app_theme_dark);
            //toggleTextItem.setTitle(R.string.app_theme_dark);
            menuitemAppMode.setTitle(R.string.app_theme_dark);
            Toast.makeText(this, "current app mode = " + String.valueOf(currentMode), Toast.LENGTH_LONG).show();
        }*/
        currentMode = getSavedMode("System");
        Logger.logToFile("current system mode = " + String.valueOf(currentMode));
        String systemMode = "current system mode = " + String.valueOf(currentMode);
        //Toast.makeText(this, "current system mode = " + String.valueOf(currentMode), Toast.LENGTH_SHORT).show();
        Fragment_General fragmentGeneral = (Fragment_General) getSupportFragmentManager().findFragmentById(R.id.fragment_general);
        if (currentMode == 1) { //Day
            if (fragmentGeneral != null) {
                fragmentGeneral.updateButtonText(getString(R.string.system_theme_night));
            }
        } else {
            if (fragmentGeneral != null) {
                fragmentGeneral.updateButtonText(getString(R.string.system_theme_day));
            }
        }
        //Utils.showInfoDialog(this, "textButtons", appMode + "\n" + systemMode);

    }

    /*private void disableRootedButtons() {
        Fragment_General fragmentGeneral = (Fragment_General) getSupportFragmentManager().findFragmentById(R.id.fragment_general);
        if (fragmentGeneral != null) {
            Toast.makeText(MainActivity.this, "Trying to disable daynight and editconfig", Toast.LENGTH_SHORT).show();
            fragmentGeneral.disableDayNightButton(false);
            fragmentGeneral.disableEditoConfigbtn(false);
        }
        Fragment_Bluetooth fragmentBluetooth = (Fragment_Bluetooth) getSupportFragmentManager().findFragmentById(R.id.fragment_bluetooth);
        if (fragmentBluetooth != null) {
            fragmentBluetooth.disableAddbttosettings();
        }
        Fragment_Settings fragmentSettings = (Fragment_Settings) getSupportFragmentManager().findFragmentById(R.id.fragment_settings);
        if (fragmentSettings != null) {
            fragmentSettings.disableADBUSBButton();
        }
    } */

    public void dispImportantProperties() {
        Utils.showAboutDialog(this, "properties");
    }


    /* Below 6 methods are for the light/dark app toggle mode */

    // menu option
    public void toggleAppMode() {
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
    // main screen option
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
    private void applyAppModeFromStart(int mode) {
        Logger.logToFile("App theme switching to " + String.valueOf(mode));
        AppCompatDelegate.setDefaultNightMode(mode);
    }
    /* Above 6 methods  are for the light/dark app toggle mode */

    /* Below method switches the unit to night or day mode */
    public void suSwitchDeviceToDayNightMode(View view) {
        //Toast.makeText(this, "isRooted " + MyGettersSetters.getIsRooted() + " isMagiskRooted " + MyGettersSetters.getIsMagiskRooted(), Toast.LENGTH_SHORT).show();
        if ( (MyGettersSetters.getIsRooted()) || MyGettersSetters.getIsMagiskRooted()) {
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
        } else {
            unitNotRooted();
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

    public void exitApp(View view) {
        // Call finish() to exit the program
        finish();
    }

    public void openBluetoothSettings(View view) {
        // Start Bluetooth settings
        BluetoothUtils.startBluetoothSettings(this);
    }

    public void checkBluetoothDevice(View view) {
        List<String> bluetoothProperties = BluetoothUtils.checkBluetoothDevice(this);
        String message = "Primary Bluetooth Adapter Name: " + bluetoothProperties.get(0) + "\n";
        message += "Primary Bluetooth Adapter MAC address: " + bluetoothProperties.get(1) + "\n";
        message += "Is the primary Bluetooth Adapter enabled? " + bluetoothProperties.get(2) + "\n";
        message += "Primary Bluetooth Adapter state: " + bluetoothProperties.get(3) + "\n";
        Utils.showInfoDialog(this, "Primary Bluetooth Adapter", message);
    }

    public void checkMultipleBTAdapters(View view) {
        String message = "";
        List<String> bluetoothProperties = BluetoothUtils.checkMultipleBTAdapters(this);
        for (String entry : bluetoothProperties) {
            message += entry;
        }
        Utils.showInfoDialog(this, "Bluetooth Adapters", message);
    }

    public void checkFYTBTAdapter(View view) {
        String message = "";
        try {
            List<String> bluetoothProperties = BluetoothUtils.checkFYTBluetoothAdapter(this);
            for (String entry : bluetoothProperties) {
                message += entry;
            }
        } catch (Exception e) {
            message = e.toString();
            throw new RuntimeException(e);
        }
        Utils.showInfoDialog(this, "Bluetooth Adapters", message);
    }

    public void findSecondaryBluetoothAdapters(View view) {
        String message = "";
        List<BluetoothAdapter> bluetoothAdapters = BluetoothUtils.findSecondaryBluetoothAdapters(this);
        for (BluetoothAdapter adapter : bluetoothAdapters) {
            if (adapter != null) {
                // Retrieve information about each Bluetooth adapter
                message += adapter.getName(); // Get the name of the adapter
                message += adapter.getAddress(); // Get the MAC address of the adapter
                message += adapter.getState(); // Get the current state of the adapter
            }
        }
        Utils.showInfoDialog(this, "Bluetooth Adapters", message);
    }

    public void addBTSettingsToFYTSettings(View view) {
        if ( (MyGettersSetters.getIsRooted()) || MyGettersSetters.getIsMagiskRooted()) {
            Intent intent = new Intent(MainActivity.this, CustomDialog.class);
            Map<String, String> fytPlatform = MyGettersSetters.getPropsHashMap();
            if (fytPlatform.get("ro.board.platform").contains("ums9620")) {
                intent.putExtra("FILENAME", "/odm/app/config.txt");
            } else {
                intent.putExtra("FILENAME", "/oem/app/config.txt");
            }
            intent.putExtra("TITLE", getString(R.string.add_btsettings_to_fyt_settings_title));
            intent.putExtra("TEXT", getString(R.string.add_btsettings_to_fyt_settings_text));
            intent.putExtra("ACTION", "btsettingstofyt");
            startActivity(intent);
        } else {
            unitNotRooted();
        }

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

    public void adboverwifiandusbdebugging(View view) {
        if ( (MyGettersSetters.getIsRooted()) || MyGettersSetters.getIsMagiskRooted()) {
            // First we enable the USB debugging. We do not even check if it us enabled
            //settings put global adb_enabled 1
            ShellRootCommands.libsuRootExec("settings put global adb_enabled 1");

            Logger.logToFile("Enable ADB over WiFi and enable usb debugging");
            Intent intent = new Intent(MainActivity.this, CustomDialog.class);
            Map<String, String> fytPlatform = MyGettersSetters.getPropsHashMap();
            if (fytPlatform.get("ro.board.platform").contains("ums9620")) {
                intent.putExtra("FILENAME", "/odm/app/config.txt");
            } else {
                intent.putExtra("FILENAME", "/oem/app/config.txt");
            }
            intent.putExtra("TITLE", getString(R.string.btn_adboverwifiandusbdebugging));
            intent.putExtra("TEXT", getString(R.string.btn_adboverwifiandusbdebugging));
            intent.putExtra("ACTION", "adboverwifiandusbdebugging");
            startActivity(intent);
        } else {
            unitNotRooted();
        }
    }

    public void testButton(View view) {
        String message = "";
        String result = FileUtils.shellGetAvailableStorageLocations(this);
        String[] lines = result.split("\\n");
        for (String line : lines) {
            if (!line.contains("tmpfs")) {
                message += line;
            }
        }
        Utils.showInfoDialog(this, "Storage Locations", message);
    }

    /**
     * This method shows an info dialog (with scrollview) for the mention file/text
     * @param view
     */
    public void disp_config_txt(View view) {
        Map<String, String> fytPlatform = MyGettersSetters.getPropsHashMap();
        if (fytPlatform.get("ro.board.platform").contains("ums9620")) {
            Utils.showAboutDialog(this, "/odm/app/config.txt");
        } else {
            Utils.showAboutDialog(this, "/oem/app/config.txt");
        }
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
        if ( (MyGettersSetters.getIsRooted()) || MyGettersSetters.getIsMagiskRooted()) {
            Intent intent = new Intent(MainActivity.this, EditorActivity.class);
            intent.putExtra("FILENAME", "/oem/app/config.txt");
            intent.putExtra("TITLE", getString(R.string.btn_edit_config_txt));
            startActivity(intent);
        } else {
            unitNotRooted();
        }
    }

    public void fytCanbusMonitor(View view) {

        final boolean[][] checkBoxes = {new boolean[4]};
        DialogWithCheckboxes dialog = new DialogWithCheckboxes();
        dialog.show(getSupportFragmentManager(), "dialog_with_checkboxes");
    }
    public void continueWithMethodForFytCanbusMonitor(boolean mainChecked, boolean canbusChecked, boolean soundChecked, boolean canupChecked) {
        // Do something with the checked options
        Intent intent = new Intent(MainActivity.this, FytCanBusMonitor.class);
        intent.putExtra("TITLE", getString(R.string.btn_fytcanbusmonitor));
        intent.putExtra("MAIN", mainChecked);
        intent.putExtra("CANBUS", canbusChecked);
        intent.putExtra("SOUND", soundChecked);
        intent.putExtra("CANUP", canupChecked);
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
        String boardProp = propsHashMap.get("ro.board.platform");
        if ((!fytProp.equals("95")) && (!boardProp.equals("ums9620"))) { // If not a T'eyes unit (95) AND not a 7870(ums9620) we can start our backup

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
            //String zipCommand = cacheDirString + "/zip-arm  -r -v -y -0 --password 048a02243bb74474b25233bda3cd02f8 /storage/emulated/0/BACKUP/AllAppUpdate.bin .";
            //Now start the command to do the backup using ShellExec or Rootexec
            //ShellRootCommands.shellExec("echo twipe_all > /storage/emulated/0/BACKUP/updatecfg.txt", "cd /oem", zipCommand);

            /////////////////////////////////////
            // Option 2
            //Toast.makeText(this, getString(R.string.toast_start_zip), Toast.LENGTH_LONG).show();

            String dlgTitle = getString(R.string.backup_finished);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    String zipCommand = cacheDirString + "/zip-arm  -r -v -y -0 --password 048a02243bb74474b25233bda3cd02f8 /storage/emulated/0/BACKUP/AllAppUpdate.bin .";
                    //Now start the command to do the backup using ShellExec or Rootexec
                    ShellRootCommands.shellExec("echo twipe_all > /storage/emulated/0/BACKUP/updatecfg.txt", "cd /oem", zipCommand);
                    bePatientDialog.dismiss();
                    endDialog(dlgTitle, message);
                }
            }, 200);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.dialog_start_zip));
            bePatientDialog = builder.create();
            bePatientDialog.show();

        } else { // So we are on a T'eyes unit OR on a 7870(ums9620). This backup will not function.
            if (fytProp.contains("ums9620")) {
                Utils.showInfoDialog(this, getString(R.string.teyes_unit_title), getString(R.string.m7_7870_unit_txt));
            } else { // so we are on a T'eyes
                Utils.showInfoDialog(this, getString(R.string.teyes_unit_title), getString(R.string.teyes_unit_txt));
            }
        }

    }

    private void endDialog(String title, String message) {
        Utils.showInfoDialog(this, title, message);
    }

    private void unitNotRooted() {
        Utils.showInfoDialog(this, getString(R.string.not_rooted_title), getString(R.string.not_rooted_text));
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

    public MainActivity() {
        super();
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
                //It is a FYT uis7862/ums512 or a uis8581/sc9863, but we still want to know whether it is at the right SDK level
                if (!sdkProp.equals("29")) {
                    Utils.showDialogAndCloseApp(this, getString(R.string.correct_fyt_wrong_sdk), getString(R.string.correct_fyt_wrong_sdk_message));
                }
                // In backup we can test for Teyes
                FYT = true;
            } else if (fytProp.contains("ums9620")) {
                // It is a FYT 7870
                if (!sdkProp.equals("33")) {
                    Utils.showDialogAndCloseApp(this, getString(R.string.correct_fyt_wrong_sdk), getString(R.string.correct_fyt_wrong_sdk_message));
                }
                // In backup we can test for Teyes
                FYT = true;
            } else {
                //showToastAndCloseApp("This is not a FYT unit. The app will be closed.", 3500);
                Utils.showDialogAndCloseApp(this, getString(R.string.not_a_fyt), getString(R.string.not_a_fyt_message));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finishAffinity();
                    }
                }, 3500);
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
                "cp /odm/app/config.txt /storage/emulated/0/HWGetInfo/",
                "cp /odm/app/fyt.prop /storage/emulated/0/HWGetInfo/",
                "cp /system/build.prop /storage/emulated/0/HWGetInfo/",
                "cp /oem/Ver /storage/emulated/0/HWGetInfo/",
                "cp /odm/Ver /storage/emulated/0/HWGetInfo/",
                "rm -rf /storage/emulated/0/HWGetInfo.zip",
                "cd /storage/emulated/0/HWGetInfo/",
                cacheDirString + "/zip-arm  -r -v -y * /storage/emulated/0/HWGetInfo.zip"};

        //Toast.makeText(this, getString(R.string.start_collect_system_info), Toast.LENGTH_LONG).show();

        String dlgTitle = (getString(R.string.collected_system_info_title));
        String dlgMessage = getString(R.string.collected_system_info_txt);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String zipCommand = cacheDirString + "/zip-arm  -r -v -y -0 --password 048a02243bb74474b25233bda3cd02f8 /storage/emulated/0/BACKUP/AllAppUpdate.bin .";
                //Now start the command to do the backup using ShellExec or Rootexec
                ShellRootCommands.shellExec(Commands);
                bePatientDialog.dismiss();
                endDialog(dlgTitle, dlgMessage);
            }
        }, 200);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.dialog_start_collect_system_info));
        bePatientDialog = builder.create();
        bePatientDialog.show();
    }

}
