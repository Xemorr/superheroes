package me.xemor.superheroes2.skills.implementations;

import de.themoep.minedown.MineDown;
import me.xemor.superheroes2.HeroHandler;
import me.xemor.superheroes2.SkillCooldownHandler;
import me.xemor.superheroes2.skills.Skill;
import me.xemor.superheroes2.skills.skilldata.SkillData;
import me.xemor.superheroes2.skills.skilldata.Spell.Spell;
import me.xemor.superheroes2.skills.skilldata.Spell.SpellData;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.Collection;

public class SpellSkill extends SkillImplementation {

    SkillCooldownHandler skillCooldownHandler = new SkillCooldownHandler();

    public SpellSkill(HeroHandler heroHandler) {
        super(heroHandler);
    }

    @EventHandler
    public void bookWrite(PlayerEditBookEvent e) {
        if (e.isSigning()) {
            Player player = e.getPlayer();
            Collection<SkillData> skillDatas = heroHandler.getSuperhero(player).getSkillData(Skill.SPELL);
            for (SkillData skillData : skillDatas) {
                SpellData spellData = (SpellData) skillData;
                BookMeta bookMeta = e.getNewBookMeta();
                if (spellData.getSpellName().equalsIgnoreCase(bookMeta.getTitle())) {
                    bookMeta.setTitle(spellData.getSpellName());
                    bookMeta.setDisplayName(spellData.getDisplayName());
                    bookMeta.setLore(spellData.getLore());
                    bookMeta.getPersistentDataContainer().set(new NamespacedKey(heroHandler.getPlugin(), spellData.getSpellName()), PersistentDataType.INTEGER, 1);
                    e.setNewBookMeta(bookMeta);
                }
            }
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = e.getPlayer();
            Collection<SkillData> skillDatas = heroHandler.getSuperhero(player).getSkillData(Skill.SPELL);
            for (SkillData skillData : skillDatas) {
                SpellData spellData = (SpellData) skillData;
                ItemStack item = player.getInventory().getItemInMainHand();
                if (item.hasItemMeta()) {
                    ItemMeta itemMeta = item.getItemMeta();
                    if (itemMeta.hasDisplayName()) {
                        if (item.getType() == Material.WRITTEN_BOOK) {
                            e.setUseItemInHand(Event.Result.DENY);
                            String displayName = itemMeta.getDisplayName();
                            if (displayName.equals(spellData.getDisplayName())) {
                                if (skillCooldownHandler.isCooldownOver(spellData, player.getUniqueId())) {
                                    handleSpells(player, spellData, e);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void handleSpells(Player player, SpellData spellData, PlayerInteractEvent e) {
        Spell spell = spellData.getSpell();
        int cost = spellData.getCost();
        double cooldown = spellData.getCooldown();
        Material fuel = spellData.getFuel();
        if (player.getInventory().containsAtLeast(new ItemStack(spellData.getFuel()), cost)) {
            if (spell == Spell.FIREBALL) {
                player.launchProjectile(Fireball.class);
            }
            else if (spell == Spell.SNOWBALL) {
                player.launchProjectile(Snowball.class);
            }
            else if (spell == Spell.ARROW) {
                SpectralArrow arrow = player.launchProjectile(SpectralArrow.class);
                arrow.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
            }
            else if (spell == Spell.LIGHTNING) {
                strikeLightning(player, 30);
            }
            else if (spell == Spell.EGG) {
                player.launchProjectile(Egg.class);
            }
            else if (spell == Spell.EXPLOSION) {
                explosionSpell(player);
            }
            else if (spell == Spell.TRIDENT) {
                Trident trident = player.launchProjectile(Trident.class);
                trident.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
            }
            else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Block clickedBlock = e.getClickedBlock();
                Block placeHere = clickedBlock.getRelative(e.getBlockFace());
                if (spell == Spell.WATER) {
                    placeHere.setType(Material.WATER);
                }
                else if (spell == Spell.LAVA) {
                    placeHere.setType(Material.LAVA);
                }
                else if (spell == Spell.FIRE) {
                    placeHere.setType(Material.FIRE);
                }
                else if (spell == Spell.TRANSMUTATION) {
                    if (spellData.getTransmutationData().getTransmutatableBlocks().contains(clickedBlock.getType())) {
                        clickedBlock.setType(spellData.getTransmutationData().getResultantBlock());
                    }
                }
            }
            else {
                cost = 0;
                cooldown = 0;
            }
            payUpThen(player, cost, fuel);
            skillCooldownHandler.startCooldown(spellData, cooldown, player.getUniqueId());
        }
        else {
            MineDown getMoreFuel = new MineDown(spellData.getMoreFuelMessage()).replace("fuelneeded", new TextComponent(String.valueOf(cost)));
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, getMoreFuel.toComponent());
        }
    }

    public void payUpThen(Player player, int cost, Material fuel) {
        int paidFor = 0;
        for (ItemStack item : player.getInventory()) {
            if (item == null) {
                continue;
            }
            if (item.getType() == fuel) {
                if (item.getAmount() >= (cost - paidFor)) {
                    item.setAmount(item.getAmount() - (cost - paidFor));
                    break;
                }
                else {
                    paidFor += item.getAmount();
                    item.setType(Material.AIR);
                }
            }
        }
    }

    private void strikeLightning(Player player, int blocksToTravel) {
        Location location = findLookingLocation(player, blocksToTravel);
        player.getWorld().strikeLightning(location);
    }

    private void explosionSpell(Player player) {
        TNTPrimed tnt = (TNTPrimed) player.getWorld().spawnEntity(player.getEyeLocation(), EntityType.PRIMED_TNT);
        tnt.setFuseTicks(50);
        tnt.setVelocity(player.getEyeLocation().getDirection().multiply(1.4));
    }

    private Location findLookingLocation(Player player, int blocksToTravel) {
        World world = player.getWorld();
        Location eyeLoc = player.getEyeLocation().clone();
        Vector travelVector = eyeLoc.getDirection();
        RayTraceResult rayTraceResult = world.rayTraceBlocks(eyeLoc, travelVector, blocksToTravel);
        Vector hitPosition;
        if (rayTraceResult == null) {
            hitPosition = eyeLoc.toVector().add(travelVector.multiply(blocksToTravel));
        } else {
            rayTraceResult.getHitPosition();
            hitPosition = rayTraceResult.getHitPosition();
        }
        return new Location(world, hitPosition.getX(), hitPosition.getY(), hitPosition.getZ());
    }
}
