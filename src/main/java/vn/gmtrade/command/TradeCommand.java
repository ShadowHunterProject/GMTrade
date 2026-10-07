package vn.gmtrade.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Player;
import vn.gmcore.GMCore;
import vn.gmcore.gui.ConfigGUI;
import vn.gmtrade.GMTrade;
import vn.gmtrade.model.TradeMenu;

public class TradeCommand implements TabExecutor {

    private final GMTrade plugin;

    public TradeCommand(GMTrade plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 3 && args[0].equalsIgnoreCase("open")) {
            if (!sender.hasPermission("gmtrade.admin")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission!");
                return true;
            }
            
            String menuId = args[1];
            String playerName = args[2];
            
            Player target = Bukkit.getPlayer(playerName);
            if (target == null || !target.isOnline()) {
                sender.sendMessage(plugin.getMessageManager().getMsg("player-offline"));
                return true;
            }
            
            TradeMenu menu = plugin.getTradeManager().getMenu(menuId);
            if (menu == null) {
                sender.sendMessage(plugin.getMessageManager().getMsg("menu-not-found").replace("{menu}", menuId));
                return true;
            }
            
            ConfigGUI gui = plugin.getTradeGUI().build(menu, target);
            GMCore.getInstance().getGuiManager().openGUI(target, gui);
            sender.sendMessage(plugin.getMessageManager().getMsg("menu-opened").replace("{menu}", menuId).replace("{player}", target.getName()));
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("gmtrade.admin")) {
                sender.sendMessage(plugin.getMessageManager().getMsg("no-permission"));
                return true;
            }
            plugin.getMessageManager().loadMessages();
            plugin.getTradeManager().loadMenus();
            sender.sendMessage(plugin.getMessageManager().getMsg("reload-success"));
            return true;
        }

        sender.sendMessage(plugin.getMessageManager().getMsg("usage-open"));
        sender.sendMessage(plugin.getMessageManager().getMsg("usage-reload"));
        return true;
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        java.util.List<String> completions = new java.util.ArrayList<>();
        if (!sender.hasPermission("gmtrade.admin")) {
            return completions;
        }

        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            if ("open".startsWith(partial)) completions.add("open");
            if ("reload".startsWith(partial)) completions.add("reload");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("open")) {
            String partial = args[1].toLowerCase();
            for (String menuId : plugin.getTradeManager().getMenuIds()) {
                if (menuId.toLowerCase().startsWith(partial)) {
                    completions.add(menuId);
                }
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("open")) {
            String partial = args[2].toLowerCase();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase().startsWith(partial)) {
                    completions.add(p.getName());
                }
            }
        }

        return completions;
    }
}
