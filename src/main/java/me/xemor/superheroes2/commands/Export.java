package me.xemor.superheroes2.commands;

import me.xemor.superheroes2.Superheroes2;
import me.xemor.superheroes2.data.ConfigHandler;
import me.xemor.superheroes2.data.HeroHandler;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Export implements SubCommand {

    private final Component exporting = MiniMessage.miniMessage().deserialize("<green>Exporting...");
    private final Component done = MiniMessage.miniMessage().deserialize("<green>Done!");

    public Export(){

    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Audience audience = Superheroes2.getBukkitAudiences().sender(sender);
        if (sender.hasPermission("superheroes.export")) {
            audience.sendMessage(exporting);
            Superheroes2.getInstance().getHeroHandler().getHeroIOHandler().exportFiles();
            Superheroes2.getInstance().getConfigHandler().reloadConfig(Superheroes2.getInstance().getHeroHandler());
            audience.sendMessage(done);
        }
        else {
            audience.sendMessage(MiniMessage.miniMessage().deserialize(Superheroes2.getInstance().getConfigHandler().getNoPermissionMessage()));
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }

}
