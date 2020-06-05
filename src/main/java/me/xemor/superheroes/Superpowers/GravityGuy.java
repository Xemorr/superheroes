package me.xemor.superheroes.Superpowers;

import me.xemor.superheroes.Events.PlayerGainedPowerEvent;
import me.xemor.superheroes.Events.PlayerLostPowerEvent;
import me.xemor.superheroes.PowersHandler;
import me.xemor.superheroes.Superheroes;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class GravityGuy extends Superpower {

    public GravityGuy(PowersHandler powersHandler) {
        super(powersHandler);
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {
        if (getPowersHandler().getPower(e.getPlayer()) != Power.GravityGuy) {
            return;
        }
        if (e.isSneaking()) {
            Player player = e.getPlayer();
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (player == null) {
                        cancel();
                        return;
                    }
                    if (!player.isOnline()) {
                        cancel();
                        return;
                    }
                    if (powersHandler.getPower(player) != Power.GravityGuy) {
                        cancel();
                        return;
                    }
                    if (!player.isSneaking()) {
                        cancel();
                        e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 30, 0));
                        e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 0));
                        return;
                    }
                    e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 4, 3));
                }
            }.runTaskTimer(JavaPlugin.getPlugin(Superheroes.class), 0L, 2L);
        }
    }

    @EventHandler
    public void onGain(PlayerGainedPowerEvent e) {
        if (e.getPower() == Power.GravityGuy) {
            e.getPlayer().setAllowFlight(true);
        }
    }

    @EventHandler
    public void onLost(PlayerLostPowerEvent e) {
        if (e.getPower() == Power.GravityGuy) {
            e.getPlayer().setGravity(true);
            e.getPlayer().setAllowFlight(false);
        }
    }

    @EventHandler
    public void flightToggle(PlayerToggleFlightEvent e) {
        if (powersHandler.getPower(e.getPlayer()) == Power.GravityGuy) {
            if (e.isFlying()) {
                e.setCancelled(true);
            }
        }
    }

}
