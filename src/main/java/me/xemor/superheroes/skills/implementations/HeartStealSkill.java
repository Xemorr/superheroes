package me.xemor.superheroes.skills.implementations;

import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.Superheroes;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.events.PlayerChangedSuperheroEvent;
import me.xemor.superheroes.skills.Skill;
import me.xemor.superheroes.skills.skilldata.HeartStealData;
import me.xemor.superheroes.skills.skilldata.SkillData;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.EquipmentSlotGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class HeartStealSkill extends SkillImplementation {
    public HeartStealSkill(HeroHandler heroHandler) {
        super(heroHandler);
    }

    @EventHandler
    public void onKill(EntityDeathEvent e) {
        if (e.getEntity() instanceof Player player) {
            Superhero superhero = heroHandler.getSuperhero(player);
            removeExtraHearts(player, superhero);
        }
        Player player = e.getEntity().getKiller();
        if (player == null) return;
        Superhero superhero = heroHandler.getSuperhero(player);
        Collection<SkillData> skillDatas = superhero.getSkillData("HEARTSTEAL");
        for (SkillData skillData : skillDatas) {
            HeartStealData heartStealData = (HeartStealData) skillData;
            if (!heartStealData.getEntities().inSet(e.getEntityType())) return;
            heartStealData.ifConditionsTrue(() -> {
                AttributeInstance maxHealth = player.getAttribute(Attribute.MAX_HEALTH);
                if (maxHealth.getValue() >= heartStealData.getMaxHearts()) {
                    return;
                }
                maxHealth.addModifier(new AttributeModifier(new NamespacedKey(Superheroes.getInstance(), "Superheroes-HeartSteal" + UUID.randomUUID()), heartStealData.getHeartsGained(), AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.ANY));
            }, player, e.getEntity());
        }
    }

    public void removeExtraHearts(Player player, Superhero superhero) {
        Collection<SkillData> skillDatas = superhero.getSkillData("HEARTSTEAL");
        if (!skillDatas.isEmpty()) {
            AttributeInstance maxHealth = player.getAttribute(Attribute.MAX_HEALTH);
            List<AttributeModifier> toRemove = new ArrayList<>();
            for (AttributeModifier instance : maxHealth.getModifiers()) {
                if (instance.getName().contains("superheroes-heartsteal")) {
                    toRemove.add(instance);
                } else if (instance.getKey().getNamespace().equalsIgnoreCase("minecraft") && instance.getKey().getKey().matches(
                        "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"
                )) {
                    toRemove.add(instance);
                }
            }
            for (AttributeModifier remove : toRemove) {
                maxHealth.removeModifier(remove);
            }
            if (player.getHealth() > maxHealth.getValue()) {
                player.setHealth(maxHealth.getValue());
            }
        }
    }

    @EventHandler
    public void onSkillLoss(PlayerChangedSuperheroEvent e) {
        removeExtraHearts(e.getPlayer(), e.getOldHero());
    }
}
