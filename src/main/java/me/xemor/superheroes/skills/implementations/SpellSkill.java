package me.xemor.superheroes.skills.implementations;

import com.fasterxml.jackson.databind.jsontype.NamedType;
import me.xemor.superheroes.SkillCooldownHandler;
import me.xemor.superheroes.Superheroes;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.skills.skilldata.SkillData;
import me.xemor.superheroes.skills.skilldata.spell.SpellData;
import me.xemor.superheroes.skills.skilldata.spell.Spells;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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

import java.util.Arrays;
import java.util.Collection;

public class SpellSkill extends SkillImplementation {

    final SkillCooldownHandler skillCooldownHandler = new SkillCooldownHandler();

    public SpellSkill(HeroHandler heroHandler) {
        super(heroHandler);
    }

    // Silly hack I have to do now I've switched to jackson
    public Collection<SkillData> getSkillDatas(Player player) {
        return Arrays
                .stream(Spells.getNamedTypes())
                .map(NamedType::getName)
                .flatMap((it) -> heroHandler.getSuperhero(player).getSkillData(it).stream())
                .toList();
    }

    @EventHandler
    public void bookWrite(PlayerEditBookEvent e) {
        if (e.isSigning()) {
            Player player = e.getPlayer();
            Collection<SkillData> skillDatas = getSkillDatas(player);
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
            Collection<SkillData> skillDatas = getSkillDatas(player);
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
                                    if (e.getClickedBlock() == null) {
                                        skillData.ifConditionsTrue(
                                            () -> handleSpells(player, spellData, e),
                                            player
                                        );
                                    }
                                    else skillData.ifConditionsTrue(
                                        () -> handleSpells(player, spellData, e),
                                        player,
                                        e.getClickedBlock().getLocation()
                                    );
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void handleSpells(Player player, SpellData spellData, PlayerInteractEvent e) {
        int cost = spellData.getCost();
        double cooldown = spellData.getCooldown();
        Material fuel = spellData.getFuel();
        if (player.getInventory().containsAtLeast(new ItemStack(spellData.getFuel()), cost)) {
            boolean success = spellData.castSpell(player, e.getClickedBlock(), e.getBlockFace());
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
