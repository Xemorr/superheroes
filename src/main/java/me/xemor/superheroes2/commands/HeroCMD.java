package me.xemor.superheroes2.commands;

import de.themoep.minedown.adventure.MineDown;
import me.xemor.superheroes2.CooldownHandler;
import me.xemor.superheroes2.Superhero;
import me.xemor.superheroes2.Superheroes2;
import me.xemor.superheroes2.data.ConfigHandler;
import me.xemor.superheroes2.data.HeroHandler;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HeroCMD implements CommandExecutor, TabExecutor {

    private final HeroHandler heroHandler;
    private final ConfigHandler configHandler;
    private final CooldownHandler cooldownHandler;

    public HeroCMD(HeroHandler heroHandler, ConfigHandler configHandler) {
        this.heroHandler = heroHandler;
        this.configHandler = configHandler;
        cooldownHandler = new CooldownHandler("", ChatMessageType.ACTION_BAR);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Audience audience = Superheroes2.getBukkitAudiences().sender(sender);
        if (!sender.hasPermission("superheroes.hero")) {
            audience.sendMessage(MineDown.parse(configHandler.getNoPermissionMessage(), "player", sender.getName()));
            return true;
        }
        if (args.length == 0) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                Superhero superhero = heroHandler.getSuperhero(player);
                audience.sendMessage(MineDown.parse(configHandler.getCurrentHeroMessage(), "player", player.getName(), "hero", superhero.getName()));
                return true;
            }
            else {
                return false;
            }
        }
        Superhero power = heroHandler.getSuperhero(args[0]);
        if (power == null) {
            return false;
        }
        if (!sender.hasPermission("superheroes.hero." + power.getName().toLowerCase())) {
            audience.sendMessage(MineDown.parse(configHandler.getNoPermissionMessage(), "player", sender.getName()));
            return true;
        }
        if (!sender.hasPermission("superheroes.hero.bypasscooldown") && sender instanceof Player) {
            Player senderPlayer = (Player) sender;
            long seconds = cooldownHandler.getCurrentCooldown(senderPlayer.getUniqueId());
            if (seconds > 0) {
                Component message = MineDown.parse(configHandler.getHeroCooldownMessage(),
                        "player", senderPlayer.getDisplayName(),
                        "currentcooldown", String.valueOf(seconds),
                        "cooldown", String.valueOf(configHandler.getHeroCommandCooldown()));
                audience.sendMessage(message);
                return true;
            }
        }
        Player player;
        if (args.length >= 2 && sender.hasPermission("superheroes.hero.others")) {
            player = Bukkit.getPlayer(args[1]);
            if (player == null) {
                return false;
            }
        }
        else {
            if (sender instanceof Player) {
                player = (Player) sender;
            }
            else {
                return false;
            }
        }
        heroHandler.setHero(player, power);
        if (!sender.hasPermission("superheroes.hero.bypasscooldown") && sender instanceof Player) {
            UUID uuid = ((Player) sender).getUniqueId();
            cooldownHandler.startCooldown(configHandler.getHeroCommandCooldown(), uuid);
        }
        return true;
    }



    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> heroesTabComplete = new ArrayList<>();
        if (args.length == 1) {
            String firstArg = args[0];
            for (Superhero superhero : heroHandler.getNameToSuperhero().values()) {
                if (superhero.getName().startsWith(firstArg) && sender.hasPermission("superheroes.hero." + superhero.getName().toLowerCase())) {
                    heroesTabComplete.add(superhero.getName());
                }
            }
            return heroesTabComplete;
        }
        return null;
    }
}
