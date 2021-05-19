package me.xemor.superheroes2.commands;

import de.themoep.minedown.MineDown;
import me.xemor.superheroes2.ConfigHandler;
import me.xemor.superheroes2.HeroHandler;
import me.xemor.superheroes2.Superhero;
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
        if (sender.hasPermission("superheroes.hero")) {
            if (args.length >= 1) {
                Superhero power = heroHandler.getSuperhero(args[0]);
                if (power == null) {
                    return false;
                }
                if (!sender.hasPermission("superheroes.hero." + power.getName().toLowerCase())) {
                    sender.spigot().sendMessage(MineDown.parse(configHandler.getNoPermissionMessage(), "player", sender.getName()));
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
            }
            else {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    Superhero superhero = heroHandler.getSuperhero(player);
                    player.spigot().sendMessage(MineDown.parse(configHandler.getCurrentHeroMessage(), "player", player.getName(), "hero", superhero.getName()));
                }
                else {
                    return false;
                }
            }
        }
        else {
            sender.spigot().sendMessage(MineDown.parse(configHandler.getNoPermissionMessage(), "player", sender.getName()));
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
        }
        return heroesTabComplete;
    }
}
