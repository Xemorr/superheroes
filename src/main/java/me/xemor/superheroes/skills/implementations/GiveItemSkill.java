/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.ItemFrame
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event$Result
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.entity.PlayerDeathEvent
 *  org.bukkit.event.inventory.InventoryAction
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.inventory.InventoryDragEvent
 *  org.bukkit.event.player.PlayerArmorStandManipulateEvent
 *  org.bukkit.event.player.PlayerDropItemEvent
 *  org.bukkit.event.player.PlayerInteractEntityEvent
 *  org.bukkit.event.player.PlayerRespawnEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 *  org.bukkit.metadata.FixedMetadataValue
 *  org.bukkit.metadata.MetadataValue
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.scheduler.BukkitRunnable
 */
package me.xemor.superheroes.skills.implementations;

import me.xemor.superheroes.Superheroes;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.events.PlayerChangedSuperheroEvent;
import me.xemor.superheroes.skills.Skill;
import me.xemor.superheroes.skills.skilldata.GiveItemData;
import me.xemor.superheroes.skills.skilldata.SkillData;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.HashMap;

public class GiveItemSkill
        extends SkillImplementation {
    public GiveItemSkill(HeroHandler heroHandler) {
        super(heroHandler);
    }

    @EventHandler
    public void heroGained(PlayerChangedSuperheroEvent e) {
        Collection<SkillData> skillDatas = e.getNewHero().getSkillData(Skill.getSkill("GIVEITEM"));
        for (SkillData skillData : skillDatas) {
            GiveItemData giveItemData = (GiveItemData) skillData;
            HashMap<Integer, ItemStack> leftovers = e.getPlayer().getInventory().addItem(new ItemStack[]{giveItemData.getItemStackData().getItem()});
            World world = e.getPlayer().getWorld();
            Location location = e.getPlayer().getLocation();
            if (!skillData.areConditionsTrue(e.getPlayer())) continue;
            for (ItemStack items : leftovers.values()) {
                world.dropItem(location, items);
            }
        }
    }

    @EventHandler
    public void heroLoss(PlayerChangedSuperheroEvent e) {
        Collection<SkillData> skillDatas = e.getOldHero().getSkillData(Skill.getSkill("GIVEITEM"));
        for (SkillData skillData : skillDatas) {
            GiveItemData giveItemData = (GiveItemData) skillData;
            if (!giveItemData.canLoseItemOnHeroLoss()) continue;
            this.removeItem(e.getPlayer(), giveItemData);
        }
    }

    private void removeItem(Player player, GiveItemData giveItemData) {
        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length; ++i) {
            if (!giveItemData.getItemStackData().getItem().isSimilar(contents[i])) continue;
            contents[i] = null;
        }
        player.getInventory().setContents(contents);
    }

    @EventHandler
    public void itemDropped(PlayerDropItemEvent e) {
        Player player = e.getPlayer();
        Collection<SkillData> skillDatas = this.heroHandler.getSuperhero(player).getSkillData(Skill.getSkill("GIVEITEM"));
        for (SkillData skillData : skillDatas) {
            GiveItemData giveItemData = (GiveItemData) skillData;
            if (giveItemData.canDrop() || !e.getItemDrop().getItemStack().isSimilar(giveItemData.getItemStackData().getItem()))
                continue;
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void itemStored(InventoryClickEvent e) {
        if (!(e.getClickedInventory() instanceof PlayerInventory)) {
            if (e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                return;
            }
            if (e.getCursor() == null) {
                return;
            }
            if (e.getViewers().size() == 0) {
                return;
            }
            Entity possiblePlayer = (Entity) e.getViewers().get(0);
            if (!(possiblePlayer instanceof Player)) {
                return;
            }
            Player player = (Player) possiblePlayer;
            Collection<SkillData> skillDatas = this.heroHandler.getSuperhero(player).getSkillData(Skill.getSkill("GIVEITEM"));
            for (SkillData skillData : skillDatas) {
                GiveItemData giveItemData = (GiveItemData) skillData;
                if (giveItemData.canStore()) continue;
                ItemStack item = giveItemData.getItemStackData().getItem();
                if (!e.getCursor().isSimilar(item)) continue;
                e.setResult(Event.Result.DENY);
            }
        }
    }

    @EventHandler
    public void itemSwapped(InventoryClickEvent e) {
        if (!(e.getClickedInventory() instanceof PlayerInventory) && e.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD) {
            if (e.getViewers().size() == 0) {
                return;
            }
            Entity possiblePlayer = (Entity) e.getViewers().get(0);
            if (!(possiblePlayer instanceof Player)) {
                return;
            }
            Player player = (Player) possiblePlayer;
            int hotbarSlot = e.getHotbarButton();
            ItemStack fromItem = e.getView().getBottomInventory().getItem(hotbarSlot);
            Collection<SkillData> skillDatas = this.heroHandler.getSuperhero(player).getSkillData(Skill.getSkill("GIVEITEM"));
            for (SkillData skillData : skillDatas) {
                ItemStack item;
                GiveItemData giveItemData = (GiveItemData) skillData;
                if (giveItemData.canStore() || !fromItem.isSimilar(item = giveItemData.getItemStackData().getItem()))
                    continue;
                e.setResult(Event.Result.DENY);
            }
        }
    }

    @EventHandler
    public void itemFrameStore(PlayerInteractEntityEvent e) {
        Player player = e.getPlayer();
        if (e.getRightClicked() instanceof ItemFrame) {
            Collection<SkillData> skillDatas = this.heroHandler.getSuperhero(player).getSkillData(Skill.getSkill("GIVEITEM"));
            for (SkillData skillData : skillDatas) {
                GiveItemData giveItemData = (GiveItemData) skillData;
                if (giveItemData.canStore()) continue;
                ItemStack item = giveItemData.getItemStackData().getItem();
                ItemStack playerItem = player.getInventory().getItem(e.getHand());
                if (!playerItem.isSimilar(item)) continue;
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void armorStandStore(PlayerArmorStandManipulateEvent e) {
        Player player = e.getPlayer();
        Collection<SkillData> skillDatas = this.heroHandler.getSuperhero(player).getSkillData(Skill.getSkill("GIVEITEM"));
        for (SkillData skillData : skillDatas) {
            GiveItemData giveItemData = (GiveItemData) skillData;
            if (giveItemData.canStore()) continue;
            ItemStack item = giveItemData.getItemStackData().getItem();
            ItemStack playerItem = e.getPlayerItem();
            if (!playerItem.isSimilar(item)) continue;
            e.setCancelled(true);
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
            if (e.getViewers().isEmpty()) {
                return;
            }
            Entity possiblePlayer = (Entity) e.getViewers().get(0);
            if (!(possiblePlayer instanceof Player)) {
                return;
            }
            Player player = (Player) possiblePlayer;
            Collection<SkillData> skillDatas = this.heroHandler.getSuperhero(player).getSkillData(Skill.getSkill("GIVEITEM"));
            for (SkillData skillData : skillDatas) {
                GiveItemData giveItemData = (GiveItemData) skillData;
                if (giveItemData.canStore()) continue;
                ItemStack item = giveItemData.getItemStackData().getItem();
                if (!e.getCurrentItem().isSimilar(item)) continue;
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (!(e.getInventory() instanceof PlayerInventory)) {
            if (e.getViewers().size() == 0) {
                return;
            }
            Entity possiblePlayer = (Entity) e.getViewers().get(0);
            if (!(possiblePlayer instanceof Player)) {
                return;
            }
            Player player = (Player) possiblePlayer;
            Collection<SkillData> skillDatas = this.heroHandler.getSuperhero(player).getSkillData(Skill.getSkill("GIVEITEM"));
            for (SkillData skillData : skillDatas) {
                GiveItemData giveItemData = (GiveItemData) skillData;
                if (giveItemData.canStore()) continue;
                ItemStack item = giveItemData.getItemStackData().getItem();
                if (!e.getOldCursor().isSimilar(item)) continue;
                e.setResult(Event.Result.DENY);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent e) {
        if (!e.getKeepInventory()) {
            e.getEntity().setMetadata("superheroes-giveitems", (MetadataValue) new FixedMetadataValue((Plugin) Superheroes.getInstance(), (Object) true));
        }
    }

    @EventHandler
    public void onRespawn(final PlayerRespawnEvent e) {
        if (e.getPlayer().hasMetadata("superheroes-giveitems")) {
            new BukkitRunnable() {

                public void run() {
                    Player player = e.getPlayer();
                    Collection<SkillData> skillDatas = GiveItemSkill.this.heroHandler.getSuperhero(player).getSkillData(Skill.getSkill("GIVEITEM"));
                    for (SkillData skillData : skillDatas) {
                        GiveItemData giveItemData = (GiveItemData) skillData;
                        if (giveItemData.canLoseOnDeath()) continue;
                        HashMap<Integer, ItemStack> leftovers = player.getInventory().addItem(giveItemData.getItemStackData().getItem());
                        World world = e.getPlayer().getWorld();
                        Location location = e.getPlayer().getLocation();
                        for (ItemStack items : leftovers.values()) {
                            world.dropItem(location, items);
                        }
                    }
                }
            }.runTaskLater((Plugin) JavaPlugin.getPlugin(Superheroes.class), 1L);
            e.getPlayer().removeMetadata("superheroes-giveitems", (Plugin) Superheroes.getInstance());
        }
    }
}

