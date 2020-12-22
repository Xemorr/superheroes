package me.xemor.superheroes2.commands;

import me.xemor.superheroes2.ConfigHandler;
import me.xemor.superheroes2.PowersHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Reroll implements Listener, CommandExecutor {

    PowersHandler powersHandler;
    ConfigHandler configHandler;
    ItemStack rerollItem;
    boolean isEnabled;
    private final String noPermission = ChatColor.translateAlternateColorCodes('&', "&4You do not have permission to use this power!");

    public Reroll(PowersHandler powersHandler, ConfigHandler configHandler) {
        this.powersHandler = powersHandler;
        this.configHandler = configHandler;
        rerollItem = configHandler.getRerollItem();
        isEnabled = configHandler.isRerollEnabled();
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
        if (e.getAction() == Action.RIGHT_CLICK_AIR) {
            if (isEnabled) {
                if (rerollItem.isSimilar(item)) {
                    item.setAmount(item.getAmount() - 1);
                    powersHandler.setRandomHero(player);
                }
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("superheroes.reroll")) {
            Player player;
            if (args.length >= 1) {
                player = Bukkit.getPlayer(args[0]);
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
            powersHandler.setRandomHero(player);
        }
        else {
            sender.sendMessage(noPermission);
        }
        return true;
    }
}
