package com.kishynskaya.manager.data;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class Plugin implements IPlugin {

    private String name;
    private Drawable icon;
    private String packageName;
    private volatile boolean isEnable;


    private volatile boolean isEnableNow;

    public Plugin(String name) {
        this.name = name;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageName() {
        return packageName;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    @Override
    public Drawable getIcon() {
        return icon;
    }

    @Override
    public boolean isEnable() {
        return isEnable;
    }

    @Override
    public synchronized void setEnable(boolean isRunning) {
        this.isEnable = isRunning;

    }

    public boolean isEnableNow() {
        return isEnableNow;
    }

    public void setEnableNow(boolean enableNow) {
        isEnableNow = enableNow;
    }

    @Override
    public void tryEnable(boolean enable, Context context) {
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        Log.d("TAG", "tryEnable" + enable);
        if (launchIntent != null) {
            if (enable) {
//                launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                launchIntent.putExtra("ENABLE", true);
                context.startActivity(launchIntent);
            } else {
//                launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                launchIntent.putExtra("ENABLE", false);
                context.startActivity(launchIntent);
            }
        }
    }
}
