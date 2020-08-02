package me.xemor.superheroes.Superpowers;

import me.xemor.superheroes.PowersHandler;
import me.xemor.superheroes.Superheroes;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Aerosurfer extends Superpower {

    Superheroes superheroes;
    public Aerosurfer(PowersHandler powersHandler, Superheroes superheroes) {
        super(powersHandler);
        this.superheroes = superheroes;
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {
        if (getPowersHandler().getPower(e.getPlayer()) != Power.Aerosurfer) {
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
                    if (powersHandler.getPower(player) != Power.Aerosurfer) {
                        cancel();
                        return;
                    }
                    if (!player.isSneaking()) {
                        cancel();
                        return;
                    }
                    Location blockToPlaceLocation = e.getPlayer().getLocation().add(0, -1, 0);
                    World world = e.getPlayer().getWorld();
                    e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 4, 1));
                    Block block = world.getBlockAt(blockToPlaceLocation);
                    if (block.getType() == Material.AIR || block.getType() == Material.CAVE_AIR) {
                        block.setType(Material.GLASS);
                        block.setMetadata("aerosurferGlass", new FixedMetadataValue(superheroes, true));
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (block.getType() == Material.GLASS) {
                                    world.playSound(block.getLocation(), Sound.BLOCK_GLASS_BREAK, 0.5F, 1.0F);
                                    block.setType(Material.AIR);

                                }
                            }
                        }.runTaskLater(JavaPlugin.getPlugin(Superheroes.class), 200L);
                    }
                }
            }.runTaskTimer(JavaPlugin.getPlugin(Superheroes.class), 0L, 2L);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.getBlock().hasMetadata("aerosurferGlass")) {
            e.setDropItems(false);
        }
    }

    @EventHandler
    public void onDeath(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                if (powersHandler.getPower(player) == Power.Aerosurfer || powersHandler.getPower(player) == Power.Enderman) {
                    if (e.getDamage() > player.getHealth()) {
                        if (player.getHealth() >= 1) {
                            e.setDamage(player.getHealth() - 1);
                        }
                        else {
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }
    }
}
