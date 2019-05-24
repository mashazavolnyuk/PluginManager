package com.kishynskaya.manager.helper;

import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import com.kishynskaya.manager.data.Plugin;


public class PluginHelper {

    public static Plugin create(ResolveInfo resolveInfo, PackageManager packageManager) {
        String name = String.valueOf(resolveInfo.loadLabel(packageManager));
        Drawable icon = resolveInfo.loadIcon(packageManager);
        ActivityInfo activityInfo = resolveInfo.activityInfo;
        ApplicationInfo applicationInfo = activityInfo.applicationInfo;
        String packageName = applicationInfo.packageName;
        Plugin plugin = new Plugin(name);
        plugin.setIcon(icon);
        plugin.setPackageName(packageName);
        return plugin;
    }
}
