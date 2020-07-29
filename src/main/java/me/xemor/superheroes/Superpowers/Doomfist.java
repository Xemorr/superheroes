package me.xemor.superheroes.Superpowers;

import me.xemor.superheroes.CooldownHandler;
import me.xemor.superheroes.PowersHandler;
import me.xemor.superheroes.Superheroes;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Collection;

public class Doomfist extends Superpower {

    CooldownHandler cooldownHandler = new CooldownHandler("&8&lSlam &fCooldown: %s seconds");
    Superheroes superheroes;


    public Doomfist(PowersHandler powersHandler, Superheroes superheroes) {
        super(powersHandler);
        this.superheroes = superheroes;
    }

    @EventHandler
    public void onPunch(PlayerAnimationEvent e) {
        if (e.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR) {
            if (powersHandler.getPower(e.getPlayer()) == Power.Doomfist) {
                if (cooldownHandler.isCooldownOver(e.getPlayer().getUniqueId())) {
                    if (e.getPlayer().getFoodLevel() > 6) {
                        cooldownHandler.startCooldown(1, e.getPlayer().getUniqueId());
                        e.getPlayer().setFoodLevel(e.getPlayer().getFoodLevel() - 3);
                        doDoomfistJump(e.getPlayer());
                    }
                }
            }
        }
    }

    public void doDoomfistJump(Player player) {
        player.setVelocity(player.getEyeLocation().getDirection().multiply(1.8).add(new Vector(0, 0.5, 0)));
        new BukkitRunnable() {
            @Override
            public void run() {
                Block under = player.getWorld().getBlockAt(player.getLocation().subtract(0, 1, 0));
                if (under.getType().isAir()) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Block under = player.getWorld().getBlockAt(player.getLocation().subtract(0, 1, 0));
                            if (!under.getType().isAir()) {
                                World world = player.getWorld();
                                Collection<Entity> entities = world.getNearbyEntities(player.getLocation(), 10, 3, 10, entity -> entity instanceof LivingEntity);
                                for (Entity entity : entities) {
                                    if (!player.equals(entity)) {
                                        LivingEntity livingEntity = (LivingEntity) entity;
                                        livingEntity.damage(8, player);
                                    }
                                }
                                cooldownHandler.startCooldown(10, player.getUniqueId());
                                cancel();
                            }
                        }
                    }.runTaskTimer(superheroes, 2L, 2L);
                }
            }
        }.runTaskLater(superheroes, 2L);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                if (powersHandler.getPower(player) == Power.Doomfist) {
                    e.setCancelled(true);
                }
            }
        }
    }

}
