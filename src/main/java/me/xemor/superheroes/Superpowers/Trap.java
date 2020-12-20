package me.xemor.superheroes.Superpowers;

import me.xemor.superheroes.Events.PlayerLostPowerEvent;
import me.xemor.superheroes.PowersHandler;
import me.xemor.superheroes.Superheroes;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class Trap extends Superpower {


    ItemStack pinkLeatherTunic = new ItemStack(Material.LEATHER_CHESTPLATE);
    ItemStack pinkLeatherLegs = new ItemStack(Material.LEATHER_LEGGINGS);
    ItemStack pinkLeatherBoots = new ItemStack(Material.LEATHER_BOOTS);

    public Trap(PowersHandler powersHandler, Superheroes superheroes) {
        super(powersHandler);
        new BukkitRunnable() {
            @Override
            public void run() {
                LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) pinkLeatherTunic.getItemMeta();
                leatherArmorMeta.setColor(Color.fromRGB(255, 0, 255));
                pinkLeatherTunic.setItemMeta(leatherArmorMeta);
                pinkLeatherBoots.setItemMeta(leatherArmorMeta);
                pinkLeatherLegs.setItemMeta(leatherArmorMeta);
            }
        }.runTaskLater(superheroes, 200L);
    }

    HashMap<UUID, UUID> playerToTrap = new HashMap<>();

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {
        Player player = e.getPlayer();
        if (powersHandler.getPower(player) == Power.Trap) {
            if (e.isSneaking()) {
                World world = player.getWorld();
                ArmorStand armorStand = world.spawn(player.getLocation(), ArmorStand.class);
                armorStand.setBasePlate(false);
                EntityEquipment equipment = armorStand.getEquipment();
                ItemStack headItem = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta skullMeta = (SkullMeta) headItem.getItemMeta();
                skullMeta.setOwningPlayer(player);
                headItem.setItemMeta(skullMeta);
                equipment.setHelmet(headItem);
                equipment.setChestplate(pinkLeatherTunic);
                equipment.setLeggings(pinkLeatherLegs);
                equipment.setBoots(pinkLeatherBoots);
                playerToTrap.put(player.getUniqueId(), armorStand.getUniqueId());
                armorStand.setInvulnerable(true);
                armorStand.setArms(true);
                armorStand.setCustomName("Trap");
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 300000, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 300000, 1));
            }
            else {
                UUID armorstandUUID = playerToTrap.get(player.getUniqueId());
                if (armorstandUUID != null) {
                    Bukkit.getEntity(armorstandUUID).remove();
                    player.removePotionEffect(PotionEffectType.INVISIBILITY);
                    player.removePotionEffect(PotionEffectType.SPEED);
                    playerToTrap.remove(player.getUniqueId());
                }
            }
        }
    }

    @EventHandler
    public void disableSlots(PlayerArmorStandManipulateEvent e) {
        if ("Trap".equals(e.getRightClicked().getCustomName())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onLost(PlayerLostPowerEvent e) {
        if (e.getPower() == Power.Trap) {
            Player player = e.getPlayer();
            UUID armorstandUUID = playerToTrap.get(player.getUniqueId());
            if (armorstandUUID != null) {
                Bukkit.getEntity(armorstandUUID).remove();
                player.removePotionEffect(PotionEffectType.INVISIBILITY);
                playerToTrap.remove(player.getUniqueId());
            }
        }
    }

    @EventHandler
    public void onTarget(EntityTargetLivingEntityEvent e) {
        if (e.getTarget() instanceof Player) {
            Player player = (Player) e.getTarget();
            if (powersHandler.getPower(player) == Power.Trap) {
                if (player.getPotionEffect(PotionEffectType.INVISIBILITY) != null) {
                    e.setCancelled(true);
                    e.setTarget(Bukkit.getEntity(playerToTrap.get(player.getUniqueId())));
                }
            }
        }
    }

}
