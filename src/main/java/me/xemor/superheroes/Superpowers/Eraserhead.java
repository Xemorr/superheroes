package me.xemor.superheroes.Superpowers;

import me.xemor.superheroes.CooldownHandler;
import me.xemor.superheroes.Events.PlayerLostPowerEvent;
import me.xemor.superheroes.PowersHandler;
import me.xemor.superheroes.Superheroes;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class Eraserhead extends Superpower {

    CooldownHandler cooldownHandler = new CooldownHandler("&8&lEraserhead &7has %s seconds left until it can be used again!");

    public Eraserhead(PowersHandler powersHandler) {
        super(powersHandler);
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {
        if (e.isSneaking()) {
            Player player = e.getPlayer();
            if (powersHandler.getPower(player) == Power.Eraserhead) {
                if (cooldownHandler.isCooldownOver(player.getUniqueId())) {
                    Player otherPlayer = raytrace(player);
                    powersHandler.temporarilyRemovePower(otherPlayer, player);
                    cooldownHandler.startCooldown(15L, player.getUniqueId());
                    for (PotionEffect potionEffect : otherPlayer.getActivePotionEffects()) {
                        otherPlayer.removePotionEffect(potionEffect.getType());
                    }
                }
            }
        }
    }

    public Player raytrace(Player player) {
        World world = player.getWorld();
        Location currentLocation = player.getEyeLocation();
        Vector increment = player.getEyeLocation().getDirection();
        RayTraceResult rayTraceResult = world.rayTrace(currentLocation, increment, 32, FluidCollisionMode.NEVER, true, 0.5, entity -> {
            if (entity instanceof Player) {
                Player otherPlayer = (Player) entity;
                if (!otherPlayer.equals(player) && otherPlayer.getGameMode() != GameMode.SPECTATOR) {
                    return true;
                }
            }
            return false;
        });
        if (rayTraceResult == null) {
            return null;
        }
        return (Player) rayTraceResult.getHitEntity();
    }

    @EventHandler
    public void onLook(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (powersHandler.getPower(player) != Power.Eraserhead) {
            return;
        }
        if (!e.getFrom().getDirection().equals(e.getTo().getDirection())) {
            Entity otherEntity = raytrace(player);
            if (otherEntity == null) {
                return;
            }
            if (otherEntity instanceof Player) {
                Player otherPlayer = (Player) otherEntity;
                if (otherPlayer == null) {
                    BossBar bossBar = Bukkit.getBossBar(new NamespacedKey(JavaPlugin.getPlugin(Superheroes.class), player.getUniqueId().toString()));
                    if (bossBar == null) {
                        return;
                    }
                    bossBar.removePlayer(player);
                    return;
                }
                Power power = powersHandler.getPower(otherPlayer);
                String title = ChatColor.GRAY + "Erased";
                if (power != null) {
                    title = power.getNameColourCode();
                }
                NamespacedKey namespacedKey = new NamespacedKey(JavaPlugin.getPlugin(Superheroes.class), player.getUniqueId().toString());
                BossBar bossBar = Bukkit.getBossBar(namespacedKey);
                if (bossBar == null) {
                    bossBar = Bukkit.createBossBar(namespacedKey, title, BarColor.PURPLE, BarStyle.SOLID);
                }
                else {
                    bossBar.setTitle(title);
                }
                bossBar.setProgress(otherPlayer.getHealth() / otherPlayer.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
                bossBar.addPlayer(player);
            }
        }
    }

    @EventHandler
    public void onLost(PlayerLostPowerEvent e) {
        if (e.getPower() == Power.Eraserhead) {
            BossBar bossBar = Bukkit.getBossBar(new NamespacedKey(JavaPlugin.getPlugin(Superheroes.class), e.getPlayer().getUniqueId().toString()));
            if (bossBar == null) {
                return;
            }
            bossBar.removePlayer(e.getPlayer());
            return;
        }
    }

}
