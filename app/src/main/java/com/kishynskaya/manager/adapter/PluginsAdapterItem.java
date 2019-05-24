package com.kishynskaya.manager.adapter;

import com.kishynskaya.manager.data.IPlugin;

import java.util.Timer;

class PluginsAdapterItem {
    private IPlugin plugin;
    private Timer timer;

    PluginsAdapterItem(IPlugin plugin) {
        this.plugin = plugin;
    }

    IPlugin getPlugin() {
        return plugin;
    }

    void setPlugin(IPlugin plugin) {
        this.plugin = plugin;
    }

    Timer getTimer() {
        return timer;
    }

    void setTimer(Timer timer) {
        this.timer = timer;
    }
}
