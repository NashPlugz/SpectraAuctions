package me.nashplugz.spectraauction;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class AuctionGUIListener implements Listener {
    private final SpectraAuctions plugin;

    public AuctionGUIListener(SpectraAuctions plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedInventory == null || clickedItem == null) {
            return;
        }

        if (event.getView().getTitle().equals(ChatColor.DARK_PURPLE + "Spectra Auctions")) {
            event.setCancelled(true);
            handleMainAuctionGUI(player, clickedItem, event.getRawSlot());
        } else if (event.getView().getTitle().equals(ChatColor.DARK_PURPLE + "Create Auction")) {
            event.setCancelled(true);
            handleCreateAuctionGUI(player, clickedItem, event.getRawSlot());
        }
    }

    private void handleMainAuctionGUI(Player player, ItemStack clickedItem, int slot) {
        if (slot < 45) {
            Auction auction = plugin.getAuctionGUI().getAuctionAt(slot);
            if (auction != null) {
                // Handle auction purchase
                handleAuctionPurchase(player, auction);
            }
        } else {
            switch (slot) {
                case 49:
                    // Search auctions
                    player.sendMessage(ChatColor.YELLOW + "Search functionality coming soon!");
                    break;
                case 50:
                    // Create auction
                    plugin.getCreateAuctionGUI().openInventory(player);
                    break;
                case 48:
                    // My auctions
                    player.sendMessage(ChatColor.AQUA + "My auctions functionality coming soon!");
                    break;
                case 53:
                    // Close
                    player.closeInventory();
                    break;
            }
        }
    }

    private void handleCreateAuctionGUI(Player player, ItemStack clickedItem, int slot) {
        CreateAuctionGUI.CreateAuctionState state = plugin.getCreateAuctionGUI().getPlayerState(player);
        if (state == null) {
            return;
        }

        switch (slot) {
            case 13:
                // Select item
                ItemStack cursorItem = player.getItemOnCursor();
                if (cursorItem != null && !cursorItem.getType().isAir()) {
                    plugin.getCreateAuctionGUI().handleItemSelection(player, cursorItem.clone());
                    player.setItemOnCursor(null);
                }
                break;
            case 11:
                // Set quantity
                player.sendMessage(ChatColor.YELLOW + "Please enter the quantity in chat.");
                player.closeInventory();
                break;
            case 15:
                // Set price
                player.sendMessage(ChatColor.YELLOW + "Please enter the price in chat.");
                player.closeInventory();
                break;
            case 26:
                // Create auction
                plugin.getCreateAuctionGUI().handleCreateAuction(player);
                break;
        }
    }

    private void handleAuctionPurchase(Player player, Auction auction) {
        if (auction.getSellerId().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You cannot buy your own auction!");
            return;
        }

        double price = auction.getPrice();
        if (plugin.getEconomy().has(player, price)) {
            plugin.getEconomy().withdrawPlayer(player, price);
            plugin.getEconomy().depositPlayer(plugin.getServer().getOfflinePlayer(auction.getSellerId()), price);
            player.getInventory().addItem(auction.getItem());
            player.sendMessage(ChatColor.GREEN + "You have successfully purchased the auction for $" + price);
            Player seller = plugin.getServer().getPlayer(auction.getSellerId());
            if (seller != null) {
                seller.sendMessage(ChatColor.GREEN + "Your auction has been sold for $" + price);
            }
            plugin.removeAuction(auction);
        } else {
            player.sendMessage(ChatColor.RED + "You don't have enough money to purchase this auction!");
        }
    }
}

