package me.nashplugz.spectraauction;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class AuctionChatListener implements Listener {
    private final SpectraAuctions plugin;

    public AuctionChatListener(SpectraAuctions plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        CreateAuctionGUI.CreateAuctionState state = plugin.getCreateAuctionGUI().getPlayerState(player);

        if (state != null) {
            event.setCancelled(true);
            String message = event.getMessage();

            if (state.getSelectedItem() != null && state.getQuantity() == 0) {
                // Setting quantity
                try {
                    int quantity = Integer.parseInt(message);
                    if (quantity > 0 && quantity <= state.getSelectedItem().getAmount()) {
                        plugin.getCreateAuctionGUI().handleQuantitySet(player, quantity);
                        player.sendMessage(ChatColor.GREEN + "Quantity set to " + quantity);
                    } else {
                        player.sendMessage(ChatColor.RED + "Invalid quantity. Please enter a number between 1 and " + state.getSelectedItem().getAmount());
                    }
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "Invalid input. Please enter a valid number.");
                }
            } else if (state.getQuantity() > 0 && state.getPrice() == 0) {
                // Setting price
                try {
                    double price = Double.parseDouble(message);
                    if (price > 0) {
                        plugin.getCreateAuctionGUI().handlePriceSet(player, price);
                        player.sendMessage(ChatColor.GREEN + "Price set to $" + price);
                    } else {
                        player.sendMessage(ChatColor.RED + "Invalid price. Please enter a positive number.");
                    }
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "Invalid input. Please enter a valid number.");
                }
            }

            plugin.getCreateAuctionGUI().openInventory(player);
        }
    }
}