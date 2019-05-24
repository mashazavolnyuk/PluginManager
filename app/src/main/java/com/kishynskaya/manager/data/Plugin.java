package com.kishynskaya.manager.data;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

public class Plugin implements IPlugin {

    private String name;
    private Drawable icon;
    private String packageName;
    private String packageNameService;
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
    public String getPackageNameService() {
        return packageNameService;
    }

    @Override
    public void setPackageNameService(String packageNameService) {
        this.packageNameService = packageNameService;
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

        Intent intent = new Intent();
        String pkg = getPackageName();
        String cls = getPackageNameService();
        intent.setComponent(new ComponentName(pkg, cls));
        if (!enable) {
            intent.setAction("ACTION_STOP");
        }
        ContextCompat.startForegroundService(context, intent);
    }
}
