package me.nashplugz.spectraauction;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SerializableAs("Auction")
public class Auction implements ConfigurationSerializable {
    private final UUID sellerId;
    private final ItemStack item;
    private final int quantity;
    private final double price;

    public Auction(Player seller, ItemStack item, int quantity, double price) {
        this.sellerId = seller.getUniqueId();
        this.item = item;
        this.quantity = quantity;
        this.price = price;
    }

    public Auction(UUID sellerId, ItemStack item, int quantity, double price) {
        this.sellerId = sellerId;
        this.item = item;
        this.quantity = quantity;
        this.price = price;
    }

    public UUID getSellerId() {
        return sellerId;
    }

    public ItemStack getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public Player getSeller() {
        return Bukkit.getPlayer(sellerId);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new HashMap<>();
        result.put("sellerId", sellerId.toString());
        result.put("item", item);
        result.put("quantity", quantity);
        result.put("price", price);
        return result;
    }

    public static Auction deserialize(Map<String, Object> args) {
        UUID sellerId = UUID.fromString((String) args.get("sellerId"));
        ItemStack item = (ItemStack) args.get("item");
        int quantity = (int) args.get("quantity");
        double price = (double) args.get("price");
        return new Auction(sellerId, item, quantity, price);
    }
}