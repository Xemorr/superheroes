package me.xemor.superheroes.Superpowers;

import me.xemor.superheroes.Events.PlayerGainedPowerEvent;
import me.xemor.superheroes.Events.PlayerLostPowerEvent;
import me.xemor.superheroes.PowersHandler;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;

public class ExtraHeartMan extends Superpower {

    public ExtraHeartMan(PowersHandler powersHandler) {
        super(powersHandler);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent e) {
        Player player = e.getPlayer();
        if (powersHandler.getPower(player)  == Power.ExtraHeartMan) {
            Entity entity = e.getRightClicked();
            if (entity instanceof Player) {
                Player otherPlayer = (Player) entity;
                if (!otherPlayer.hasPotionEffect(PotionEffectType.ABSORPTION)) {
                    World world = otherPlayer.getWorld();
                    world.spawnParticle(Particle.VILLAGER_HAPPY, otherPlayer.getLocation().add(0, 1, 0), 1);
                    otherPlayer.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 600, 0));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 600, 0));
                }
            }
        }
    }

    @EventHandler
    public void powerGained(PlayerGainedPowerEvent e) {
        if (e.getPower() == Power.ExtraHeartMan) {
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, Integer.MAX_VALUE, 0));
        }
    }

    @EventHandler
    public void powerLost(PlayerLostPowerEvent e) {
        if (e.getPower() == Power.ExtraHeartMan) {
            e.getPlayer().removePotionEffect(PotionEffectType.HEALTH_BOOST);
        }
    }
}
