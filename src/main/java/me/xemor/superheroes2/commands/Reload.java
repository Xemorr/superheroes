package me.xemor.superheroes2.commands;

import me.xemor.superheroes2.data.ConfigHandler;
import me.xemor.superheroes2.data.HeroHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Reload implements SubCommand {

    private final String noPermission = ChatColor.translateAlternateColorCodes('&', "&4You do not have permission to use this power!");
    private final String reloading = ChatColor.translateAlternateColorCodes('&', "&aReloading...");
    private final String done = ChatColor.translateAlternateColorCodes('&', "&aDone!");
    private final HeroHandler heroHandler;
    private final ConfigHandler configHandler;

    public Reload(HeroHandler heroHandler, ConfigHandler configHandler) {
        this.heroHandler = heroHandler;
        this.configHandler = configHandler;
    }

    @Override
    public void onCommand(CommandSender sender,  String[] args) {
        if (sender.hasPermission("superheroes.reload")) {
            sender.sendMessage(reloading);
            configHandler.reloadConfig(heroHandler);
            sender.sendMessage(done);
        }
        else {
            sender.sendMessage(noPermission);
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
