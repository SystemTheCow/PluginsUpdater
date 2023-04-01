package com.system32.pluginsupdatecore;

import com.system32.pluginsupdatecore.utils.Chat;
import com.system32.pluginsupdatecore.utils.PluginBuilder;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    public void onEnable() {
        PluginBuilder pluginBuilder = new PluginBuilder(this);
        pluginBuilder.loadCommands();
        Chat.messageConsole("Plugin loaded!", true);
    }
}
