package me.xemor.superheroes.commands;

import me.xemor.superheroes.Superheroes;
import me.xemor.superheroes.data.HeroHandler;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.List;

public class GUI implements SubCommand {
    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Audience audience = Superheroes.getBukkitAudiences().sender(sender);
        if (!sender.hasPermission("superheroes.hero")) {
            audience.sendMessage(MiniMessage.miniMessage().deserialize(Superheroes.getInstance().getConfigHandler().getNoPermissionMessage(), Placeholder.unparsed("player", sender.getName())));
            return;
        }
        HeroHandler heroHandler = Superheroes.getInstance().getHeroHandler();
        if (args.length < 2) {
            if (sender instanceof Player player) {
                heroHandler.openHeroGUI(player);
            }
            return;
        }
        Player player = Bukkit.getPlayer(args[1]);
        if (player == null) {
            var component = MiniMessage.miniMessage().deserialize(Superheroes.getInstance().getConfigHandler().getInvalidPlayerMessage(), Placeholder.unparsed("player", sender.getName()));
            audience.sendMessage(component);
            return;
        }
        heroHandler.openHeroGUI(player);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).toList();
    }
}
