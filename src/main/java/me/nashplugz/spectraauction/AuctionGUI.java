package me.nashplugz.spectraauction;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class AuctionGUI {
    private final SpectraAuctions plugin;
    private final List<Auction> activeAuctions;
    private Inventory inventory;

    public AuctionGUI(SpectraAuctions plugin, List<Auction> activeAuctions) {
        this.plugin = plugin;
        this.activeAuctions = activeAuctions;
        createInventory();
    }

    private void createInventory() {
        inventory = Bukkit.createInventory(null, 54, ChatColor.DARK_PURPLE + "Spectra Auctions");
        updateInventory();
    }

    public void updateInventory() {
        inventory.clear();
        for (int i = 0; i < Math.min(activeAuctions.size(), 45); i++) {
            Auction auction = activeAuctions.get(i);
            ItemStack displayItem = createDisplayItem(auction);
            inventory.setItem(i, displayItem);
        }

        // Add functional items
        inventory.setItem(49, createGuiItem(Material.COMPASS, ChatColor.YELLOW + "Search Auctions"));
        inventory.setItem(50, createGuiItem(Material.ANVIL, ChatColor.GREEN + "Create Auction"));
        inventory.setItem(48, createGuiItem(Material.CHEST, ChatColor.AQUA + "My Auctions"));
        inventory.setItem(53, createGuiItem(Material.BARRIER, ChatColor.RED + "Close"));
    }

    private ItemStack createDisplayItem(Auction auction) {
        ItemStack displayItem = auction.getItem().clone();
        ItemMeta meta = displayItem.getItemMeta();
        if (meta != null) {
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.YELLOW + "Quantity: " + auction.getQuantity());
            lore.add(ChatColor.GREEN + "Price: $" + String.format("%.2f", auction.getPrice()));
            lore.add(ChatColor.GRAY + "Seller: " + Bukkit.getOfflinePlayer(auction.getSellerId()).getName());
            lore.add(ChatColor.LIGHT_PURPLE + "Click to purchase!");
            meta.setLore(lore);
            displayItem.setItemMeta(meta);
        }
        return displayItem;
    }

    private ItemStack createGuiItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            if (lore.length > 0) {
                meta.setLore(List.of(lore));
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    public void openInventory(Player player) {
        updateInventory();
        player.openInventory(inventory);
    }

    public Auction getAuctionAt(int slot) {
        if (slot >= 0 && slot < Math.min(activeAuctions.size(), 45)) {
            return activeAuctions.get(slot);
        }
        return null;
    }

    public void removeAuction(Auction auction) {
        activeAuctions.remove(auction);
        updateInventory();
    }
}