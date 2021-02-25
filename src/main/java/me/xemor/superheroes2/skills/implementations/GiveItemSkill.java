package me.xemor.superheroes2.skills.implementations;

import me.xemor.superheroes2.PowersHandler;
import me.xemor.superheroes2.Superheroes2;
import me.xemor.superheroes2.events.PlayerGainedSuperheroEvent;
import me.xemor.superheroes2.events.PlayerLostSuperheroEvent;
import me.xemor.superheroes2.skills.Skill;
import me.xemor.superheroes2.skills.skilldata.GiveItemData;
import me.xemor.superheroes2.skills.skilldata.SkillData;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.HashMap;

public class GiveItemSkill extends SkillImplementation {

    public GiveItemSkill(PowersHandler powersHandler) {
        super(powersHandler);
    }

    @EventHandler
    public void heroGained(PlayerGainedSuperheroEvent e) {
        Collection<SkillData> skillDatas = e.getHero().getSkillData(Skill.GIVEITEM);
        for (SkillData skillData : skillDatas) {
            GiveItemData giveItemData = (GiveItemData) skillData;
            HashMap<Integer, ItemStack> leftovers = e.getPlayer().getInventory().addItem(giveItemData.getItemStackData().getItem());
            World world = e.getPlayer().getWorld();
            Location location = e.getPlayer().getLocation();
            for (ItemStack items : leftovers.values()) {
                world.dropItem(location, items);
            }
        }
    }

    @EventHandler
    public void heroLoss(PlayerLostSuperheroEvent e) {
        Collection<SkillData> skillDatas = e.getHero().getSkillData(Skill.GIVEITEM);
        for (SkillData skillData : skillDatas) {
            GiveItemData giveItemData = (GiveItemData) skillData;
            if (giveItemData.canLoseItemOnHeroLoss()) {
                e.getPlayer().getInventory().remove(giveItemData.getItemStackData().getItem());
            }
        }
    }

    @EventHandler
    public void itemDropped(PlayerDropItemEvent e) {
        Player player = e.getPlayer();
        Collection<SkillData> skillDatas = powersHandler.getSuperhero(player).getSkillData(Skill.GIVEITEM);
        for (SkillData skillData : skillDatas) {
            GiveItemData giveItemData = (GiveItemData) skillData;
            if (!giveItemData.canDrop()) {
                if (e.getItemDrop().getItemStack().isSimilar(giveItemData.getItemStackData().getItem())) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void itemStored(InventoryMoveItemEvent e) {
        Entity possiblePlayer = e.getInitiator().getViewers().get(0);
        if (!(possiblePlayer instanceof Player)) {
            return;
        }
        Player player = (Player) possiblePlayer;
        Collection<SkillData> skillDatas = powersHandler.getSuperhero(player).getSkillData(Skill.GIVEITEM);
        for (SkillData skillData : skillDatas) {
            GiveItemData giveItemData = (GiveItemData) skillData;
            if (!giveItemData.canStore()) {
                if (e.getItem().isSimilar(giveItemData.getItemStackData().getItem())) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerRespawnEvent e) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Player player = e.getPlayer();
                Collection<SkillData> skillDatas = powersHandler.getSuperhero(player).getSkillData(Skill.GIVEITEM);
                for (SkillData skillData : skillDatas) {
                    GiveItemData giveItemData = (GiveItemData) skillData;
                    if (giveItemData.canDropOnDeath()) {
                        HashMap<Integer, ItemStack> leftovers = player.getInventory().addItem(giveItemData.getItemStackData().getItem());
                        World world = e.getPlayer().getWorld();
                        Location location = e.getPlayer().getLocation();
                        for (ItemStack items : leftovers.values()) {
                            world.dropItem(location, items);
                        }
                    }
                }
            }
        }.runTaskLater(JavaPlugin.getPlugin(Superheroes2.class), 1L);
    }

}
