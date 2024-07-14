package me.nashplugz.spectraauction;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateAuctionGUI {
    private final SpectraAuctions plugin;
    private final Inventory inventory;
    private final Map<Player, CreateAuctionState> playerStates;

    public CreateAuctionGUI(SpectraAuctions plugin) {
        this.plugin = plugin;
        this.inventory = Bukkit.createInventory(null, 27, ChatColor.DARK_PURPLE + "Create Auction");
        this.playerStates = new HashMap<>();
        initializeInventory();
    }

    private void initializeInventory() {
        // Set up the GUI layout
        inventory.setItem(13, createGuiItem(Material.CHEST, ChatColor.YELLOW + "Click to select an item"));
        inventory.setItem(15, createGuiItem(Material.PAPER, ChatColor.GREEN + "Set Price"));
        inventory.setItem(11, createGuiItem(Material.HOPPER, ChatColor.AQUA + "Set Quantity"));
        inventory.setItem(26, createGuiItem(Material.EMERALD, ChatColor.GOLD + "Create Auction"));
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
        player.openInventory(inventory);
        playerStates.put(player, new CreateAuctionState());
    }

    public void handleItemSelection(Player player, ItemStack selectedItem) {
        CreateAuctionState state = playerStates.get(player);
        if (state != null) {
            state.setSelectedItem(selectedItem);
            updateInventory(player);
        }
    }

    public void handleQuantitySet(Player player, int quantity) {
        CreateAuctionState state = playerStates.get(player);
        if (state != null) {
            state.setQuantity(quantity);
            updateInventory(player);
        }
    }

    public void handlePriceSet(Player player, double price) {
        CreateAuctionState state = playerStates.get(player);
        if (state != null) {
            state.setPrice(price);
            updateInventory(player);
        }
    }

    public void handleCreateAuction(Player player) {
        CreateAuctionState state = playerStates.get(player);
        if (state != null && state.isComplete()) {
            Auction newAuction = new Auction(player, state.getSelectedItem(), state.getQuantity(), state.getPrice());
            plugin.addAuction(newAuction);
            player.sendMessage(ChatColor.GREEN + "Auction created successfully!");
            player.closeInventory();
            playerStates.remove(player);
        } else {
            player.sendMessage(ChatColor.RED + "Please complete all fields before creating the auction.");
        }
    }

    private void updateInventory(Player player) {
        CreateAuctionState state = playerStates.get(player);
        if (state != null) {
            Inventory playerInventory = player.getOpenInventory().getTopInventory();

            if (state.getSelectedItem() != null) {
                playerInventory.setItem(13, state.getSelectedItem());
            }

            ItemStack quantityItem = createGuiItem(Material.HOPPER,
                    ChatColor.AQUA + "Quantity: " + (state.getQuantity() > 0 ? state.getQuantity() : "Not set"));
            playerInventory.setItem(11, quantityItem);

            ItemStack priceItem = createGuiItem(Material.PAPER,
                    ChatColor.GREEN + "Price: " + (state.getPrice() > 0 ? "$" + state.getPrice() : "Not set"));
            playerInventory.setItem(15, priceItem);
        }
    }

    public CreateAuctionState getPlayerState(Player player) {
        return playerStates.get(player);
    }

    public void removePlayerState(Player player) {
        playerStates.remove(player);
    }

    public static class CreateAuctionState {
        private ItemStack selectedItem;
        private int quantity;
        private double price;

        public void setSelectedItem(ItemStack selectedItem) {
            this.selectedItem = selectedItem;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public ItemStack getSelectedItem() {
            return selectedItem;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getPrice() {
            return price;
        }

        public boolean isComplete() {
            return selectedItem != null && quantity > 0 && price > 0;
        }
    }
}