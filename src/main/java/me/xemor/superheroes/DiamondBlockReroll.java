package me.xemor.superheroes;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class DiamondBlockReroll implements Listener {

    private PowersHandler powersHandler;

    public DiamondBlockReroll(PowersHandler powersHandler) {
        this.powersHandler = powersHandler;
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        if (e.getItem() == null) {
            return;
        }
        if (e.getItem().getType() == Material.DIAMOND_BLOCK || e.getItem().getType() == Material.NETHER_STAR) {
            if (e.getAction() == Action.RIGHT_CLICK_AIR) {
                powersHandler.setRandomPower(e.getPlayer());
                e.getItem().setAmount(e.getItem().getAmount() - 1);
            }
        }
    }

}
