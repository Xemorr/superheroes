package me.xemor.superheroes.skills.implementations;

import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.skills.Skill;
import me.xemor.superheroes.skills.skilldata.OHKOData;
import me.xemor.superheroes.skills.skilldata.SkillData;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Collection;

public class OHKOSkill extends SkillImplementation {
    public OHKOSkill(HeroHandler heroHandler) {
        super(heroHandler);
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player player && e.getEntity() instanceof LivingEntity) {
            Superhero superhero = heroHandler.getSuperhero(player);
            Collection<SkillData> skillDatas = superhero.getSkillData(Skill.getSkill("OHKO"));
            for (SkillData skillData : skillDatas) {
                OHKOData ohkoData = (OHKOData) skillData;
                LivingEntity livingEntity = (LivingEntity) e.getEntity();
                if (ohkoData.getDisplayName() == null || ohkoData.getDisplayName().equals(livingEntity.getCustomName())) {
                    if (ohkoData.getEntityTypes().contains(e.getEntity().getType())) {
                        skillData.ifConditionsTrue(
                                () -> e.setDamage(livingEntity.getHealth() * 100),
                                player,
                                livingEntity
                        );
                    }
                }
            }
        }
    }

}
