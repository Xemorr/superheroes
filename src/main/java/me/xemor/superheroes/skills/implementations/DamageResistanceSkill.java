package me.xemor.superheroes.skills.implementations;

import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.events.PlayerChangedSuperheroEvent;
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
        if (!(e.getEntity() instanceof Player player)) {
            return;
        }
        Superhero superhero = heroHandler.getSuperhero(player);
        Collection<SkillData> skillDatas = superhero.getSkillData("DAMAGERESISTANCE");
        for (SkillData skillData : skillDatas) {
            DamageResistanceData damageResistanceData = (DamageResistanceData) skillData;
            if (damageResistanceData.isValidDamageCause(e.getCause())) {
                damageResistanceData.ifConditionsTrue(() -> {
                    e.setDamage(e.getDamage() * damageResistanceData.getDamageMultiplier());
                    if (damageResistanceData.getDamageMultiplier() == 0) {
                        e.setCancelled(true);
                    }
                    damageResistanceData.getPotionEffect().ifPresent((potionEffect -> {
                        if (!player.hasPotionEffect(potionEffect.getType())) {
                            player.addPotionEffect(potionEffect);
                        }
                    }));
                }, player);
            }
        }
    }

    @EventHandler
    public void onPowerLoss(PlayerChangedSuperheroEvent e) {
        Player player = e.getPlayer();
        Superhero superhero = e.getOldHero();
        Collection<SkillData> skillDatas = superhero.getSkillData("DAMAGERESISTANCE");
        if (!skillDatas.isEmpty()) {
            for (SkillData skillData : skillDatas) {
                DamageResistanceData damageResistanceData = (DamageResistanceData) skillData;
                damageResistanceData.getPotionEffect().ifPresent(potionEffect -> player.removePotionEffect(potionEffect.getType()));
            }
        }
    }


}
