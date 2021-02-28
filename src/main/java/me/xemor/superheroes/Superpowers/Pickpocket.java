package me.xemor.superheroes.Superpowers;

import me.xemor.superheroes.PowersHandler;
import me.xemor.superheroes.Superheroes;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class Pickpocket extends Superpower {
    public Pickpocket(PowersHandler powersHandler) {
        super(powersHandler);
    }

    @EventHandler
    public void onRightClick(PlayerInteractEntityEvent e) {
        Player player = e.getPlayer();
        if (powersHandler.getPower(player) == Power.Pickpocket) {
            if (e.getRightClicked() instanceof Player) {
                if (!player.isSneaking()) {
                    return;
                }
                Player otherPlayer = (Player) e.getRightClicked();
                player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1F, 0.5F);
                otherPlayer.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1F, 0.5F);
                Inventory inventory =  otherPlayer.getInventory();
                InventoryView inventoryView = player.openInventory(inventory);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (inventoryView == null) {
                            cancel();
                            return;
                        }
                        if (powersHandler.getPower(player) != Power.Pickpocket) {
                            cancel();
                            return;
                        }
                        if (otherPlayer.getLocation().distanceSquared(player.getLocation()) > 9) {
                            inventoryView.close();
                            cancel();
                            return;
                        }
                    }
                }.runTaskTimer(JavaPlugin.getPlugin(Superheroes.class), 0L, 4L);
            }
            List<Entity> passengers = e.getRightClicked().getPassengers();
            if (passengers.size() > 0 && e.getRightClicked() instanceof Vehicle) {
                for (Entity entity : passengers) {
                    e.getRightClicked().removePassenger(entity);
                }
            }
        }
    }
}
