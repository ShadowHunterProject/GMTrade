package vn.gmtrade;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import vn.gmtrade.command.TradeCommand;
import vn.gmtrade.gui.TradeGUI;
import vn.gmtrade.manager.MessageManager;
import vn.gmtrade.manager.TradeManager;

public class GMTrade extends JavaPlugin {

    private static GMTrade instance;
    private Economy econ = null;
    private TradeManager tradeManager;
    private MessageManager messageManager;
    private TradeGUI tradeGUI;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        
        if (!setupEconomy() ) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        messageManager = new MessageManager(this);
        messageManager.loadMessages();

        tradeManager = new TradeManager(this);
        tradeManager.loadMenus();
        
        tradeGUI = new TradeGUI(this);

        getCommand("gmtrade").setExecutor(new TradeCommand(this));

        getLogger().info("GMTrade enabled successfully!");
    }

    @Override
    public void onDisable() {
        getLogger().info("GMTrade disabled.");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static GMTrade getInstance() {
        return instance;
    }

    public Economy getEconomy() {
        return econ;
    }

    public TradeManager getTradeManager() {
        return tradeManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public TradeGUI getTradeGUI() {
        return tradeGUI;
    }
}
