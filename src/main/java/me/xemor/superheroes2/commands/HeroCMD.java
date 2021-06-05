package me.xemor.superheroes2.commands;

import de.themoep.minedown.adventure.MineDown;
import me.xemor.superheroes2.Superhero;
import me.xemor.superheroes2.Superheroes2;
import me.xemor.superheroes2.data.ConfigHandler;
import me.xemor.superheroes2.data.HeroHandler;
import me.xemor.superheroes2.data.SuperheroPlayer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HeroCMD implements CommandExecutor, TabExecutor {

    private final HeroHandler heroHandler;
    private final ConfigHandler configHandler;

    public HeroCMD(HeroHandler heroHandler, ConfigHandler configHandler) {
        this.heroHandler = heroHandler;
        this.configHandler = configHandler;
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
            SuperheroPlayer superheroPlayer = heroHandler.getSuperheroPlayer(senderPlayer);
            long seconds = getCooldownLeft(superheroPlayer) / 1000;
            if (!isCooldownOver(superheroPlayer)) {
                Component message = MineDown.parse(configHandler.getHeroCooldownMessage(),
                        "player", senderPlayer.getDisplayName(),
                        "currentcooldown", String.valueOf(Math.round(seconds)),
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
        if (!sender.hasPermission("superheroes.hero.bypasscooldown") && sender instanceof Player && sender == player) {
            SuperheroPlayer superheroPlayer = heroHandler.getSuperheroPlayer(player);
            superheroPlayer.setHeroCommandTimestamp(System.currentTimeMillis());
            heroHandler.saveSuperheroPlayer(superheroPlayer);
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

    public boolean isCooldownOver(SuperheroPlayer superheroPlayer) {
        return getCooldownLeft(superheroPlayer) <= 0;
    }

    public long getCooldownLeft(SuperheroPlayer superheroPlayer) {
        long cooldown = configHandler.getHeroCommandCooldown() * 1000;
        return cooldown - (System.currentTimeMillis() - superheroPlayer.getHeroCommandTimestamp());
    }

}
