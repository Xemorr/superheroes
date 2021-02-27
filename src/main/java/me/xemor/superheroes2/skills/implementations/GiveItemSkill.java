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
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
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
// This collection of functions handles preventing items from being stored.
    @EventHandler
    public void itemStored(InventoryClickEvent e) {
        if (!(e.getClickedInventory() instanceof PlayerInventory)) {
            if (e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                return;
            }
            if (e.getCursor() == null) {
                return;
            }
            Entity possiblePlayer = e.getViewers().get(0);
            if (!(possiblePlayer instanceof Player)) {
                return;
            }
            Player player = (Player) possiblePlayer;
            System.out.println(player.getName());
            Collection<SkillData> skillDatas = powersHandler.getSuperhero(player).getSkillData(Skill.GIVEITEM);
            for (SkillData skillData : skillDatas) {
                GiveItemData giveItemData = (GiveItemData) skillData;
                if (!giveItemData.canStore()) {
                    ItemStack item = giveItemData.getItemStackData().getItem();
                    if (e.getCursor().isSimilar(item)) {
                        e.setResult(Event.Result.DENY);
                    }
                }
            }
        }
    }

    @EventHandler
    public void itemShiftClicked(InventoryClickEvent e) {
        if (!(e.getInventory() instanceof PlayerInventory)) {
            if (e.getAction() != InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                return;
            }
            if (e.getCurrentItem() == null) {
                return;
            }
            Entity possiblePlayer = e.getViewers().get(0);
            if (!(possiblePlayer instanceof Player)) {
                return;
            }
            Player player = (Player) possiblePlayer;
            Collection<SkillData> skillDatas = powersHandler.getSuperhero(player).getSkillData(Skill.GIVEITEM);
            for (SkillData skillData : skillDatas) {
                GiveItemData giveItemData = (GiveItemData) skillData;
                if (!giveItemData.canStore()) {
                    ItemStack item = giveItemData.getItemStackData().getItem();
                    if (e.getCurrentItem().isSimilar(item)) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (!(e.getInventory() instanceof PlayerInventory)) {
            Entity possiblePlayer = e.getViewers().get(0);
            if (!(possiblePlayer instanceof Player)) {
                return;
            }
            Player player = (Player) possiblePlayer;
            Collection<SkillData> skillDatas = powersHandler.getSuperhero(player).getSkillData(Skill.GIVEITEM);
            for (SkillData skillData : skillDatas) {
                GiveItemData giveItemData = (GiveItemData) skillData;
                if (!giveItemData.canStore()) {
                    ItemStack item = giveItemData.getItemStackData().getItem();
                    if (e.getOldCursor().isSimilar(item)) {
                        e.setResult(Event.Result.DENY);
                    }
                }
            }
        }
    }
//End of Functions

    @EventHandler
    public void onDeath(PlayerRespawnEvent e) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Player player = e.getPlayer();
                Collection<SkillData> skillDatas = powersHandler.getSuperhero(player).getSkillData(Skill.GIVEITEM);
                for (SkillData skillData : skillDatas) {
                    GiveItemData giveItemData = (GiveItemData) skillData;
                    if (!giveItemData.canLoseOnDeath()) {
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
