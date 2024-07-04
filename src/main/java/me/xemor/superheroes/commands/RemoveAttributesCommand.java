package me.xemor.superheroes.commands;

import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.Superheroes;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RemoveAttributesCommand implements SubCommand {
    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (sender.hasPermission("superheroes.hero.removeattributes")) {
            if (args.length == 1) {
                if (sender instanceof Player player) {
                    removeAttributes(player);
                }
            }
            if (args.length == 2) {
                String name = args[1];
                Player player = Bukkit.getPlayer(name);
                if (player != null) {
                    removeAttributes(player);
                }
            }
        }
    }

    public void removeAttributes(Player player) {
        for (Attribute attribute : Attribute.values()) {
            AttributeInstance attributeInstance = player.getAttribute(attribute);
            if (attributeInstance != null) {
                List<AttributeModifier> modifiers = new ArrayList<>();
                attributeInstance.getModifiers().forEach((am) -> {
                    if (am.getKey().getNamespace().equalsIgnoreCase(Superheroes.getInstance().getName())) {
                        modifiers.add(am);
                    }
                });
                for (AttributeModifier modifier : modifiers) {
                    attributeInstance.removeModifier(modifier);
                }
            }
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> heroesTabComplete = new ArrayList<>();
        if (args.length == 2) {
            String secondArg = args[1];
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().startsWith(secondArg)) {
                    heroesTabComplete.add(player.getName());
                }
            }
            return heroesTabComplete;
        }
        return null;
    }
}
