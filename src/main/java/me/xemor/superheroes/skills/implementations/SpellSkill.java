package me.xemor.superheroes.skills.implementations;

import me.xemor.superheroes.SkillCooldownHandler;
import me.xemor.superheroes.Superheroes;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.skills.Skill;
import me.xemor.superheroes.skills.skilldata.SkillData;
import me.xemor.superheroes.skills.skilldata.spell.Spell;
import me.xemor.superheroes.skills.skilldata.SpellData;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
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
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.function.Consumer;

public class SpellSkill extends SkillImplementation {

    final SkillCooldownHandler skillCooldownHandler = new SkillCooldownHandler();

    public SpellSkill(HeroHandler heroHandler) {
        super(heroHandler);
    }

    @EventHandler
    public void bookWrite(PlayerEditBookEvent e) {
        if (e.isSigning()) {
            Player player = e.getPlayer();
            Collection<SkillData> skillDatas = heroHandler.getSuperhero(player).getSkillData(Skill.getSkill("SPELL"));
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
            Collection<SkillData> skillDatas = heroHandler.getSuperhero(player).getSkillData(Skill.getSkill("SPELL"));
            for (SkillData skillData : skillDatas) {
                SpellData spellData = (SpellData) skillData;
                ItemStack item = player.getInventory().getItemInMainHand();
                if (item.hasItemMeta()) {
                    ItemMeta itemMeta = item.getItemMeta();
                    if (itemMeta == null) {
                        return;
                    }
                    if (itemMeta.hasDisplayName()) {
                        if (item.getType() == Material.WRITTEN_BOOK) {
                            String displayName = itemMeta.getDisplayName();
                            if (displayName.equals(spellData.getDisplayName())) {
                                e.setUseItemInHand(Event.Result.DENY);
                                if (skillCooldownHandler.isCooldownOver(spellData, player.getUniqueId())) {
                                    if (((e.getClickedBlock() == null && skillData.areConditionsTrue(player))) || skillData.areConditionsTrue(player, e.getClickedBlock().getLocation())) {
                                        handleSpells(player, spellData, e);
                                    }
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
            boolean success = spell.castSpell(player, e.getClickedBlock(), e.getBlockFace());
            if (success) {
                useFuel(player, cost, fuel);
                skillCooldownHandler.startCooldown(spellData, cooldown, player.getUniqueId());
            }
        }
        else {
            Component moreFuelNeeded = MiniMessage.miniMessage().deserialize(spellData.getMoreFuelMessage(), Placeholder.unparsed("fuel", fuel.name()), Placeholder.unparsed("fuelneeded", String.valueOf(cost)));
            Audience playerAudience = Superheroes.getBukkitAudiences().player(player);
            playerAudience.sendActionBar(moreFuelNeeded);
        }
    }

    public void useFuel(Player player, int cost, Material fuel) {
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
}
