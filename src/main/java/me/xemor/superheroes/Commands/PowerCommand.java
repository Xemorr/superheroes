package me.xemor.superheroes.Commands;

import me.xemor.superheroes.PowersHandler;
import me.xemor.superheroes.Superpowers.Power;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PowerCommand implements CommandExecutor, TabExecutor {

    PowersHandler powersHandler;
    public PowerCommand(PowersHandler powersHandler) {
        this.powersHandler = powersHandler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("superheroes.power")) {
            if (args.length >= 1) {
                Power power = Power.valueOf(args[0]);
                if (power == null) {
                    return false;
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
                powersHandler.setPower(player, power);
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> tabComplete = new ArrayList<>();
        if (args.length == 1) {
            for (Power power : Power.values()) {
                if (power.toString().startsWith(args[0])) {
                    tabComplete.add(power.toString());
                }
            }
        }
        return tabComplete;
    }
}
