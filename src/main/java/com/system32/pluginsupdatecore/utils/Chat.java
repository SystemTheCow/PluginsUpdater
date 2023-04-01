package com.system32.pluginsupdatecore.utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Chat {
    public static String plugin_prefix = "&9&lPluginsUpdateCore &8&l» &f";

    public static void messagePlayer(Player player, String message, Boolean prefix){
        if(prefix){
            player.sendMessage(colorMessage(plugin_prefix + message));
            return;
        }
        player.sendMessage(colorMessage(message));
    }

    public static void messageConsole(String message, Boolean prefix){
        if(prefix){
            Bukkit.getConsoleSender().sendMessage(colorMessage(plugin_prefix + message));
            return;
        }
        Bukkit.getConsoleSender().sendMessage(colorMessage(message));
    }
    public static void debug(String title, String message){
        Bukkit.getConsoleSender().sendMessage(colorMessage(plugin_prefix + "&c&lDEBUG-"+title.toUpperCase() + ": &f" +message));
    }
    public static Boolean checkPerms(Player player, String perm){
        Boolean havePerm = player.hasPermission(perm);
        if(!havePerm){
            messagePlayer(player,  "&cYou don't have permission to use this, you need the permission &e" + perm, true);
        }
        return havePerm;
    }
    public static void forceCommand(String command){
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }
    public static void forceCommand(Player player, String command){
        Bukkit.dispatchCommand(player, command);
    }
    public static String colorMessage(String message){
        Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        Matcher match = pattern.matcher(message);
        while(match.find()){
            String color = message.substring(match.start(), match.end());
            message = message.replace(color, ChatColor.of(color)+"");
            match = pattern.matcher(message);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
