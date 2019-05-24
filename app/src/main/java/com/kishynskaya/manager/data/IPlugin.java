package com.kishynskaya.manager.data;

import android.content.Context;
import android.graphics.drawable.Drawable;

public interface IPlugin {

    String getName();

    void setPackageName(String packageName);

    String getPackageName();

    void setIcon(Drawable icon);

    Drawable getIcon();

    boolean isEnable();

    void setEnable(boolean isRunning);

    void tryEnable(boolean enable, Context context);

     boolean isEnableNow();

     void setEnableNow(boolean enableNow);

}
