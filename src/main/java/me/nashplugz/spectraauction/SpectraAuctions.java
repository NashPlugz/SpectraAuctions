package me.nashplugz.spectraauction;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SpectraAuctions extends JavaPlugin {

    private AuctionGUI auctionGUI;
    private CreateAuctionGUI createAuctionGUI;
    private List<Auction> activeAuctions;
    private File auctionsFile;
    private FileConfiguration auctionsConfig;
    private Economy economy;

    @Override
    public void onEnable() {
        getLogger().info("SpectraAuctions plugin is starting...");

        try {
            // Register Auction class for serialization
            ConfigurationSerialization.registerClass(Auction.class);

            if (!setupEconomy()) {
                getLogger().severe("Disabled due to no Vault dependency found!");
                getServer().getPluginManager().disablePlugin(this);
                return;
            }

            loadAuctions();
            this.auctionGUI = new AuctionGUI(this, activeAuctions);
            this.createAuctionGUI = new CreateAuctionGUI(this);

            SpectraAuctionsCommand commandExecutor = new SpectraAuctionsCommand(this);
            getCommand("au").setExecutor(commandExecutor);

            getServer().getPluginManager().registerEvents(new AuctionGUIListener(this), this);
            getServer().getPluginManager().registerEvents(new AuctionChatListener(this), this);

            getLogger().info("SpectraAuctions plugin has been enabled successfully!");
        } catch (Exception e) {
            getLogger().severe("Error occurred while enabling SpectraAuctions: " + e.getMessage());
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        saveAuctions();
        getLogger().info("SpectraAuctions plugin has been disabled!");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    private void loadAuctions() {
        auctionsFile = new File(getDataFolder(), "auctions.yml");
        if (!auctionsFile.exists()) {
            auctionsFile.getParentFile().mkdirs();
            try {
                auctionsFile.createNewFile();
                // Initialize with empty auctions list
                YamlConfiguration config = new YamlConfiguration();
                config.set("auctions", new ArrayList<>());
                config.save(auctionsFile);
            } catch (IOException e) {
                getLogger().severe("Could not create auctions.yml file!");
                e.printStackTrace();
            }
        }

        auctionsConfig = YamlConfiguration.loadConfiguration(auctionsFile);
        try {
            activeAuctions = (List<Auction>) auctionsConfig.getList("auctions", new ArrayList<>());
        } catch (Exception e) {
            getLogger().warning("Failed to load auctions from file. Starting with empty list.");
            activeAuctions = new ArrayList<>();
        }
    }

    private void saveAuctions() {
        if (auctionsConfig == null) {
            getLogger().warning("auctionsConfig is null, cannot save auctions.");
            return;
        }
        auctionsConfig.set("auctions", activeAuctions);
        try {
            auctionsConfig.save(auctionsFile);
        } catch (IOException e) {
            getLogger().severe("Could not save auctions to file!");
            e.printStackTrace();
        }
    }

    public AuctionGUI getAuctionGUI() {
        return auctionGUI;
    }

    public CreateAuctionGUI getCreateAuctionGUI() {
        return createAuctionGUI;
    }

    public List<Auction> getActiveAuctions() {
        return activeAuctions;
    }

    public Economy getEconomy() {
        return economy;
    }

    public void addAuction(Auction auction) {
        activeAuctions.add(auction);
        auctionGUI.updateInventory();
        saveAuctions();
    }

    public void removeAuction(Auction auction) {
        activeAuctions.remove(auction);
        auctionGUI.updateInventory();
        saveAuctions();
    }
}