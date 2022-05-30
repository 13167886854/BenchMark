package com.example.benchmark.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

import java.util.List;

public class ApkUtil {

    /**
     * judge whether the apk is installed
     */
    private static boolean isApkInstalled(Context context, String packageName) {
        if (packageName == null || packageName.isEmpty()) return false;
        final PackageManager packageManager = context.getPackageManager();
        @SuppressLint("QueryPermissionsNeeded")
        List<PackageInfo> info = packageManager.getInstalledPackages(0);
        if(info == null || info.isEmpty()) return false;
        for ( int i = 0; i < info.size(); i++ ) {
            if(packageName.equals(info.get(i).packageName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Go to App market to see the app's details
     *
     * @param packageName target App package name
     */
    private static void launchAppDetailInMarket(Context context, String packageName) {
        if (packageName == null || packageName.isEmpty()) return;
        Intent toAppMarketIntent = new Intent(Intent.ACTION_VIEW);
        toAppMarketIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        toAppMarketIntent.setData(Uri.parse("market://details?id=" + packageName));
        context.startActivity(toAppMarketIntent);
    }

    public static void launchApp(Context context, String packageName) {
        if (isApkInstalled(context, packageName)) {
            Intent launchAppIntent = context.getPackageManager()
                    .getLaunchIntentForPackage(packageName);
            if (launchAppIntent != null) {
                launchAppIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(launchAppIntent);
            } else {
                Toast.makeText(context, "Launch " + packageName + " Fail.",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            launchAppDetailInMarket(context, packageName);
        }
    }

}

