package me.xemor.superheroes.commands;

import me.xemor.superheroes.Superheroes;
import me.xemor.superheroes.data.ConfigHandler;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ExportCommand implements SubCommand {

    private final Component exporting = MiniMessage.miniMessage().deserialize("<green>Exporting...");
    private final Component done = MiniMessage.miniMessage().deserialize("<green>Done!");

    public ExportCommand() {

    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Audience audience = Superheroes.getBukkitAudiences().sender(sender);
        if (sender.hasPermission("superheroes.hero.export")) {
            audience.sendMessage(exporting);
            Superheroes.getInstance().getHeroHandler().getHeroIOHandler().exportFiles();
            Superheroes.getInstance().getConfigHandler().reloadConfig(Superheroes.getInstance().getHeroHandler());
            audience.sendMessage(done);
        }
        else {
            audience.sendMessage(MiniMessage.miniMessage().deserialize(ConfigHandler.getLanguageYAML().chatLanguageSettings().getNoPermission()));
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }

}
