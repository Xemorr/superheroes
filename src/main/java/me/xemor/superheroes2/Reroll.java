package me.xemor.superheroes2;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Reroll implements Listener {

    PowersHandler powersHandler;
    ConfigHandler configHandler;
    ItemStack rerollItem;
    boolean isEnabled;

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

}
