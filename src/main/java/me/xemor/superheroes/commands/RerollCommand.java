package me.xemor.superheroes.commands;

import me.xemor.superheroes.Superheroes;
import me.xemor.superheroes.events.PlayerChangedSuperheroEvent;
import me.xemor.superheroes.reroll.RerollGroup;
import me.xemor.superheroes.reroll.RerollHandler;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Map;

public class RerollCommand implements SubCommand, Listener {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Audience audience = Superheroes.getBukkitAudiences().sender(sender);
        if (sender.hasPermission("superheroes.hero.reroll")) {
            RerollHandler rerollHandler = Superheroes.getInstance().getRerollHandler();
            String rerollGroupName;
            if (args.length >= 2) {
                rerollGroupName = args[1];
            } else {
                rerollGroupName = "default";
            }
            if (!sender.hasPermission("superheroes.hero.reroll." + rerollGroupName)) {
                audience.sendMessage(MiniMessage.miniMessage().deserialize(Superheroes.getInstance().getConfigHandler().getNoPermissionMessage(), Placeholder.unparsed("player", sender.getName())));
                return;
            }
            RerollGroup rerollGroup = rerollHandler.getWeightedHeroes(rerollGroupName);
            if (rerollGroup == null) {
                audience.sendMessage(MiniMessage.miniMessage().deserialize(Superheroes.getInstance().getConfigHandler().getInvalidRerollGroupMessage(), Placeholder.unparsed("player", sender.getName())));
                return;
            }
            Player player;
            if (args.length >= 3) {
                player = Bukkit.getPlayer(args[2]);
                if (player == null) {
                    audience.sendMessage(MiniMessage.miniMessage().deserialize(Superheroes.getInstance().getConfigHandler().getInvalidPlayerMessage(), Placeholder.unparsed("player", sender.getName())));
                    return;
                }
            } else if (sender instanceof Player) {
                player = (Player) sender;
            } else {
                return;
            }
            Superheroes.getInstance().getHeroHandler().setHero(player, rerollGroup.chooseHero(player), true, PlayerChangedSuperheroEvent.Cause.REROLL);
        } else {
            audience.sendMessage(MiniMessage.miniMessage().deserialize(Superheroes.getInstance().getConfigHandler().getNoPermissionMessage()));
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return Superheroes.getInstance().getRerollHandler().getIterator().stream().map(Map.Entry::getKey).toList();
        }
        return null;
    }
}

