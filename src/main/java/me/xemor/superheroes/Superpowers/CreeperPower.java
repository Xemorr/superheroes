package me.xemor.superheroes.Superpowers;

import me.xemor.superheroes.CooldownHandler;
import me.xemor.superheroes.PowersHandler;
import me.xemor.superheroes.Superheroes;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class CreeperPower extends Superpower {

    CooldownHandler cooldownHandler = new CooldownHandler("&2&lCreeper &7is on cooldown for %s more seconds!");
    private String creeperDetonate = ChatColor.translateAlternateColorCodes('&', Power.Creeper.getNameColourCode() + ": &7you detonated the creeper!");

    public CreeperPower(PowersHandler powersHandler) {
        super(powersHandler);
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {
        if (e.isSneaking()) {
            Player player = e.getPlayer();
            if (powersHandler.getPower(player) == Power.Creeper) {
                if (player.isOnGround()) {
                    final int[] timer = {0};
                    World world = player.getWorld();
                    if (cooldownHandler.isCooldownOver(player.getUniqueId())) {
                        world.playSound(player.getLocation(), Sound.ENTITY_CREEPER_PRIMED, 1.0F, 1.0F);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (!player.isSneaking()) {
                                    cancel();return;
                                }
                                if (powersHandler.getPower(player) != Power.Creeper) {
                                    cancel();return;
                                }
                                if (!player.isOnGround()) {
                                    cancel();return;
                                }
                                if (timer[0] >= 40) {
                                    world.createExplosion(player.getLocation(), 3, false);
                                    cooldownHandler.startCooldown(10L, player.getUniqueId());
                                    player.setVelocity(new Vector(0, 2.5, 0));
                                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 140, 0));
                                    cancel();return;
                                } else {
                                    timer[0]++;
                                }
                            }
                        }.runTaskTimer(JavaPlugin.getPlugin(Superheroes.class), 0L, 1L);
                    }
                    Creeper otherCreeper = raytrace(player);
                    if (otherCreeper != null) {
                        player.sendMessage(creeperDetonate);
                        otherCreeper.explode();
                    }
                }
            }
        }
    }

    public Creeper raytrace(Player player) {
        World world = player.getWorld();
        RayTraceResult rayTraceResult = world.rayTraceEntities(player.getEyeLocation(), player.getEyeLocation().getDirection(), 32, 0.5, (entity) -> entity instanceof Creeper);
        if (rayTraceResult == null) {
            return null;
        }
        return (Creeper) rayTraceResult.getHitEntity();
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION || e.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
            if (e.getEntity() instanceof Player) {
                Player player = (Player) e.getEntity();
                if (powersHandler.getPower(player) == Power.Creeper) {
                    e.setDamage(0);
                }
            }
        }
    }
}
