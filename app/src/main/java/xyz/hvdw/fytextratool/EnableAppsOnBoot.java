package xyz.hvdw.fytextratool;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;


public class EnableAppsOnBoot extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enable_apps_on_boot);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getIntent().getStringExtra("TITLE"));
        }

        // Get a list of applications that have a luancher intent
        List<PackageInfo> launcherPackageInfoList = getAllLauncherApps();
        // Get a list of packages that like to use the boot_completed command and need to be white listed
        //List<ApplicationInfo> appsWithBootPermission = getApplicationsWithBootPermission(this);
        List<PackageInfo> appsWithBootPermission = getApplicationsWithBootPermission(this);


        // Set up the ListView with the custom adapter
        ListView appListView = findViewById(R.id.appListView);
        // The launcher apps
        //AppListAdapter appListAdapter = new AppListAdapter(this, launcherPackageInfoList);
        // The boot_completed apps
        AppListAdapter appListAdapter = new AppListAdapter(this, appsWithBootPermission);
        appListView.setAdapter(appListAdapter);

        // Handle item click events
        appListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Handle item click
                // You can use the position to identify the clicked app
            }
        });
    }

    // Handle mulit-window support in case of splitscreen
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    private static boolean hasLauncherIntent(PackageManager packageManager, String packageName) {
        Intent launchIntent = packageManager.getLaunchIntentForPackage(packageName);
        return launchIntent != null;
    }


    private List<PackageInfo> getAllLauncherApps() {
        List<PackageInfo> appList = new ArrayList<>();

        PackageManager packageManager = getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(intent, 0);

        for (ResolveInfo resolveInfo : resolveInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            try {
                PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
                ApplicationInfo applicationInfo = packageInfo.applicationInfo;
                if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    appList.add(packageInfo);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        return appList;
    }

    private List<PackageInfo> getApplicationsWithBootPermission(Context context) {
    //private List<ApplicationInfo> getApplicationsWithBootPermission(Context context) {
        //List<ApplicationInfo> appsWithBootPermission = new ArrayList<>();
        List<PackageInfo> appsWithBootPermission = new ArrayList<>();
        PackageManager packageManager = context.getPackageManager();

        // Get the list of installed packages
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS);

        for (PackageInfo packageInfo : installedPackages) {
            // Check if the package has requested the BOOT_COMPLETED permission
            if (hasBootPermission(packageInfo)) {
                // Add the application info to the list
                //appsWithBootPermission.add(packageInfo.applicationInfo);
                appsWithBootPermission.add(packageInfo);
            }
        }

        return appsWithBootPermission;
    }

    private boolean hasBootPermission(PackageInfo packageInfo) {
        // Check if the package has requested the BOOT_COMPLETED permission
        String[] requestedPermissions = packageInfo.requestedPermissions;
        if (requestedPermissions != null) {
            for (String permission : requestedPermissions) {
                if (permission.equals("android.permission.RECEIVE_BOOT_COMPLETED")) {
                    return true;
                }
            }
        }
        return false;
    }
}
