package me.xemor.superheroes2.commands;

import me.xemor.superheroes2.Superheroes2;
import me.xemor.superheroes2.data.ConfigHandler;
import me.xemor.superheroes2.data.HeroHandler;
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
        Audience audience = Superheroes2.getBukkitAudiences().sender(sender);
        ConfigHandler configHandler = Superheroes2.getInstance().getConfigHandler();
        if (sender.hasPermission("superheroes.import")) {
            audience.sendMessage(importing);
            HeroHandler heroHandler = Superheroes2.getInstance().getHeroHandler();
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
