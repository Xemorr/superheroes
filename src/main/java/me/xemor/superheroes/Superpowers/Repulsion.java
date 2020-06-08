package me.xemor.superheroes.Superpowers;

import me.xemor.superheroes.PowersHandler;
import me.xemor.superheroes.Superheroes;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Collection;

public class Repulsion extends Superpower {
    public Repulsion(PowersHandler powersHandler) {
        super(powersHandler);
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {
        if (e.isSneaking()) {
            Player player = e.getPlayer();
            if (powersHandler.getPower(player) == Power.Repulsion) {
                World world = player.getWorld();
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!player.isSneaking()) {
                            cancel();
                            return;
                        }
                        if (powersHandler.getPower(player) != Power.Repulsion) {
                            cancel();
                            return;
                        }
                        Collection<Entity> nearbyLivingEntities = world.getNearbyEntities(player.getLocation(), 10,10,10, (entity) -> !player.equals(entity) && entity instanceof LivingEntity);
                        for (Entity entity : nearbyLivingEntities) {
                            Vector vector = entity.getLocation().subtract(player.getLocation()).toVector();
                            vector.setY(0);
                            entity.teleport(entity.getLocation().add(0, 0.2, 0));
                            entity.setVelocity(vector.normalize().multiply(1.5));
                        }
                    }
                }.runTaskTimer(JavaPlugin.getPlugin(Superheroes.class), 0L, 5L);

            }
        }
    }
}
