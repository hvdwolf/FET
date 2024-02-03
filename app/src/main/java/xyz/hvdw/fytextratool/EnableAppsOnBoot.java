package xyz.hvdw.fytextratool;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
        import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class EnableAppsOnBoot extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enable_apps_on_boot);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getIntent().getStringExtra("TITLE"));
        }
        // Get a list of installed applications
        List<PackageInfo> allPackageInfoList = getInstalledApplications();
        List<PackageInfo> launcherPackageInfoList = getAllLauncherApps();

        // Set up the ListView with the custom adapter
        ListView appListView = findViewById(R.id.appListView);
        AppListAdapter appListAdapter = new AppListAdapter(this, launcherPackageInfoList);
        //AppListAdapter appListAdapter = new AppListAdapter(this, allPackageInfoList);
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

    /*private List<PackageInfo> getInstalledApplications() {
        PackageManager pm = getPackageManager();
        //return packageManager.getInstalledPackages(PackageManager.GET_META_DATA);
        List<ApplicationInfo> allPackageInfoList = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        List<ApplicationInfo> launcherPackageInfoList = new ArrayList<>();

        for (ApplicationInfo packageInfo : allPackageInfoList) {
            if (hasLauncherIntent(pm, packageInfo.packageName)) {
                launcherPackageInfoList.add(packageInfo);
            }
        }
        return launcherPackageInfoList;
    }*/

    private static boolean hasLauncherIntent(PackageManager packageManager, String packageName) {
        Intent launchIntent = packageManager.getLaunchIntentForPackage(packageName);
        return launchIntent != null;
    }

    // Below one is working but doesn not filter non-launcher packages
    private List<PackageInfo> getInstalledApplications() {
        PackageManager pm = getPackageManager();
        //return pm.getInstalledPackages(PackageManager.GET_META_DATA);
        List<ApplicationInfo> allPackages =
                pm.getInstalledApplications(PackageManager.GET_META_DATA);

        List<ApplicationInfo> launcherPackageInfoList = new ArrayList<>();
                String TAG = "pipo";
        for (ApplicationInfo packageInfo : allPackages) {
            Log.d(TAG, "Installed package :" + packageInfo.packageName);
            Log.d(TAG, "Source dir : " + packageInfo.sourceDir);
            Log.d(TAG, "Launch Activity :" +
                    pm.getLaunchIntentForPackage(packageInfo.packageName));
            launcherPackageInfoList.add(packageInfo);
        }
        return pm.getInstalledPackages(PackageManager.GET_META_DATA);
        //return launcherPackageInfoList;
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
}
