package me.xemor.superheroes2.commands;

import de.themoep.minedown.adventure.MineDown;
import me.xemor.superheroes2.Superheroes2;
import me.xemor.superheroes2.data.ConfigHandler;
import me.xemor.superheroes2.data.HeroHandler;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Import implements SubCommand {

    private final String importing = ChatColor.translateAlternateColorCodes('&', "&aImporting...");
    private final String done = ChatColor.translateAlternateColorCodes('&', "&aDone!");
    private HeroHandler heroHandler;
    private ConfigHandler configHandler;

    public Import(HeroHandler heroHandler, ConfigHandler configHandler) {
        this.heroHandler = heroHandler;
        this.configHandler = configHandler;
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (sender.hasPermission("superheroes.import")) {
            sender.sendMessage(importing);
            heroHandler.getHeroIOHandler().importFiles().thenAccept((ignored) -> Bukkit.getScheduler().runTask(heroHandler.getPlugin(), () -> configHandler.reloadConfig(heroHandler)));
            sender.sendMessage(done);
        }
        else {
            Audience audience = Superheroes2.getBukkitAudiences().sender(sender);
            audience.sendMessage(MineDown.parse(configHandler.getNoPermissionMessage()));
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
