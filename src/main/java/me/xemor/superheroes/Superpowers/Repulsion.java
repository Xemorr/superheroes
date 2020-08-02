package me.xemor.superheroes.Superpowers;

import me.xemor.superheroes.CooldownHandler;
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

    CooldownHandler cooldownHandler = new CooldownHandler(Power.Repulsion.getNameColourCode() + " &fcan be used again in %s seconds");

    public Repulsion(PowersHandler powersHandler) {
        super(powersHandler);
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {
        if (e.isSneaking()) {
            Player player = e.getPlayer();
            if (powersHandler.getPower(player) == Power.Repulsion) {
                if (cooldownHandler.isCooldownOver(player.getUniqueId())) {
                    World world = player.getWorld();
                    long currentTimeMillis = System.currentTimeMillis();
                    cooldownHandler.startCooldown(10L, player.getUniqueId());
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
                            if (currentTimeMillis + 1000 < System.currentTimeMillis()) {
                                cancel();
                                return;
                            }
                            Collection<Entity> nearbyLivingEntities = world.getNearbyEntities(player.getLocation(), 10, 10, 10, (entity) -> !player.equals(entity) && entity instanceof LivingEntity);
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
}
