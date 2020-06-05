package me.xemor.superheroes.Superpowers;

import me.xemor.superheroes.CooldownHandler;
import me.xemor.superheroes.ParticleHandler;
import me.xemor.superheroes.PowersHandler;
import me.xemor.superheroes.Superheroes;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class Enderman extends Superpower {


    public Enderman(PowersHandler powersHandler) {
        super(powersHandler);
    }


    private String extraEnderPearl = ChatColor.translateAlternateColorCodes('&', "&fYou have gained an additional enderpearl!");
    private CooldownHandler cooldownHandler = new CooldownHandler("&5&lTeleport &7has %s seconds until it can be used again!");

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        if (e.getItem() == null) {
            Player player = e.getPlayer();
            if (e.getAction() == Action.LEFT_CLICK_AIR) {
                if (powersHandler.getPower(player) == Power.Enderman) {
                    if (cooldownHandler.isCooldownOver(player.getUniqueId())) {
                        doEnderTeleport(player, 30, 0.25);
                        cooldownHandler.startCooldown(10L, player.getUniqueId());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onKill(EntityDeathEvent e) {
        if (e.getEntity().getType() == EntityType.ENDERMAN) {
            if (e.getEntity().getKiller() instanceof Player) {
                Player player = e.getEntity().getKiller();
                if (powersHandler.getPower(player) == Power.Enderman) {
                    World world = player.getWorld();
                    world.dropItemNaturally(player.getLocation(), new ItemStack(Material.ENDER_PEARL, 1));
                    player.sendMessage(extraEnderPearl);
                }
            }
        }
    }

    public void doEnderTeleport(Player player, int blocksToTravel, double yAxisMultiplier) {
        World world = player.getWorld();
        Location eyeLoc = player.getEyeLocation().clone();
        Vector travelVector = eyeLoc.getDirection().setY(eyeLoc.getDirection().getY() * 0.75);
        RayTraceResult rayTraceResult = world.rayTraceBlocks(eyeLoc, travelVector, blocksToTravel);
        Vector hitPosition;
        if (rayTraceResult == null || rayTraceResult.getHitPosition() == null) {
            hitPosition = eyeLoc.toVector().add(travelVector.multiply(blocksToTravel));
        } else {
            hitPosition = rayTraceResult.getHitPosition();
        }
        Location location = new Location(world, hitPosition.getX(), hitPosition.getY(), hitPosition.getZ());
        Location eyeLocation = player.getEyeLocation();
        location.setYaw(eyeLocation.getYaw());
        location.setPitch(eyeLocation.getPitch());
        player.teleport(location, PlayerTeleportEvent.TeleportCause.ENDER_PEARL);
        ParticleHandler particleHandler = new ParticleHandler(player);
        particleHandler.setHelix(true);
        particleHandler.setParticle(Particle.PORTAL);
        particleHandler.setRadius(1);
        particleHandler.setDuration(50);
        particleHandler.runTaskTimer(JavaPlugin.getPlugin(Superheroes.class), 0L, 5L);
    }

}
