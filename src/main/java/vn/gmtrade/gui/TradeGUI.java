package vn.gmtrade.gui;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import vn.gmcore.gui.ConfigGUI;
import vn.gmcore.gui.GUIItem;
import vn.gmtrade.GMTrade;
import vn.gmtrade.model.RequiredItem;
import vn.gmtrade.model.TradeItem;
import vn.gmtrade.model.TradeMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TradeGUI {

    private final GMTrade plugin;

    public TradeGUI(GMTrade plugin) {
        this.plugin = plugin;
    }

    public ConfigGUI build(TradeMenu menu, Player player) {
        ConfigGUI gui = new ConfigGUI();
        gui.setTitle(menu.getTitle().replace("&", "§"));
        gui.setLayout(menu.getLayout());

        // Add decorations
        for (Map.Entry<Character, ItemStack> entry : menu.getDecorations().entrySet()) {
            ItemStack displayItem = entry.getValue().clone();
            ItemMeta meta = displayItem.getItemMeta();
            List<String> commandsToRun = new ArrayList<>();
            if (meta != null && meta.hasLore()) {
                List<String> lore = meta.getLore();
                lore.removeIf(line -> {
                    if (line.startsWith("§0[cmd]")) {
                        commandsToRun.add(line.substring(7));
                        return true;
                    }
                    return false;
                });
                meta.setLore(lore);
                displayItem.setItemMeta(meta);
            }

            gui.setIngredient(entry.getKey(), new GUIItem(displayItem, event -> {
                event.setCancelled(true);
                if (!commandsToRun.isEmpty()) {
                    player.closeInventory();
                    for (String cmd : commandsToRun) {
                        if (cmd.startsWith("[console] ")) {
                            String consoleCmd = cmd.substring(10).replace("%player%", player.getName());
                            org.bukkit.Bukkit.dispatchCommand(org.bukkit.Bukkit.getConsoleSender(), consoleCmd);
                        } else if (cmd.startsWith("[player] ")) {
                            String playerCmd = cmd.substring(9).replace("%player%", player.getName());
                            player.performCommand(playerCmd);
                        } else {
                            player.performCommand(cmd.replace("%player%", player.getName()));
                        }
                    }
                }
            }));
        }

        // Add trades
        for (Map.Entry<Character, TradeItem> entry : menu.getTrades().entrySet()) {
            TradeItem trade = entry.getValue();
            ItemStack resultItem = getMMOItem(trade.getResultType(), trade.getResultId(), trade.getResultAmount());
            
            if (resultItem == null) continue; // If item invalid, skip or put error item

            // Add lore to show costs
            ItemMeta meta = resultItem.getItemMeta();
            if (meta != null) {
                List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
                lore.add(" ");
                lore.add(plugin.getMessageManager().getGuiText("lore-req-title"));
                if (trade.getCost().getMoney() > 0) {
                    java.text.DecimalFormat df = new java.text.DecimalFormat("#,###.##");
                    lore.add(plugin.getMessageManager().getGuiText("lore-req-money").replace("{money}", df.format(trade.getCost().getMoney())));
                }
                for (RequiredItem req : trade.getCost().getItems()) {
                    String reqName = getMMOItemName(req.getType(), req.getId());
                    lore.add(plugin.getMessageManager().getGuiText("lore-req-item").replace("{name}", reqName).replace("{amount}", String.valueOf(req.getAmount())));
                }
                lore.add(" ");
                lore.add(plugin.getMessageManager().getGuiText("lore-click"));
                meta.setLore(lore);
                resultItem.setItemMeta(meta);
            }

            gui.setIngredient(entry.getKey(), new GUIItem(resultItem, event -> {
                event.setCancelled(true);
                handleTrade(player, trade);
            }));
        }

        return gui;
    }

    private void handleTrade(Player player, TradeItem trade) {
        // Check Money
        double moneyCost = trade.getCost().getMoney();
        if (moneyCost > 0) {
            if (plugin.getEconomy() == null || plugin.getEconomy().getBalance(player) < moneyCost) {
                player.sendMessage(plugin.getMessageManager().getMsg("not-enough-money"));
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                return;
            }
        }

        // Check MMOItems
        for (RequiredItem req : trade.getCost().getItems()) {
            if (!hasMMOItem(player, req.getType(), req.getId(), req.getAmount())) {
                String reqName = getMMOItemName(req.getType(), req.getId());
                player.sendMessage(plugin.getMessageManager().getMsg("not-enough-item").replace("{name}", reqName).replace("{amount}", String.valueOf(req.getAmount())));
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                return;
            }
        }

        // Deduct Money
        if (moneyCost > 0 && plugin.getEconomy() != null) {
            plugin.getEconomy().withdrawPlayer(player, moneyCost);
        }

        // Deduct Items
        for (RequiredItem req : trade.getCost().getItems()) {
            takeMMOItem(player, req.getType(), req.getId(), req.getAmount());
        }

        // Give Result
        ItemStack reward = getMMOItem(trade.getResultType(), trade.getResultId(), trade.getResultAmount());
        if (reward != null) {
            player.getInventory().addItem(reward);
            player.sendMessage(plugin.getMessageManager().getMsg("trade-success"));
            
            org.bukkit.configuration.file.FileConfiguration config = plugin.getConfig();
            
            if (config.getBoolean("success-effects.sound.enabled", true)) {
                try {
                    String soundType = config.getString("success-effects.sound.type", "ENTITY_PLAYER_LEVELUP");
                    float volume = (float) config.getDouble("success-effects.sound.volume", 1.0);
                    float pitch = (float) config.getDouble("success-effects.sound.pitch", 1.0);
                    player.playSound(player.getLocation(), Sound.valueOf(soundType), volume, pitch);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid sound type in config!");
                }
            }
            
            if (config.getBoolean("success-effects.particle.enabled", true)) {
                String pType = config.getString("success-effects.particle.type", "FIREWORK");
                if (pType.equalsIgnoreCase("FIREWORK")) {
                    try {
                        org.bukkit.entity.Firework fw = player.getWorld().spawn(player.getLocation(), org.bukkit.entity.Firework.class);
                        org.bukkit.inventory.meta.FireworkMeta fwm = fw.getFireworkMeta();
                        
                        org.bukkit.FireworkEffect.Type type = org.bukkit.FireworkEffect.Type.valueOf(config.getString("success-effects.particle.firework-type", "BALL"));
                        
                        int r = config.getInt("success-effects.particle.color.red", 0);
                        int g = config.getInt("success-effects.particle.color.green", 255);
                        int b = config.getInt("success-effects.particle.color.blue", 0);
                        org.bukkit.Color color = org.bukkit.Color.fromRGB(r, g, b);
                        
                        int fr = config.getInt("success-effects.particle.fade.red", 255);
                        int fg = config.getInt("success-effects.particle.fade.green", 255);
                        int fb = config.getInt("success-effects.particle.fade.blue", 255);
                        org.bukkit.Color fade = org.bukkit.Color.fromRGB(fr, fg, fb);
                        
                        org.bukkit.FireworkEffect effect = org.bukkit.FireworkEffect.builder().flicker(true).withColor(color).withFade(fade).with(type).trail(true).build();
                        fwm.addEffect(effect);
                        fwm.setPower(config.getInt("success-effects.particle.power", 0));
                        fw.setFireworkMeta(fwm);
                    } catch (Exception e) {
                        plugin.getLogger().warning("Error spawning firework: " + e.getMessage());
                    }
                }
            }
        }
    }

    private ItemStack getMMOItem(String typeStr, String id, int amount) {
        Type type = Type.get(typeStr);
        if (type == null) return null;
        ItemStack item = MMOItems.plugin.getItem(type, id);
        if (item != null) {
            item.setAmount(amount);
        }
        return item;
    }

    private String getMMOItemName(String typeStr, String id) {
        ItemStack item = getMMOItem(typeStr, id, 1);
        if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            return item.getItemMeta().getDisplayName();
        }
        return typeStr + " " + id;
    }

    private boolean hasMMOItem(Player player, String typeStr, String id, int amount) {
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (isMatchingMMOItem(item, typeStr, id)) {
                count += item.getAmount();
            }
        }
        return count >= amount;
    }

    private void takeMMOItem(Player player, String typeStr, String id, int amount) {
        int remaining = amount;
        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (isMatchingMMOItem(item, typeStr, id)) {
                if (item.getAmount() <= remaining) {
                    remaining -= item.getAmount();
                    player.getInventory().setItem(i, null);
                } else {
                    item.setAmount(item.getAmount() - remaining);
                    remaining = 0;
                }
                if (remaining <= 0) break;
            }
        }
    }

    private boolean isMatchingMMOItem(ItemStack item, String typeStr, String id) {
        if (item == null || !item.hasItemMeta()) return false;
        try {
            io.lumine.mythic.lib.api.item.NBTItem nbtItem = io.lumine.mythic.lib.api.item.NBTItem.get(item);
            String mmoType = nbtItem.getString("MMOITEMS_ITEM_TYPE");
            String mmoId = nbtItem.getString("MMOITEMS_ITEM_ID");
            return typeStr.equalsIgnoreCase(mmoType) && id.equalsIgnoreCase(mmoId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
