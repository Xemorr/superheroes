package me.xemor.superheroes.Superpowers;

import me.xemor.superheroes.PowersHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class Pyromaniac extends Superpower {
    public Pyromaniac(PowersHandler powersHandler) {
        super(powersHandler);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (powersHandler.getPower(player) == Power.Pyromaniac) {
            if (player.isSneaking()) {
                return;
            }
            Location location = player.getLocation();
            World world = player.getWorld();
            Block block = world.getBlockAt(location);
            if (block.getType() == Material.AIR || block.getType() == Material.CAVE_AIR) {
                if (!block.getRelative(BlockFace.DOWN).isPassable()) {
                    block.setType(Material.FIRE);
                }
            }
            Location eyeLocation = player.getEyeLocation();
            RayTraceResult rayTraceResult = world.rayTraceBlocks(eyeLocation, eyeLocation.getDirection(), 20);
            if (rayTraceResult == null) {
                return;
            }
            Block tracedBlock = rayTraceResult.getHitBlock();
            if (tracedBlock.getType().isFlammable()) {
                Vector backwards = eyeLocation.getDirection().multiply(-1);
                Block fireBlock = tracedBlock.getRelative(backwards.getBlockX(), backwards.getBlockY(), backwards.getBlockZ());
                if (fireBlock.getType() == Material.AIR) {
                    fireBlock.setType(Material.FIRE);
                }
                return;
            }
        }
    }



}
