package me.xemor.superheroes.Superpowers;

import me.xemor.superheroes.CooldownHandler;
import me.xemor.superheroes.Events.PlayerGainedPowerEvent;
import me.xemor.superheroes.Events.PlayerLostPowerEvent;
import me.xemor.superheroes.PowersHandler;
import me.xemor.superheroes.Superheroes;
import org.bukkit.*;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class Gun extends Superpower {

    ItemStack gun = new ItemStack(Material.STONE_HOE);
    String gunDisplayName = ChatColor.translateAlternateColorCodes('&', "&8&lGun");
    CooldownHandler cooldownHandler = new CooldownHandler("&8&lGun &7has %s seconds left until it can be used again!");

    public Gun(PowersHandler powersHandler) {
        super(powersHandler);
        ItemMeta itemMeta = gun.getItemMeta();
        itemMeta.setUnbreakable(true);
        itemMeta.setDisplayName(gunDisplayName);
        gun.setItemMeta(itemMeta);
    }

    @EventHandler
    public void powerGained(PlayerGainedPowerEvent e) {
        if (e.getPower() == Power.Gun) {
            e.getPlayer().getInventory().addItem(gun);
        }
    }

    @EventHandler
    public void powerLost(PlayerLostPowerEvent e) {
        if (e.getPower() == Power.Gun) {
            e.getPlayer().getInventory().remove(gun);
        }
    }

    @EventHandler
    public void gunDrop(PlayerDropItemEvent e) {
        if (gun.equals(e.getItemDrop().getItemStack())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void gunStore(InventoryMoveItemEvent e) {
        if (gun.equals(e.getItem())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDeath(PlayerRespawnEvent e) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Player player = e.getPlayer();
                if (powersHandler.getPower(player) == Power.Gun) {
                    player.getInventory().addItem(gun);
                }
            }
        }.runTaskLater(JavaPlugin.getPlugin(Superheroes.class), 1L);
    }

    @EventHandler
    public void useGun(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (gun.isSimilar(e.getItem())) {
                Player player = e.getPlayer();
                if (powersHandler.getPower(player) == Power.Gun) {
                    World world = player.getWorld();
                    if (cooldownHandler.isCooldownOver(player.getUniqueId())) {
                        Location currentLocation = player.getEyeLocation();
                        Vector increment = player.getEyeLocation().getDirection();
                        world.playSound(player.getEyeLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.5F, 1.0F);
                        RayTraceResult rayTraceResult = world.rayTrace(currentLocation, increment, 64, FluidCollisionMode.NEVER, true, 1.0, (entity) -> (entity instanceof LivingEntity || entity instanceof EnderCrystal)&& !player.equals(entity));
                        cooldownHandler.startCooldown(2L, player.getUniqueId());
                        for (int i = 0; i < 64; i++) {
                            world.spawnParticle(Particle.FLAME, currentLocation, 1);
                            currentLocation = currentLocation.add(increment);
                        }
                        if (rayTraceResult == null) {
                            return;
                        }
                        if (rayTraceResult.getHitEntity() instanceof EnderCrystal) {
                            EnderCrystal enderCrystal = (EnderCrystal) rayTraceResult.getHitEntity();
                            enderCrystal.remove();
                            world.createExplosion(enderCrystal.getLocation(), 6);
                            return;
                        }
                        LivingEntity livingEntity = (LivingEntity) rayTraceResult.getHitEntity();
                        if (livingEntity == null) {
                            return;
                        }
                        if (livingEntity instanceof EnderDragon) {
                            livingEntity.setHealth(livingEntity.getHealth() - 5);
                        }
                        livingEntity.damage(5.0, player); //doesn't work on edragon for some reason
                        world.spawnParticle(Particle.EXPLOSION_NORMAL, livingEntity.getLocation().add(0, 1, 0), 1);
                    }
                }
            }
        }
    }
}
