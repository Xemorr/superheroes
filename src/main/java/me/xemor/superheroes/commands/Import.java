package me.xemor.superheroes.commands;

import me.xemor.superheroes.Superheroes;
import me.xemor.superheroes.data.ConfigHandler;
import me.xemor.superheroes.data.HeroHandler;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Import implements SubCommand {
    private final Component importing = MiniMessage.miniMessage().deserialize("<green>Importing...");
    private final Component done = MiniMessage.miniMessage().deserialize("<green>Done!");


    public Import() {

    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Audience audience = Superheroes.getBukkitAudiences().sender(sender);
        ConfigHandler configHandler = Superheroes.getInstance().getConfigHandler();
        if (sender.hasPermission("superheroes.import")) {
            audience.sendMessage(importing);
            HeroHandler heroHandler = Superheroes.getInstance().getHeroHandler();
            heroHandler.getHeroIOHandler().importFiles()
                    .thenAccept((ignored) -> Bukkit.getScheduler().runTask(heroHandler.getPlugin(), () -> configHandler.reloadConfig(heroHandler)));
            audience.sendMessage(done);
        }
        else {
            audience.sendMessage(MiniMessage.miniMessage().deserialize(configHandler.getNoPermissionMessage()));
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
