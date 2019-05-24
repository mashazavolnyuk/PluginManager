package com.kishynskaya.manager.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.kishynskaya.manager.R;
import com.kishynskaya.manager.data.IPlugin;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PluginsAdapter extends RecyclerView.Adapter<PluginsAdapter.ViewHolder> {

    private static final int TIMEOUT = 3000;

    private List<PluginsAdapterItem> plugins;
    private WeakReference<Context> contextWeakReference;

    public PluginsAdapter(List<IPlugin> plugins, Context context) {
        this.plugins = new ArrayList<>();
        for (IPlugin plugin : plugins) {
            this.plugins.add(new PluginsAdapterItem(plugin));
        }
        contextWeakReference = new WeakReference<>(context);
    }

    private void startWaitingData() {
        for (int i = 0; i < plugins.size(); i++) {
            startWaitingForItem(i);
        }
        notifyDataSetChanged();
    }

    private void startWaitingForItem(final int position) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                plugins.get(position).getPlugin().setEnable(false);
                plugins.get(position).setTimer(null);
                Context context = contextWeakReference.get();
                if(context != null && !((Activity)context).isFinishing()) {
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            notifyItemChanged(position);
                        }
                    });
                }
            }
        }, TIMEOUT);
        plugins.get(position).setTimer(timer);
    }

    @NonNull
    @Override
    public PluginsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_plugin, viewGroup, false);
        return new ViewHolder(view);
    }

    public void updateData(IPlugin pluginToUpdate) {
        for (int i = 0; i < plugins.size(); i++) {
            if (plugins.get(i).getPlugin().getPackageName().equals(pluginToUpdate.getPackageName())) {
                plugins.get(i).setPlugin(pluginToUpdate);
                Timer timer = plugins.get(i).getTimer();
                if (timer != null) {
                    plugins.get(i).getTimer().cancel();
                }
                plugins.get(i).setTimer(null);
                notifyItemChanged(i);
                break;
            }
        }
    }

    public void updateAllData(List<IPlugin> data) {
        this.plugins.clear();
        for (IPlugin plugin : data) {
            this.plugins.add(new PluginsAdapterItem(plugin));
        }
        startWaitingData();
    }

    @Override
    public void onBindViewHolder(@NonNull final PluginsAdapter.ViewHolder holder, int position) {
        final Context context = contextWeakReference.get();
        IPlugin plugin = plugins.get(position).getPlugin();
        holder.imageViewIcon.setImageDrawable(plugin.getIcon());
        holder.textViewName.setText(plugin.getName());
        holder.switchEnableApp.setOnCheckedChangeListener(null);
        holder.switchEnableApp.setChecked(plugin.isEnable());
        holder.switchEnableApp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                plugins.get(holder.getAdapterPosition()).getPlugin().tryEnable(isChecked, context);
                startWaitingForItem(holder.getAdapterPosition());
            }
        });

        if (plugins.get(position).getTimer() != null) {
            holder.switchEnableApp.setVisibility(View.GONE);
            holder.progressEnableApp.setVisibility(View.VISIBLE);
        } else {
            holder.switchEnableApp.setVisibility(View.VISIBLE);
            holder.progressEnableApp.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return plugins.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageViewIcon;
        TextView textViewName;
        Switch switchEnableApp;
        ProgressBar progressEnableApp;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewIcon = itemView.findViewById(R.id.imageViewIcon);
            textViewName = itemView.findViewById(R.id.textViewName);
            switchEnableApp = itemView.findViewById(R.id.switchEnableApp);
            progressEnableApp = itemView.findViewById(R.id.progressEnableApp);
        }
    }
}
