package vn.gmtrade.manager;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import vn.gmtrade.GMTrade;

import java.io.File;

public class MessageManager {

    private final GMTrade plugin;
    private FileConfiguration config;
    private String prefix;

    public MessageManager(GMTrade plugin) {
        this.plugin = plugin;
    }

    public void loadMessages() {
        File file = new File(plugin.getDataFolder(), "message.yml");
        if (!file.exists()) {
            plugin.saveResource("message.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(file);
        prefix = ChatColor.translateAlternateColorCodes('&', config.getString("prefix", "&8[&eGMTrade&8] &r"));
    }

    public String getMsg(String path) {
        if (config == null) return "";
        String msg = config.getString("messages." + path);
        if (msg == null) return "Missing message: " + path;
        return prefix + ChatColor.translateAlternateColorCodes('&', msg);
    }

    public String getGuiText(String path) {
        if (config == null) return "";
        String msg = config.getString("gui." + path);
        if (msg == null) return "Missing gui text: " + path;
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}
