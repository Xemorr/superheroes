package me.xemor.superheroes.skills.implementations;

import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.skills.Skill;
import me.xemor.superheroes.skills.skilldata.SkillData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import me.xemor.superheroes.skills.skilldata.DamageModifierData;

import java.util.Collection;

public class DamageModifierSkill extends SkillImplementation {
    public DamageModifierSkill(HeroHandler heroHandler) {
        super(heroHandler);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        double offence = event.getDamage();
        double offencePrioity = Integer.MIN_VALUE;

        double defence = event.getDamage();
        double defencePriority = Integer.MIN_VALUE;

        Entity damager = event.getDamager();
        if (damager instanceof Projectile proj) {
            if (proj.getShooter() instanceof Player shooter) {
                damager = shooter;
            };
        }

        if (damager instanceof Player damagerPlayer) {

            Collection<SkillData> skillDataCollection =
                heroHandler.getSuperhero(damagerPlayer).getSkillData(
                    Skill.getSkill("DAMAGEMODIFIER"));

            for (SkillData skillData : skillDataCollection) {
                DamageModifierData damageModifierData = (DamageModifierData) skillData;
                if (!damageModifierData.limitProjectiles() && event.getDamager() instanceof Projectile) {
                    continue;
                }
                if (damageModifierData.isOutgoing() &&
                    damageModifierData.isValidEntity(event.getEntity().getType())) {
                    if (damageModifierData.getPriority() > offencePrioity) {
                        offence = damageModifierData.calculateDamage(event.getDamage());
                        offencePrioity = damageModifierData.getPriority();
                    }
                }
            }
        }

        if (event.getEntity() instanceof Player damagedPlayer) {
            Collection<SkillData> skillDataCollection =
                heroHandler.getSuperhero(damagedPlayer).getSkillData(
                    Skill.getSkill("DAMAGEMODIFIER"));

            for (SkillData skillData : skillDataCollection) {
                DamageModifierData damageModifierData = (DamageModifierData) skillData;
                if (!damageModifierData.limitProjectiles() && event.getDamager() instanceof Projectile) {
                    continue;
                }
                if (damageModifierData.isIncoming() &&
                    damageModifierData.isValidEntity(damager.getType())) {
                    if (damageModifierData.getPriority() > offencePrioity) {
                        defence = damageModifierData.calculateDamage(event.getDamage());
                        defencePriority = damageModifierData.getPriority();
                    }
                }
            }
        }

        if (defencePriority >= offencePrioity) {
            event.setDamage(defence);
        } else {
            event.setDamage(offence);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {

            Collection<SkillData> skillDataCollection =
                heroHandler.getSuperhero(player).getSkillData(
                    Skill.getSkill("DAMAGEMODIFIER"));


            for (SkillData skillData : skillDataCollection) {
                DamageModifierData damageModifierData =
                    (DamageModifierData) skillData;

                if (damageModifierData.isValidCause(event.getCause())) {
                    double newDamage = damageModifierData.calculateDamage(event.getDamage());
                    event.setDamage(newDamage);
                }
            }
        }
    }
}