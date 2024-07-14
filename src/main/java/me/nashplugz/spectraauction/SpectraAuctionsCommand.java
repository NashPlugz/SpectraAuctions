package me.nashplugz.spectraauction;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpectraAuctionsCommand implements CommandExecutor {

    private final SpectraAuctions plugin;

    public SpectraAuctionsCommand(SpectraAuctions plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        plugin.getAuctionGUI().openInventory(player);
        return true;
    }
}