package me.xemor.superheroes.skills.implementations;

import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.events.PlayerChangedSuperheroEvent;
import me.xemor.superheroes.skills.Skill;
import me.xemor.superheroes.skills.skilldata.HeartStealData;
import me.xemor.superheroes.skills.skilldata.SkillData;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;

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
        Collection<SkillData> skillDatas = superhero.getSkillData(Skill.getSkill("HEARTSTEAL"));
        for (SkillData skillData : skillDatas) {
            HeartStealData heartStealData = (HeartStealData) skillData;
            if (!heartStealData.getEntities().inSet(e.getEntityType())) return;
            if (!heartStealData.areConditionsTrue(player, e.getEntity())) return;
            AttributeInstance maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            if (maxHealth.getValue() >= heartStealData.getMaxHearts()) {
                return;
            }
            maxHealth.addModifier(new AttributeModifier(UUID.randomUUID(), "Superheroes-HeartSteal", heartStealData.getHeartsGained(), AttributeModifier.Operation.ADD_NUMBER));
        }
    }

    public void removeExtraHearts(Player player, Superhero superhero) {
        Collection<SkillData> skillDatas = superhero.getSkillData(Skill.getSkill("HEARTSTEAL"));
        if (!skillDatas.isEmpty()) {
            AttributeInstance maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            List<AttributeModifier> toRemove = new ArrayList<>();
            for (AttributeModifier instance : maxHealth.getModifiers()) {
                if ("Superheroes-HeartSteal".equals(instance.getName())) {
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
