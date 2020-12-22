package me.xemor.superheroes2.commands;

import me.xemor.superheroes2.PowersHandler;
import me.xemor.superheroes2.Superhero;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HeroCMD implements CommandExecutor, TabExecutor {

    private PowersHandler powersHandler;
    private final String noPermission = ChatColor.translateAlternateColorCodes('&', "&4You do not have permission to use this power!");
    public HeroCMD(PowersHandler powersHandler) {
        this.powersHandler = powersHandler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("superheroes.hero")) {
            if (args.length >= 1) {
                Superhero power = powersHandler.getSuperhero(args[0]);
                if (power == null) {
                    return false;
                }
                if (!sender.hasPermission("superheroes.hero" + power.toString().toLowerCase())) {
                    sender.sendMessage(noPermission);
                }
                Player player;
                if (args.length >= 2) {
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
                powersHandler.setHero(player, power);
            }
        }
        else {
            sender.sendMessage(noPermission);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> heroesTabComplete = new ArrayList<>();
        List<String> heroNames = powersHandler.getNameToSuperhero().values().stream().map(hero -> hero.getName()).collect(Collectors.toList());
        heroNames.add("None");
        if (args.length == 1) {
            String firstArg = args[0];
            for (Superhero superhero : powersHandler.getNameToSuperhero().values()) {
                if (superhero.getName().startsWith(firstArg)) {
                    heroesTabComplete.add(superhero.getName());
                }
            }
        }
        return heroesTabComplete;
    }
}
