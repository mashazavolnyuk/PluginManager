package com.kishynskaya.manager.adapter;

import com.kishynskaya.manager.data.IPlugin;

import java.util.Timer;

public class PluginsAdapterItem {
    private IPlugin plugin;
    private Timer timer;

    public PluginsAdapterItem(IPlugin plugin) {
        this.plugin = plugin;
    }

    public IPlugin getPlugin() {
        return plugin;
    }

    public void setPlugin(IPlugin plugin) {
        this.plugin = plugin;
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }
}
