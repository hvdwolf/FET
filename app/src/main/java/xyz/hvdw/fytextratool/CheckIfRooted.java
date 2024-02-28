package xyz.hvdw.fytextratool;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import java.io.File;

public class CheckIfRooted {

    // Check if Magisk rooted
    public static boolean isMagiskRooted(Context context) {
        return (checkMagiskFiles() || checkMagiskSuBinary()) && isMagiskAPKInstalled(context);
    }

    private static boolean checkMagiskFiles() {
        // Check for the presence of Magisk-related files
        File file1 = new File("/sbin/.core/mirror/system");
        File file2 = new File("/sbin/.core/img");
        return file1.exists() || file2.exists();
    }

    private static boolean checkMagiskSuBinary() {
        // Check for the presence of Magisk su binary
        File file1 = new File("/sbin/.magisk/img/magisk");
        File file2 = new File("/sbin/su");
        return file1.exists() || file2.exists();
    }

    private static boolean isMagiskAPKInstalled(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo("com.topjohnwu.magisk", 0);
            return applicationInfo != null;
        } catch (PackageManager.NameNotFoundException e) {
            // Magisk Manager package not found
            return false;
        }
    }



    // General root check
    public static boolean isUnitRooted(Context context) {
        //return checkRootMethod1() || checkRootMethod2() || checkRootMethod3();
        return isSuperSUInstalled(context) && checkRootMethod3();
    }

    // This check is very debatable. Having test keys does not mean the unit is rooted.
    // Now no longer used
    private static boolean checkRootMethod1() {
        String buildTags = android.os.Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }

    private static boolean checkRootMethod2() {
        // Check if Superuser.apk is present
        File file = new File("/system/app/Superuser.apk");
        return file.exists();
    }

    public static boolean isSuperSUInstalled(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo("eu.chainfire.supersu", 0);
            return applicationInfo != null;
        } catch (PackageManager.NameNotFoundException e) {
            // SuperSU package not found
            return false;
        }
    }

    private static boolean checkRootMethod3() {
        // Check for the presence of su binary
        return new File("/system/xbin/su").exists() || new File("/system/bin/su").exists();
    }
}
