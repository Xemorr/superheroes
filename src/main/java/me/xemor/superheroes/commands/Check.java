package me.xemor.superheroes.commands;

import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.Superheroes;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class Check implements SubCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Audience audience = Superheroes.getBukkitAudiences().sender(sender);
        if (sender.hasPermission("superheroes.check")) {
            if (args.length > 1) {
                if (sender.hasPermission("superheroes.check.others")) {
                    Player other = Bukkit.getPlayer(args[1]);
                    if (other == null) {
                        audience.sendMessage(MiniMessage.miniMessage().deserialize(Superheroes.getInstance().getConfigHandler().getInvalidPlayerMessage(),
                                Placeholder.unparsed("player", sender.getName())));
                        return;
                    }
                    Superhero hero = Superheroes.getInstance().getHeroHandler().getSuperhero(other);
                    audience.sendMessage(
                            MiniMessage.miniMessage().deserialize(Superheroes.getInstance().getConfigHandler().getCurrentHeroMessage(),
                                    Placeholder.unparsed("player", other.getName()),
                                    Placeholder.unparsed("hero", hero.getName()))
                    );
                }
                else {
                    audience.sendMessage(MiniMessage.miniMessage().deserialize(Superheroes.getInstance().getConfigHandler().getNoPermissionMessage(),
                            Placeholder.unparsed("player", sender.getName())));
                }
            }
            else {
                if (sender instanceof Player player) {
                    Superhero hero = Superheroes.getInstance().getHeroHandler().getSuperhero(player);
                    audience.sendMessage(
                            MiniMessage.miniMessage().deserialize(Superheroes.getInstance().getConfigHandler().getCurrentHeroMessage(),
                                    Placeholder.unparsed("player", player.getName()),
                                    Placeholder.unparsed("hero", hero.getName()))
                    );
                }
            }
        }
        else {
            audience.sendMessage(MiniMessage.miniMessage().deserialize(Superheroes.getInstance().getConfigHandler().getNoPermissionMessage(),
                    Placeholder.unparsed("player", sender.getName())));
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
