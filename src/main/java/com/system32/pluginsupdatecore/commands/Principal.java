package com.system32.pluginsupdatecore.commands;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.system32.pluginsupdatecore.Main;
import com.system32.pluginsupdatecore.utils.Chat;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.StringUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class Principal implements CommandExecutor, TabCompleter {
    private final Main main;

    public Principal(Main main) {
        this.main = main;
    }

    private Map<String, String> plugins = new HashMap<>();
    private final String[] COMMANDS = {"update", "plugins"};
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        final List<String> completions = new ArrayList<>();
        StringUtil.copyPartialMatches(args[0], Arrays.asList(COMMANDS), completions);
        Collections.sort(completions);
        return completions;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if((sender instanceof Player)){
            Player player = (Player) sender;
            if(Chat.checkPerms(player, "updatecore.commands")){
                if(args.length == 0) {
                    player.sendMessage(Chat.colorMessage(Chat.plugin_prefix + "Comandos disponibles:"));
                    player.sendMessage(Chat.colorMessage("&7&l» &c/pluginsupdate update &7- &fActualiza todos los plugins"));
                    player.sendMessage(Chat.colorMessage("&7&l» &c/pluginsupdate plugins &7- &fMuestra los plugins disponibles en el servidor de Github"));
                    return false;
                }
                if(args[0].equalsIgnoreCase("update")){
                    plugins.clear();
                    getPlugins();
                    plugins.forEach((plugin, version) -> {
                        if(hasPlugin(plugin)){
                            int parseActualVersion = Integer.parseInt(getPluginVer(plugin).replace(".", ""));
                            int parseGithubVersion = Integer.parseInt(version.replace(".", ""));
                            if(parseActualVersion < parseGithubVersion){
                                updatePlugin(version, plugin, player, false);
                            }else{
                                Chat.messagePlayer(player,"No hay actualizaciones disponibles para &c" + plugin + " &9(&f"+ getPluginVer(plugin)+"&9)", true);
                            }
                        }else{
                            updatePlugin(version, plugin, player, true);
                        }
                    });
                }else if(args[0].equalsIgnoreCase("plugins")){
                    plugins.clear();
                    getPlugins();
                    player.sendMessage(Chat.colorMessage(Chat.plugin_prefix + "Plugins Actuales en el Servidor de Github"));
                    plugins.forEach((plugin, version) -> {
                        if(hasPlugin(plugin)){
                            player.sendMessage(Chat.colorMessage(String.format("&7&l» &c%s &9(&f%s&7/&e%s&9)", plugin.toLowerCase(), getPluginVer(plugin), version)));
                        }else{
                            player.sendMessage(Chat.colorMessage("&7&l» &c" + plugin.toLowerCase() + " &9(&f" + version + "&9)"));
                        }
                    });
                }else{
                    return false;
                }
            }

        }
        return false;
    }
    private Boolean hasPlugin(String plugin){
        return Bukkit.getServer().getPluginManager().getPlugin(plugin) != null;
    }
    private String getPluginVer(String plugin){
        return Bukkit.getServer().getPluginManager().getPlugin(plugin).getDescription().getVersion();
    }
    private void getPlugins() {
        try {
            URL api = new URL("https://api.github.com/repos/SystemTheCow/PluginsUpdater/releases/latest");
            URLConnection con = api.openConnection();
            con.setConnectTimeout(15000);
            con.setReadTimeout(15000);

            JsonObject json = new JsonParser().parse(new InputStreamReader(con.getInputStream())).getAsJsonObject();
            json.get("assets").getAsJsonArray().forEach(jsonElement -> {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                String plugin_name = jsonObject.get("name").getAsString().split("-")[0];
                String plugin_version = jsonObject.get("name").getAsString().split("-")[1].split(".j")[0];
                plugins.put(plugin_name, plugin_version);
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void updatePlugin(String github_version, String plugin, Player player, Boolean isNew){
        try {
            String tagname = null;
            URL api = new URL("https://api.github.com/repos/SystemTheCow/PluginsUpdater/releases/latest");
            URLConnection con = api.openConnection();
            con.setConnectTimeout(15000);
            con.setReadTimeout(15000);

            JsonObject json = new JsonParser().parse(new InputStreamReader(con.getInputStream())).getAsJsonObject();
            tagname = json.get("tag_name").getAsString();

            URL download = new URL("https://github.com/SystemTheCow/PluginsUpdater/releases/download/" + tagname + "/"+ plugin + "-"+github_version+".jar");
                if(isNew){
                    Chat.messagePlayer(player,"El plugin &c" + plugin + " &fno se encuentra en el servidor, descargando ahora... &9(&f" + github_version +
                            "&9)", true);
                }else{
                    Chat.messagePlayer(player,"Hay una nueva versión de &c" + plugin + " &fdisponible, descargando ahora... &9(&f" + github_version +
                            "&9)", true);
                }


                new BukkitRunnable(){

                    @Override
                    public void run() {
                        try {
                            InputStream in = download.openStream();
                            File temp = new File("plugins");
                            if (!temp.exists()) {
                                temp.mkdir();
                            }
                            Path path = new File("plugins" + File.separator + plugin+ ".jar").toPath();
                            Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
                        }catch(IOException e){
                            e.printStackTrace();
                        }
                    }
                }.runTaskLaterAsynchronously(main, 0);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
