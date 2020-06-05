package me.xemor.superheroes.Commands;

import me.xemor.superheroes.PowersHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Reroll implements CommandExecutor {


    PowersHandler powersHandler;
    public Reroll(PowersHandler powersHandler) {
        this.powersHandler = powersHandler;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("superheroes.reroll")) {
            if (args.length >= 1) {
                Player player = Bukkit.getPlayer(args[0]);
                if (player == null) {
                    return false;
                }
                powersHandler.setRandomPower(player);
            }
            else {
                if (sender instanceof Player) {
                    powersHandler.setRandomPower((Player) sender);
                }
            }
        }
        return true;
    }
}
