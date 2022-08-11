package me.xemor.superheroes.commands;

import me.xemor.superheroes.Superheroes;
import me.xemor.superheroes.data.ConfigHandler;
import me.xemor.superheroes.data.HeroHandler;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Reload implements SubCommand {

    private final Component noPermission = MiniMessage.miniMessage().deserialize("<dark_red>You do not have permission to use this power!");
    private final Component reloading = MiniMessage.miniMessage().deserialize("<green>Reloading...");
    private final Component done = MiniMessage.miniMessage().deserialize("<green>Done!");
    private final HeroHandler heroHandler;
    private final ConfigHandler configHandler;

    public Reload(HeroHandler heroHandler, ConfigHandler configHandler) {
        this.heroHandler = heroHandler;
        this.configHandler = configHandler;
    }

    @Override
    public void onCommand(CommandSender sender,  String[] args) {
        Audience audience = Superheroes.getBukkitAudiences().sender(sender);
        if (sender.hasPermission("superheroes.reload")) {
            audience.sendMessage(reloading);
            configHandler.reloadConfig(heroHandler);
            audience.sendMessage(done);
        }
        else {
            audience.sendMessage(noPermission);
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
