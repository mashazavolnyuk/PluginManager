package com.kishynskaya.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.support.annotation.IntDef;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;


import com.kishynskaya.manager.adapter.PluginsAdapter;
import com.kishynskaya.manager.data.IPlugin;
import com.kishynskaya.manager.data.Plugin;
import com.kishynskaya.manager.helper.PluginHelper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    private final String DETECT_ENABLED_APPS = "DETECT_ENABLING_APPS";

    private PackageManager packageManager;
    private PluginsAdapter pluginsAdapter;

    private static Map<String, IPlugin> pluginMap;

    private AppsExistReceiver appsExistReceiver;
    private AppsIsEnableReceiver appsIsEnableReceiver;

    final static int EXIST_APPS = 0;
    final static int ENABLED_APPS = 1;

    Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        pluginsAdapter = new PluginsAdapter(Collections.<IPlugin>emptyList(), this);
        recyclerView.setAdapter(pluginsAdapter);
        pluginMap = new HashMap<>();
        packageManager = getPackageManager();
        handler = new Handler();
        appsExistReceiver = new AppsExistReceiver();
        registerReceiver(appsExistReceiver, getFilter(EXIST_APPS));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (appsIsEnableReceiver == null) {
            appsIsEnableReceiver = new AppsIsEnableReceiver();
        }
        registerReceiver(appsIsEnableReceiver, getFilter(ENABLED_APPS));


        updateListInstalledPlugins(packageManager);
        startUpdateStatesPlugin();
    }

    private void startUpdateStatesPlugin() {
        if (handler == null) {
            handler = new Handler();
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                updatePluginsTask();
                handler.postDelayed(this, 1000);
            }
        });
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({EXIST_APPS, ENABLED_APPS})
    @interface ItemFilter {
    }

    private IntentFilter getFilter(@ItemFilter int filter) {
        IntentFilter intentFilter = new IntentFilter();
        switch (filter) {
            case EXIST_APPS:
                intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
                intentFilter.addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED);
                intentFilter.addAction(Intent.ACTION_INSTALL_PACKAGE);
                intentFilter.addDataScheme("package");
                break;
            case ENABLED_APPS:
                intentFilter.addAction(DETECT_ENABLED_APPS);
                break;
        }
        return intentFilter;
    }

    private void updatePluginsTask() {
        for (IPlugin plugin : pluginMap.values()) {
            if (plugin.isEnable() != plugin.isEnableNow()) {
                plugin.setEnable(plugin.isEnableNow());
                pluginsAdapter.updateData(plugin);
            }
        }

        resetEnableNow();

        Intent intent = new Intent();
        String ENABLED_APP = "ENABLED_APP";
        intent.setAction(ENABLED_APP);
        sendBroadcast(intent);
    }

    private void resetEnableNow() {
        List<IPlugin> iPluginList = new ArrayList<>(pluginMap.values());
        for (IPlugin iPlugin : iPluginList) {
            iPlugin.setEnableNow(false);
        }
    }

    private void updateListInstalledPlugins(PackageManager pm) {
        pluginMap.clear();
        Intent startupIntent = new Intent(Intent.ACTION_MAIN);
        String FILTER_PLUGIN = "OWN_PLUGIN";
        startupIntent.addCategory(FILTER_PLUGIN);
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(startupIntent, 0);
        for (ResolveInfo resolveInfo : resolveInfos) {
            Plugin plugin = PluginHelper.create(resolveInfo, pm);
            if (plugin != null) {
                pluginMap.put(plugin.getPackageName(), plugin);
            }
        }
        List<IPlugin> plugins = new ArrayList<>(pluginMap.values());
        pluginsAdapter.updateAllData(plugins);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (appsIsEnableReceiver != null) {
            unregisterReceiver(appsIsEnableReceiver);
        }
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onDestroy() {
        if (appsExistReceiver != null) {
            unregisterReceiver(appsExistReceiver);
        }
        super.onDestroy();
    }

    public class AppsExistReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == null) return;
            switch (intent.getAction()) {
                case Intent.ACTION_PACKAGE_FULLY_REMOVED:
                case Intent.ACTION_PACKAGE_ADDED:
                case Intent.ACTION_INSTALL_PACKAGE:
                    updateListInstalledPlugins(packageManager);
                    break;
            }
        }
    }

    public class AppsIsEnableReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == null) return;
            if (DETECT_ENABLED_APPS.equals(intent.getAction())) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    String packageName = bundle.getString("packageName");
                    boolean isEnabled = bundle.getBoolean("ENABLE", false);
                    IPlugin iPlugin = pluginMap.get(packageName);
                    if (iPlugin != null) {
                        iPlugin.setEnableNow(isEnabled);
                    }
                }
            }
        }
    }
}
