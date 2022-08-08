package me.xemor.superheroes2.skills.implementations;

import me.xemor.superheroes2.Superhero;
import me.xemor.superheroes2.Superheroes2;
import me.xemor.superheroes2.data.HeroHandler;
import me.xemor.superheroes2.events.PlayerLostSuperheroEvent;
import me.xemor.superheroes2.skills.Skill;
import me.xemor.superheroes2.skills.skilldata.DecoyData;
import me.xemor.superheroes2.skills.skilldata.SkillData;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class DecoySkill extends SkillImplementation {

    HashMap<SkillData, HashMap<UUID, UUID>> playerToDecoy = new HashMap<>();
    NamespacedKey namespacedKey;

    public DecoySkill(HeroHandler heroHandler) {
        super(heroHandler);
        namespacedKey = new NamespacedKey(heroHandler.getPlugin(), "decoy");
    }

    @EventHandler
    public void disableSlots(PlayerArmorStandManipulateEvent e) {
        ArmorStand armorStand = e.getRightClicked();
        if (armorStand.getPersistentDataContainer().has(namespacedKey, PersistentDataType.INTEGER)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {
        Player player = e.getPlayer();
        Superhero superhero = heroHandler.getSuperhero(player);
        Collection<SkillData> skillDatas = superhero.getSkillData(Skill.getSkill("DECOY"));
        for (SkillData skillData : skillDatas) {
            DecoyData decoyData = (DecoyData) skillData;
            if (e.isSneaking() && decoyData.areConditionsTrue(player)) {
                removeArmorStand(player, decoyData);
                ArmorStand armorStand = createArmorStand(player, decoyData);
                HashMap<UUID, UUID> hashMap = playerToDecoy.getOrDefault(decoyData, new HashMap<>());
                hashMap.put(player.getUniqueId(), armorStand.getUniqueId());
                playerToDecoy.put(skillData, hashMap);
            }
            else {
                removeArmorStand(player, decoyData);
            }
        }
    }

    public void removeArmorStand(Player player, DecoyData decoyData) {
        HashMap<UUID, UUID> hashMap = playerToDecoy.getOrDefault(decoyData, new HashMap<>());
        UUID armorStandUUID = hashMap.get(player.getUniqueId());
        if (armorStandUUID != null) {
            Entity entity = Bukkit.getEntity(armorStandUUID);
            if (entity != null) {
                entity.remove();
            }
            hashMap.remove(player.getUniqueId());
            playerToDecoy.put(decoyData, hashMap);
        }
    }

    public ArmorStand createArmorStand(Player player, DecoyData decoyData) {
        World world = player.getWorld();
        ArmorStand armorStand = world.spawn(player.getLocation(), ArmorStand.class);
        armorStand.setBasePlate(false);
        EntityEquipment entityEquipment = armorStand.getEquipment();
        entityEquipment.setHelmet(decoyData.getSkull(player));
        entityEquipment.setChestplate(createLeatherGear(Material.LEATHER_CHESTPLATE, decoyData));
        entityEquipment.setLeggings(createLeatherGear(Material.LEATHER_LEGGINGS, decoyData));
        entityEquipment.setBoots(createLeatherGear(Material.LEATHER_BOOTS, decoyData));
        armorStand.setInvulnerable(true);
        armorStand.setArms(true);
        armorStand.setCustomName("Decoy");
        armorStand.getPersistentDataContainer().set(namespacedKey, PersistentDataType.INTEGER, 0);
        return armorStand;
    }

    public ItemStack createLeatherGear(@NotNull Material leatherArmor, DecoyData decoyData) {
        ItemStack itemStack = new ItemStack(leatherArmor);
        LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemStack.getItemMeta();
        leatherArmorMeta.setColor(decoyData.getColor());
        itemStack.setItemMeta(leatherArmorMeta);
        return itemStack;
    }

    @EventHandler
    public void onLost(PlayerLostSuperheroEvent e) {
        Superhero superhero = e.getHero();
        Collection<SkillData> skillDatas = superhero.getSkillData(Skill.getSkill("DECOY"));
        for (SkillData skillData : skillDatas) {
            removeArmorStand(e.getPlayer(), (DecoyData) skillData);
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        Superhero superhero = heroHandler.getSuperhero(e.getPlayer());
        Collection<SkillData> skillDatas = superhero.getSkillData(Skill.getSkill("DECOY"));
        for (SkillData skillData : skillDatas) {
            removeArmorStand(e.getPlayer(), (DecoyData) skillData);
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {
        new BukkitRunnable() {
            @Override
            public void run() {
                removeDecoys(e.getChunk());
            }
        }.runTaskLater(Superheroes2.getInstance(), 50L);
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent e) {
        new BukkitRunnable() {
            @Override
            public void run() {
                removeDecoys(e.getChunk());
            }
        }.runTaskLater(Superheroes2.getInstance(), 50L);
    }

    public void removeDecoys(Chunk chunk) {
        if (chunk.isEntitiesLoaded()) {
            Entity[] entities = chunk.getEntities();
            for (Entity entity : entities) {
                if (entity instanceof ArmorStand) {
                    if ("Decoy".equals(entity.getCustomName())) {
                        if (entity.getPersistentDataContainer().has(namespacedKey, PersistentDataType.INTEGER)) {
                            entity.remove();
                        }
                    }
                }
            }
        }
    }

}
