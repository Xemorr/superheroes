package me.xemor.superheroes.skills.implementations;

import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.events.PlayerChangedSuperheroEvent;
import me.xemor.superheroes.skills.Skill;
import me.xemor.superheroes.skills.skilldata.DamagePotionData;
import me.xemor.superheroes.skills.skilldata.DamageResistanceData;
import me.xemor.superheroes.skills.skilldata.SkillData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Collection;

public class DamagePotionSkill extends SkillImplementation {

    public DamagePotionSkill(HeroHandler heroHandler) {
        super(heroHandler);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player player)) {
            return;
        }
        Superhero superhero = heroHandler.getSuperhero(player);
        Collection<SkillData> skillDatas = superhero.getSkillData(Skill.getSkill("DAMAGEPOTION"));
        for (SkillData skillData : skillDatas) {
            DamagePotionData damagePotionData = (DamagePotionData) skillData;
            if (damagePotionData.getDamageCause() == null || damagePotionData.getDamageCause().contains(e.getCause())) {
                damagePotionData.getPotionEffect().ifPresent(potionEffect -> {
                    if (!player.hasPotionEffect(potionEffect.getType())) {
                        damagePotionData.ifConditionsTrue(() -> player.addPotionEffect(potionEffect), player);
                    }
                });
            }
        }
    }

    @EventHandler
    public void onPowerLoss(PlayerChangedSuperheroEvent e) {
        Player player = e.getPlayer();
        Superhero superhero = e.getOldHero();
        Collection<SkillData> skillDatas = superhero.getSkillData(Skill.getSkill("DAMAGEPOTION"));
        if (!skillDatas.isEmpty()) {
            for (SkillData skillData : skillDatas) {
                DamagePotionData damagePotionData = (DamagePotionData) skillData;
                damagePotionData.getPotionEffect().ifPresent(potionEffect -> player.removePotionEffect(potionEffect.getType()));
            }
        }
    }


}