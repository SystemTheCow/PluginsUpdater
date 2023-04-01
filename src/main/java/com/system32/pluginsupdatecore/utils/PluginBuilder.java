package com.system32.pluginsupdatecore.utils;

import com.system32.pluginsupdatecore.Main;
import com.system32.pluginsupdatecore.commands.Principal;
import org.bukkit.plugin.PluginManager;

import java.io.File;

public class PluginBuilder {
    private final Main main;

    public PluginBuilder(Main main) {
        this.main = main;
    }

    public void loadCommands(){
        main.getCommand("pluginsupdate").setExecutor(new Principal(main));
        main.getCommand("pluginsupdate").setTabCompleter(new Principal(main));
    }
}
