package me.xemor.superheroes.Superpowers;

import me.xemor.superheroes.Events.PlayerGainedPowerEvent;
import me.xemor.superheroes.Events.PlayerLostPowerEvent;
import me.xemor.superheroes.PowersHandler;
import me.xemor.superheroes.Superheroes;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Robot extends Superpower {

    private Superheroes superheroes;

    public Robot(PowersHandler powersHandler, Superheroes superheroes) {
        super(powersHandler);
        this.superheroes = superheroes;
    }

    @EventHandler
    public void onSaturationLoss(FoodLevelChangeEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            if (powersHandler.getPower(player) == Power.Robot) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            if (powersHandler.getPower(player) == Power.Robot) {
                if (e.getCause() == EntityDamageEvent.DamageCause.LIGHTNING) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 6000, 0));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 6000, 1));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 0, 0));
                    e.setCancelled(true);
                }
                else if (e.getCause() == EntityDamageEvent.DamageCause.FIRE && e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
                    e.setDamage(e.getDamage() * 0.5);
                }
            }
        }
    }

    @EventHandler
    public void onBecomeRobot(PlayerGainedPowerEvent e) {
        if (e.getPower() == Power.Robot) {
            startWaterCheck(e.getPlayer());
            e.getPlayer().setFoodLevel(20);
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0));
        }
    }

    @EventHandler
    public void losesRobot(PlayerLostPowerEvent e) {
        if (e.getPower() == Power.Robot) {
            e.getPlayer().removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
            e.getPlayer().removePotionEffect(PotionEffectType.GLOWING);
            e.getPlayer().removePotionEffect(PotionEffectType.NIGHT_VISION);
            e.getPlayer().removePotionEffect(PotionEffectType.SPEED);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (powersHandler.getPower(e.getPlayer()) == Power.Robot) {
            startWaterCheck(e.getPlayer());
        }
    }

    public void startWaterCheck(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (powersHandler.getPower(player) != Power.Robot) {
                    cancel();
                    return;
                } else {
                    if (player.getWorld().hasStorm()) {
                        if (player.getWorld().getBlockAt(player.getLocation()).getLightFromSky() == 15) {
                            player.damage(1.0);
                        }
                    }
                    if (player.getWorld().getBlockAt(player.getLocation()).getType() == Material.WATER) {
                        player.damage(1.0);
                    }
                }
            }
        }.runTaskTimer(superheroes, 10L, 10L);
    }



}
