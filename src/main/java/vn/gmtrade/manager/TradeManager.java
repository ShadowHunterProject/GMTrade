package vn.gmtrade.manager;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import vn.gmtrade.GMTrade;
import vn.gmtrade.model.RequiredItem;
import vn.gmtrade.model.TradeCost;
import vn.gmtrade.model.TradeItem;
import vn.gmtrade.model.TradeMenu;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TradeManager {

    private final GMTrade plugin;
    private final Map<String, TradeMenu> menus = new HashMap<>();

    public TradeManager(GMTrade plugin) {
        this.plugin = plugin;
    }

    public void loadMenus() {
        menus.clear();
        File menusFolder = new File(plugin.getDataFolder(), "menus");
        if (!menusFolder.exists()) {
            menusFolder.mkdirs();
            plugin.saveResource("menus/example.yml", false);
        }

        File[] files = menusFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            String menuId = file.getName().replace(".yml", "");
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            loadMenu(menuId, config);
        }
        plugin.getLogger().info("Loaded " + menus.size() + " menus.");
    }

    private void loadMenu(String menuId, FileConfiguration config) {
        String title = config.getString("title", "Trade Menu");
        List<String> layout = config.getStringList("layout");
        
        Map<Character, TradeItem> trades = new HashMap<>();
        ConfigurationSection tradesSection = config.getConfigurationSection("trades");
        if (tradesSection != null) {
            for (String symbolStr : tradesSection.getKeys(false)) {
                if (symbolStr.length() != 1) continue;
                char symbol = symbolStr.charAt(0);
                ConfigurationSection tradeSec = tradesSection.getConfigurationSection(symbolStr);
                
                String resultType = tradeSec.getString("result.type");
                String resultId = tradeSec.getString("result.id");
                int resultAmount = tradeSec.getInt("result.amount", 1);
                
                double moneyCost = tradeSec.getDouble("cost.money", 0);
                List<RequiredItem> reqItems = new ArrayList<>();
                ConfigurationSection itemsSec = tradeSec.getConfigurationSection("cost.items");
                if (itemsSec != null) {
                    for (String key : itemsSec.getKeys(false)) {
                        String type = itemsSec.getString(key + ".type");
                        String id = itemsSec.getString(key + ".id");
                        int amount = itemsSec.getInt(key + ".amount", 1);
                        reqItems.add(new RequiredItem(type, id, amount));
                    }
                }
                
                TradeCost cost = new TradeCost(moneyCost, reqItems);
                trades.put(symbol, new TradeItem(symbol, resultType, resultId, resultAmount, cost));
            }
        }

        Map<Character, ItemStack> decorations = new HashMap<>();
        ConfigurationSection decoSection = config.getConfigurationSection("decorations");
        if (decoSection != null) {
            for (String symbolStr : decoSection.getKeys(false)) {
                if (symbolStr.length() != 1) continue;
                char symbol = symbolStr.charAt(0);
                ConfigurationSection itemSec = decoSection.getConfigurationSection(symbolStr);
                
                Material mat = Material.matchMaterial(itemSec.getString("material", "STONE"));
                if (mat == null) mat = Material.STONE;
                ItemStack item = new ItemStack(mat);
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    if (itemSec.contains("name")) {
                        meta.setDisplayName(itemSec.getString("name").replace("&", "§"));
                    }
                    if (itemSec.contains("lore")) {
                        List<String> lore = new ArrayList<>();
                        for (String l : itemSec.getStringList("lore")) {
                            lore.add(l.replace("&", "§"));
                        }
                        meta.setLore(lore);
                    }
                    if (itemSec.contains("command")) {
                        List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
                        if (itemSec.isList("command")) {
                            for (String cmd : itemSec.getStringList("command")) {
                                lore.add("§0[cmd]" + cmd);
                            }
                        } else {
                            lore.add("§0[cmd]" + itemSec.getString("command"));
                        }
                        meta.setLore(lore);
                    }
                    item.setItemMeta(meta);
                }
                decorations.put(symbol, item);
            }
        }

        TradeMenu menu = new TradeMenu(menuId, title, layout, trades, decorations);
        menus.put(menuId, menu);
    }

    public TradeMenu getMenu(String id) {
        return menus.get(id);
    }

    public java.util.Set<String> getMenuIds() {
        return menus.keySet();
    }
}
