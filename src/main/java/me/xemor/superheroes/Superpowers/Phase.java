package me.xemor.superheroes.Superpowers;

import me.xemor.superheroes.PowersHandler;
import me.xemor.superheroes.Superheroes;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Phase extends Superpower {

    public Phase(PowersHandler powersHandler) {
        super(powersHandler);
    }

    @EventHandler
    public void onToggleSneak(PlayerToggleSneakEvent e) {
        if (e.isSneaking()) {
            if (!e.getPlayer().isOnGround()) {
                return;
            }
            Player player = e.getPlayer();
            if (powersHandler.getPower(player) == Power.Phase) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (powersHandler.getPower(player) != Power.Phase) {
                            cancel();
                            player.setGameMode(GameMode.SURVIVAL);
                            return;
                        }
                        if (player.isSneaking() && player.getLocation().getY() > 3.5) {
                            player.setGameMode(GameMode.SPECTATOR);
                            player.setGravity(true);
                            player.setAllowFlight(false);
                            player.setFlying(false);
                            player.setVelocity(player.getVelocity().normalize().multiply(0.75));
                            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 1));
                            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 0));
                        }
                        else {
                            player.setGameMode(GameMode.SURVIVAL);
                            player.removePotionEffect(PotionEffectType.BLINDNESS);
                            if (!player.getWorld().getBlockAt(player.getEyeLocation()).isPassable()) {
                                player.setVelocity(new Vector(0, 1.33, 0));
                                player.teleport(player.getEyeLocation().add(0, 0.5, 0));
                                return;
                            }
                            cancel();
                        }
                    }
                }.runTaskTimer(JavaPlugin.getPlugin(Superheroes.class), 0L, 5L);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION || e.getCause() == EntityDamageEvent.DamageCause.FALL) {
            if (e.getEntity() instanceof Player) {
                Player player = (Player) e.getEntity();
                if (powersHandler.getPower(player) == Power.Phase) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void teleport(PlayerTeleportEvent e) {
        if (e.getPlayer().getGameMode() == GameMode.SPECTATOR) {
            e.setCancelled(true);
        }
    }

}
