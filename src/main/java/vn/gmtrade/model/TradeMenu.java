package vn.gmtrade.model;

import org.bukkit.inventory.ItemStack;
import java.util.List;
import java.util.Map;

public class TradeMenu {
    private final String id;
    private final String title;
    private final List<String> layout;
    private final Map<Character, TradeItem> trades;
    private final Map<Character, ItemStack> decorations;

    public TradeMenu(String id, String title, List<String> layout, Map<Character, TradeItem> trades, Map<Character, ItemStack> decorations) {
        this.id = id;
        this.title = title;
        this.layout = layout;
        this.trades = trades;
        this.decorations = decorations;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public List<String> getLayout() { return layout; }
    public Map<Character, TradeItem> getTrades() { return trades; }
    public Map<Character, ItemStack> getDecorations() { return decorations; }
}
