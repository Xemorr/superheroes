package me.xemor.superheroes.Superpowers;

import me.xemor.superheroes.CooldownHandler;
import me.xemor.superheroes.Events.PlayerLostPowerEvent;
import me.xemor.superheroes.PowersHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class Zeus extends Superpower {

    CooldownHandler cooldownHandler = new CooldownHandler("&e&lZeus &fCooldown: %s seconds");

    public Zeus(PowersHandler powersHandler) {
        super(powersHandler);
    }

    @EventHandler
    public void onPunch(PlayerInteractEvent e) {
        if (e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_AIR) {
            Player player = e.getPlayer();
            if (powersHandler.getPower(player) == Power.Zeus) {
                if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
                    if (cooldownHandler.isCooldownOver(player.getUniqueId())) {
                        strikeLightning(player, 30);
                        cooldownHandler.startCooldown(10, player.getUniqueId());
                        player.setVelocity(player.getEyeLocation().getDirection().multiply(-0.5));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 300, 0));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onLosePower(PlayerLostPowerEvent e) {
        if (e.getPower() == Power.Zeus) {
            e.getPlayer().removePotionEffect(PotionEffectType.SLOW_FALLING);
            e.getPlayer().removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
        }
    }

    public void strikeLightning(Player player, int blocksToTravel) {
        World world = player.getWorld();
        Location eyeLoc = player.getEyeLocation().clone();
        Vector travelVector = eyeLoc.getDirection();
        RayTraceResult rayTraceResult = world.rayTraceBlocks(eyeLoc, travelVector, blocksToTravel);
        Vector hitPosition;
        if (rayTraceResult == null || rayTraceResult.getHitPosition() == null) {
            hitPosition = eyeLoc.toVector().add(travelVector.multiply(blocksToTravel));
        } else {
            hitPosition = rayTraceResult.getHitPosition();
        }
        Location location = new Location(world, hitPosition.getX(), hitPosition.getY(), hitPosition.getZ());
        world.strikeLightning(location);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.LIGHTNING) {
            if (e.getEntity() instanceof Player) {
                Player player = (Player) e.getEntity();
                if (powersHandler.getPower(player) == Power.Zeus) {
                    e.setCancelled(true);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 100, 0));
                }
            }
        }
    }
}
