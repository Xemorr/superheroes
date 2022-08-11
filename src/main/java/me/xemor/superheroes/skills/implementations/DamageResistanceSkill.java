package me.xemor.superheroes.skills.implementations;

import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.events.PlayerLostSuperheroEvent;
import me.xemor.superheroes.skills.Skill;
import me.xemor.superheroes.skills.skilldata.DamageResistanceData;
import me.xemor.superheroes.skills.skilldata.SkillData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Collection;

public class DamageResistanceSkill extends SkillImplementation {

    public DamageResistanceSkill(HeroHandler heroHandler) {
        super(heroHandler);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) e.getEntity();
        Superhero superhero = heroHandler.getSuperhero(player);
        Collection<SkillData> skillDatas = superhero.getSkillData(Skill.getSkill("DAMAGERESISTANCE"));
        for (SkillData skillData : skillDatas) {
            DamageResistanceData damageResistanceData = (DamageResistanceData) skillData;
            if (damageResistanceData.getDamageCause() == null || damageResistanceData.getDamageCause().contains(e.getCause())) {
                e.setDamage(e.getDamage() * damageResistanceData.getDamageMultiplier());
                if (damageResistanceData.getDamageMultiplier() == 0) {
                    e.setCancelled(true);
                }
                if (damageResistanceData.getPotionEffect() != null) {
                    if (!player.hasPotionEffect(damageResistanceData.getPotionEffect().getType())) {
                        if (damageResistanceData.areConditionsTrue(player)) {
                            player.addPotionEffect(damageResistanceData.getPotionEffect());
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPowerLoss(PlayerLostSuperheroEvent e) {
        Player player = e.getPlayer();
        Superhero superhero = heroHandler.getSuperhero(player);
        Collection<SkillData> skillDatas = superhero.getSkillData(Skill.getSkill("DAMAGERESISTANCE"));
        if (!skillDatas.isEmpty()) {
            for (SkillData skillData : skillDatas) {
                DamageResistanceData damageResistanceData = (DamageResistanceData) skillData;
                if (damageResistanceData.getPotionEffect() != null) {
                    player.removePotionEffect(damageResistanceData.getPotionEffect().getType());
                }
            }
        }
    }


}
