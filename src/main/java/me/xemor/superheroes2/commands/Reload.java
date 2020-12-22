package me.xemor.superheroes2.commands;

import me.xemor.superheroes2.ConfigHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Reload implements CommandExecutor {

    private final String noPermission = ChatColor.translateAlternateColorCodes('&', "&4You do not have permission to use this power!");
    private final String reloading = ChatColor.translateAlternateColorCodes('&', "&aReloading...");
    private final String done = ChatColor.translateAlternateColorCodes('&', "&aDone!");
    private final ConfigHandler configHandler;


    public Reload(ConfigHandler configHandler) {
        this.configHandler = configHandler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("superheroes.reload")) {
            sender.sendMessage(reloading);
            configHandler.reloadConfig();
            sender.sendMessage(done);
        }
        else {
            sender.sendMessage(noPermission);
        }
        return true;
    }
}
