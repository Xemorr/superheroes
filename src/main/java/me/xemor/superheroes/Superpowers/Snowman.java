package me.xemor.superheroes.Superpowers;

import me.xemor.superheroes.Events.PlayerGainedPowerEvent;
import me.xemor.superheroes.PowersHandler;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class Snowman extends Superpower{

    public Snowman(PowersHandler powersHandler) {
        super(powersHandler);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (player.isSneaking()) {
            return;
        }
        if (player.getGameMode() == GameMode.SPECTATOR) {
            return;
        }
        if (powersHandler.getPower(player) == Power.Snowman) {
            Location location = e.getTo();
            World world = player.getWorld();
            Block block = world.getBlockAt(location);
            Block belowBlock = world.getBlockAt(e.getTo()).getRelative(BlockFace.DOWN);
            if ((block.getType() == Material.AIR  || block.getType() == Material.CAVE_AIR )&& belowBlock.getType().isSolid()) {
                block.setType(Material.SNOW);
            }
        }
    }

    @EventHandler
    public void onThrow(PlayerInteractEvent e) {
        if (e.getItem() == null) {
            return;
        }
        if (e.getHand() == EquipmentSlot.OFF_HAND) {
            return;
        }
        if (!(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        if (e.getItem().getType() == Material.SNOWBALL) {
            Player player = e.getPlayer();
            if (powersHandler.getPower(player) == Power.Snowman) {
                e.setCancelled(true);
                Snowball snowball = player.launchProjectile(Snowball.class);
                snowball.teleport(snowball.getLocation().add(player.getEyeLocation().getDirection().normalize()));
            }
        }
    }

    @EventHandler
    public void onGain(PlayerGainedPowerEvent e) {
        if (e.getPower() == Power.Snowman) {
            e.getPlayer().getInventory().addItem(new ItemStack(Material.SNOWBALL));
        }
    }

}
